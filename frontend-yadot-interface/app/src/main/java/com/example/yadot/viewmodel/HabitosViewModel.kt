package com.example.yadot.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yadot.network.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import com.example.yadot.screen.HabitoDiaAnterior
import com.example.yadot.screen.CategoriaHabito
import com.example.yadot.screen.ProgressoCategoria
import com.example.yadot.screen.DiaAnteriorUiState


interface EstrategiaMsg {
    fun gerar(progresso: Int): String
}

class EstrategiaMsgPadrao : EstrategiaMsg {
    override fun gerar(progresso: Int) = when {
        progresso == 100 -> "Dia perfeito! Você arrasou! 🔥"
        progresso >= 80  -> "Dia super produtivo! O ritmo está excelente."
        progresso >= 50  -> "Bom progresso! Mais da metade concluída."
        progresso > 0    -> "Começou bem, amanhã vai melhor!"
        else             -> "Nenhuma tarefa registrada neste dia."
    }
}

data class RankingItem(val nome: String, val percentual: Int, val ofensivaDias: Int)

data class HabitosUiState(
    val carregando: Boolean = false,
    val erro: String? = null,
    val usuarioLogado: UsuarioResponse? = null,
    val habitosDeHoje: List<HabitoResponse> = emptyList(),   // será removido, mas mantenha compatibilidade
    val progressoHoje: ProgressoResponse? = null,
    val ranking: List<RankingItem> = emptyList()
)

data class HabitoDisplay(val id: Long, val nome: String, val icone: String, val concluido: Boolean)

@RequiresApi(Build.VERSION_CODES.O)
class HabitosViewModel(
    private val estrategiaMsg: EstrategiaMsg = EstrategiaMsgPadrao()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitosUiState())
    val uiState: StateFlow<HabitosUiState> = _uiState

    val diasDaSemana = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
    private val indexDeHoje get() = LocalDate.now().dayOfWeek.value - 1

    var diaSelecionado by mutableStateOf(diasDaSemana[indexDeHoje])
        private set

    var mostrarModal by mutableStateOf(false)
        private set

    var modoEdicao by mutableStateOf(false)
        private set

    val categorias = listOf("EDUCACAO", "SAUDE", "TRABALHO", "ESTUDOS", "RESPONSABILIDADES", "FINANCAS", "CASA", "LAZER")
    val categoriasDisplay = mapOf(
        "EDUCACAO" to "Educação", "SAUDE" to "Saúde", "TRABALHO" to "Trabalho",
        "ESTUDOS" to "Estudos", "RESPONSABILIDADES" to "Responsabilidades",
        "FINANCAS" to "Finanças", "CASA" to "Casa", "LAZER" to "Lazer"
    )

    val iconesDisponiveis: List<Pair<String, ImageVector>> = listOf(
        "Star" to Icons.Filled.Star, "AttachMoney" to Icons.Filled.AttachMoney,
        "FitnessCenter" to Icons.Filled.FitnessCenter, "Book" to Icons.Filled.Book,
        "Home" to Icons.Filled.Home, "Work" to Icons.Filled.Work,
        "ShoppingCart" to Icons.Filled.ShoppingCart, "Favorite" to Icons.Filled.Favorite,
        "School" to Icons.Filled.School, "Code" to Icons.Filled.Code,
        "MusicNote" to Icons.Filled.MusicNote, "Restaurant" to Icons.Filled.Restaurant,
        "Bedtime" to Icons.Filled.Bedtime, "SelfImprovement" to Icons.Filled.SelfImprovement,
        "Brush" to Icons.Filled.Brush, "Pets" to Icons.Filled.Pets
    )

    private val diasParaEnum = mapOf(
        "Seg" to "SEGUNDA", "Ter" to "TERCA", "Qua" to "QUARTA",
        "Qui" to "QUINTA", "Sex" to "SEXTA", "Sáb" to "SABADO", "Dom" to "DOMINGO"
    )

    // ── Novos estados globais ─────────────────────────
    private val _todosHabitos = MutableStateFlow<List<HabitoResponse>>(emptyList())
    private val _checkinsPorHabito = MutableStateFlow<Map<Long, List<CheckinResponse>>>(emptyMap())

    private val _habitosDoDiaSelecionado = MutableStateFlow<List<HabitoDisplay>>(emptyList())
    val habitosComStatus: StateFlow<List<HabitoDisplay>> = _habitosDoDiaSelecionado

    private val _progressoDiaSelecionado = MutableStateFlow(0)
    val progressoDiaSelecionado: StateFlow<Int> = _progressoDiaSelecionado

    private val _diaAnteriorState = MutableStateFlow(DiaAnteriorUiState())
    val diaAnteriorState: StateFlow<DiaAnteriorUiState> = _diaAnteriorState

    // Retorna a data real do dia selecionado (dentro da semana atual)
    private fun dataDoDiaSelecionado(): LocalDate {
        val offset = diasDaSemana.indexOf(diaSelecionado)
        val hoje = LocalDate.now()
        val inicioSemana = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return inicioSemana.plusDays(offset.toLong())
    }

    // Atualiza as listas de hábitos e progresso para o dia selecionado
    private fun atualizarHabitosEProgresso() {
        val todos = _todosHabitos.value
        val checksMap = _checkinsPorHabito.value
        val diaEnum = diasParaEnum[diaSelecionado] ?: return
        val data = dataDoDiaSelecionado()
        val dataStr = data.toString()

        val habitosDoDia = todos.filter { it.diasDaSemana.contains(diaEnum) }
        var concluidosCount = 0
        val habitosComStatus = habitosDoDia.map { habito ->
            val checkins = checksMap[habito.habitId] ?: emptyList()
            val feito = checkins.any { it.dataCheckin == dataStr }
            if (feito) concluidosCount++
            HabitoDisplay(id = habito.habitId, nome = habito.habitName, icone = habito.habitIcon, concluido = feito)
        }
        _habitosDoDiaSelecionado.value = habitosComStatus

        val total = habitosDoDia.size
        val percent = if (total > 0) ((concluidosCount * 100) / total).coerceIn(0, 100) else 0
        _progressoDiaSelecionado.value = percent

        // Preencher estado da tela de dia anterior
        val habitosAnteriores = habitosDoDia.map { habito ->
            val feito = (checksMap[habito.habitId] ?: emptyList()).any { it.dataCheckin == dataStr }
            HabitoDiaAnterior(
                id = habito.habitId.toString(),
                nome = habito.habitName,
                icone = iconesDisponiveis.firstOrNull { it.first == habito.habitIcon }?.second ?: Icons.Filled.Star,
                foiConcluido = feito,
                categoria = mapearCategoria(habito.categoria)
            )
        }
        val ofensiva = calcularOfensiva(data)
        val categoriaProgresso = calcularProgressoPorCategoria(habitosAnteriores)

        _diaAnteriorState.value = DiaAnteriorUiState(
            labelSemana = "Week 1",
            diaAtual = diaSelecionado,
            habitos = habitosAnteriores,
            progressoPercent = percent,
            ofensiva = ofensiva,
            concluidosCount = concluidosCount,
            totalCount = total,
            progressoCategoria = categoriaProgresso,
            mensagemMotivacional = estrategiaMsg.gerar(percent)
        )
    }

    // Carrega todos os hábitos do usuário e seus check‑ins históricos
    fun carregarTodosHabitosECheckins() {
        val userId = _uiState.value.usuarioLogado?.id ?: return
        viewModelScope.launch {
            try {
                val habitos = RetrofitClient.api.listarHabitosDoUsuario(userId)
                _todosHabitos.value = habitos
                val checksMap = mutableMapOf<Long, List<CheckinResponse>>()
                habitos.forEach { habito ->
                    try {
                        checksMap[habito.habitId] = RetrofitClient.api.historicoCheckins(habito.habitId)
                    } catch (_: Exception) { }
                }
                _checkinsPorHabito.value = checksMap
                atualizarHabitosEProgresso()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(erro = "Erro ao carregar hábitos: ${e.message}")
            }
        }
    }

    fun selecionarDia(diaClicado: String) {
        diaSelecionado = diaClicado
        modoEdicao = false
        atualizarHabitosEProgresso()
    }

    fun abrirModal() {
        if (!podeEditar()) return
        _uiState.value = _uiState.value.copy(erro = null)
        mostrarModal = true
    }
    fun fecharModal() { mostrarModal = false }

    fun alternarModoEdicao() {
        if (podeEditar()) modoEdicao = !modoEdicao
    }

    fun podeEditar(): Boolean {
        val idxSelecionado = diasDaSemana.indexOf(diaSelecionado)
        return idxSelecionado >= indexDeHoje
    }

    fun podeCheckin(): Boolean {
        return diaSelecionado == diasDaSemana[indexDeHoje]
    }

    // Autenticação
    fun login(email: String, senha: String, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)
            try {
                val usuario = RetrofitClient.api.login(UsuarioLoginRequest(email, senhaHash = senha))
                _uiState.value = _uiState.value.copy(carregando = false, usuarioLogado = usuario)
                carregarTodosHabitosECheckins()
                onSucesso()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(carregando = false, erro = "Email ou senha inválidos. Tente novamente!")
                onErro("")
            }
        }
    }

    fun cadastrar(nome: String, sobrenome: String, email: String, senha: String, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)
            try {
                val usuario = RetrofitClient.api.cadastrarUsuario(UsuarioCadastroRequest(nome, sobrenome, email, senha))
                _uiState.value = _uiState.value.copy(carregando = false, usuarioLogado = usuario)
                onSucesso()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(carregando = false, erro = "Erro ao cadastrar: ${e.message}")
                onErro("")
            }
        }
    }

    fun restaurarSessao(usuario: UsuarioResponse) {
        _uiState.value = _uiState.value.copy(usuarioLogado = usuario)
        carregarTodosHabitosECheckins()
    }

    // Hábitos
    fun adicionarHabito(nomeDigitado: String, categoria: String, icone: String, diasSelecionados: List<String>) {
        val userId = _uiState.value.usuarioLogado?.id ?: return
        val diasConvertidos = diasSelecionados.mapNotNull { diasParaEnum[it] }
        if (diasConvertidos.isEmpty()) {
            _uiState.value = _uiState.value.copy(erro = "Selecione ao menos um dia.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)
            try {
                RetrofitClient.api.criarHabito(HabitoRequest(userId, nomeDigitado, categoria, icone, diasConvertidos))
                fecharModal()
                carregarTodosHabitosECheckins()   // recarrega todos
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(carregando = false, erro = "Erro ao criar hábito: ${e.message}")
            }
        }
    }

    fun removerHabito(habitoId: Long) {
        if (!podeEditar()) return
        viewModelScope.launch {
            try {
                RetrofitClient.api.deletarHabito(habitoId)
                carregarTodosHabitosECheckins()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(erro = "Erro ao remover: ${e.message}")
            }
        }
    }

    fun realizarCheckin(habitoId: Long) {
        if (!podeCheckin()) return
        viewModelScope.launch {
            try {
                RetrofitClient.api.realizarCheckin(CheckinRequest(habitoId, LocalDate.now().toString()))
                // Recarrega histórico do hábito e atualiza
                val novoHistorico = RetrofitClient.api.historicoCheckins(habitoId)
                _checkinsPorHabito.update { it + (habitoId to novoHistorico) }
                atualizarHabitosEProgresso()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(erro = "Erro ao checkin: ${e.message}")
            }
        }
    }

    fun alternarStatusDoHabito(habitoId: Long) {
        val jaConcluido = _habitosDoDiaSelecionado.value.find { it.id == habitoId }?.concluido ?: false
        if (!jaConcluido) realizarCheckin(habitoId)
    }

    // Ranking
    fun carregarRanking() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true)
            try {
                val usuarios = RetrofitClient.api.listarUsuarios()
                val itens = usuarios.mapNotNull { usuario ->
                    try {
                        val prog = RetrofitClient.api.progressoDoDia(usuario.id)
                        val percent = if (prog.total > 0) ((prog.concluidos * 100) / prog.total).toInt() else 0
                        RankingItem(usuario.nome, percent, prog.concluidos.toInt())
                    } catch (e: Exception) { null }
                }.sortedByDescending { it.percentual }
                _uiState.value = _uiState.value.copy(carregando = false, ranking = itens)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(carregando = false, erro = "Erro no ranking: ${e.message}")
            }
        }
    }

    // Métodos auxiliares privados
    private fun mapearCategoria(cat: String): CategoriaHabito {
        return when (cat) {
            "EDUCACAO" -> CategoriaHabito.EDUCACAO
            "SAUDE" -> CategoriaHabito.SAUDE
            "RESPONSABILIDADES" -> CategoriaHabito.RESPONSABILIDADES
            else -> CategoriaHabito.EDUCACAO
        }
    }

    private fun calcularOfensiva(ateData: LocalDate): Int {
        var dias = 0
        var data = ateData
        while (true) {
            val diaSemana = data.dayOfWeek
            val diaEnum = when (diaSemana) {
                DayOfWeek.MONDAY -> "SEGUNDA"
                DayOfWeek.TUESDAY -> "TERCA"
                DayOfWeek.WEDNESDAY -> "QUARTA"
                DayOfWeek.THURSDAY -> "QUINTA"
                DayOfWeek.FRIDAY -> "SEXTA"
                DayOfWeek.SATURDAY -> "SABADO"
                DayOfWeek.SUNDAY -> "DOMINGO"
            }
            val dataStr = data.toString()
            val habitosDesseDia = _todosHabitos.value.filter { it.diasDaSemana.contains(diaEnum) }
            if (habitosDesseDia.isEmpty()) break
            var concluidos = 0
            for (h in habitosDesseDia) {
                if ((_checkinsPorHabito.value[h.habitId] ?: emptyList()).any { it.dataCheckin == dataStr }) concluidos++
            }
            val progresso = if (habitosDesseDia.isNotEmpty()) (concluidos * 100) / habitosDesseDia.size else 0
            if (progresso >= 60) { dias++; data = data.minusDays(1) } else break
        }
        return dias
    }

    private fun calcularProgressoPorCategoria(habitos: List<HabitoDiaAnterior>): List<ProgressoCategoria> {
        val mapa = mutableMapOf<CategoriaHabito, Pair<Int,Int>>()
        for (h in habitos) {
            val (c, t) = mapa.getOrDefault(h.categoria, Pair(0,0))
            mapa[h.categoria] = Pair(c + if (h.foiConcluido) 1 else 0, t + 1)
        }
        return mapa.map { (cat, par) ->
            ProgressoCategoria(cat, if (par.second > 0) (par.first * 100) / par.second else 0)
        }
    }
}