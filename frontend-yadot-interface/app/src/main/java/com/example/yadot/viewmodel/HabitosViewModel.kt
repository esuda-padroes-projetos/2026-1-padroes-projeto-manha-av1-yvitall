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
import java.time.LocalDate

// ════════════════════════════════════════════════════════════
//  STRATEGY PATTERN
// ════════════════════════════════════════════════════════════

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

// ════════════════════════════════════════════════════════════
//  ESTADO DA UI
// ════════════════════════════════════════════════════════════

data class RankingItem(
    val nome: String,
    val percentual: Int,
    val ofensivaDias: Int
)

data class HabitosUiState(
    val carregando: Boolean = false,
    val erro: String? = null,
    val usuarioLogado: UsuarioResponse? = null,
    val habitosDeHoje: List<HabitoResponse> = emptyList(),
    val progressoHoje: ProgressoResponse? = null,
    val ranking: List<RankingItem> = emptyList()
)

/** Modelo exibido na lista da tela SemHabitos */
data class HabitoDisplay(
    val id: Long,
    val nome: String,
    val icone: String,
    val concluido: Boolean
)

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

    val categorias = listOf(
        "EDUCACAO", "SAUDE", "TRABALHO", "ESTUDOS",
        "RESPONSABILIDADES", "FINANCAS", "CASA", "LAZER"
    )

    // Mapa de exibição das categorias (agora dentro da classe)
    val categoriasDisplay = mapOf(
        "EDUCACAO"          to "Educação",
        "SAUDE"             to "Saúde",
        "TRABALHO"          to "Trabalho",
        "ESTUDOS"           to "Estudos",
        "RESPONSABILIDADES" to "Responsabilidades",
        "FINANCAS"          to "Finanças",
        "CASA"              to "Casa",
        "LAZER"             to "Lazer"
    )

    val iconesDisponiveis: List<Pair<String, ImageVector>> = listOf(
        "Star"            to Icons.Filled.Star,
        "AttachMoney"     to Icons.Filled.AttachMoney,
        "FitnessCenter"   to Icons.Filled.FitnessCenter,
        "Book"            to Icons.Filled.Book,
        "Home"            to Icons.Filled.Home,
        "Work"            to Icons.Filled.Work,
        "ShoppingCart"    to Icons.Filled.ShoppingCart,
        "Favorite"        to Icons.Filled.Favorite,
        "School"          to Icons.Filled.School,
        "Code"            to Icons.Filled.Code,
        "MusicNote"       to Icons.Filled.MusicNote,
        "Restaurant"      to Icons.Filled.Restaurant,
        "Bedtime"         to Icons.Filled.Bedtime,
        "SelfImprovement" to Icons.Filled.SelfImprovement,
        "Brush"           to Icons.Filled.Brush,
        "Pets"            to Icons.Filled.Pets,
    )

    // Mapeamento dos dias da semana para o formato do backend (agora dentro da classe)
    private val diasParaEnum = mapOf(
        "Seg" to "SEGUNDA",
        "Ter" to "TERCA",
        "Qua" to "QUARTA",
        "Qui" to "QUINTA",
        "Sex" to "SEXTA",
        "Sáb" to "SABADO",
        "Dom" to "DOMINGO"
    )

    // ── Estado dos check‑ins do dia ──────────────────────
    private val _checkinsHoje = MutableStateFlow<Map<Long, Boolean>>(emptyMap())

    /** Combina a lista de hábitos do dia com o mapa de check‑ins */
    val habitosComStatus: StateFlow<List<HabitoDisplay>> = combine(
        _uiState.map { it.habitosDeHoje },
        _checkinsHoje
    ) { habitos, checks ->
        habitos.map { habito ->
            HabitoDisplay(
                id = habito.habitId,
                nome = habito.habitName,
                icone = habito.habitIcon,
                concluido = checks[habito.habitId] == true
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Navegação e modal ──────────────────────────────────

    fun selecionarDia(diaClicado: String) {
        diaSelecionado = diaClicado
    }

    fun abrirModal() {
        _uiState.value = _uiState.value.copy(erro = null)
        mostrarModal = true
    }
    fun fecharModal()        { mostrarModal = false }
    fun alternarModoEdicao() { modoEdicao = !modoEdicao }

    fun ehDiaEditavel(): Boolean {
        return diaSelecionado == diasDaSemana[indexDeHoje]
    }

    // ── Autenticação ───────────────────────────────────────

    fun login(email: String, senha: String, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)
            try {
                val usuario = RetrofitClient.api.login(
                    UsuarioLoginRequest(email = email, senhaHash = senha)
                )
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    usuarioLogado = usuario
                )
                carregarHabitosDeHoje()
                onSucesso()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = "Erro: ${e.message}"   // ← adicionado
                )
                onErro("Erro no login: ${e.message}") // mantido se quiser log
            }
        }
    }

    fun cadastrar(
        nome: String,
        sobrenome: String,
        email: String,
        senha: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)
            try {
                val usuario = RetrofitClient.api.cadastrarUsuario(
                    UsuarioCadastroRequest(
                        nome = nome,
                        sobrenome = sobrenome,
                        email = email,
                        senhaHash = senha
                    )
                )
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    usuarioLogado = usuario
                )
                onSucesso()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = "Erro: ${e.message}"   // ← adicionado
                )
                onErro("Erro no cadastro: ${e.message}")
            }
        }
    }

    // ── Hábitos ────────────────────────────────────────────

    fun carregarHabitosDeHoje() {
        val userId = _uiState.value.usuarioLogado?.id ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true)
            try {
                val habitos = RetrofitClient.api.listarHabitosDeHoje(userId)
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    habitosDeHoje = habitos
                )
                carregarCheckinsHoje()
                carregarProgressoDeHoje()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = "Erro ao carregar hábitos: ${e.message}"
                )
            }
        }
    }

    private fun carregarCheckinsHoje() {
        val habitos = _uiState.value.habitosDeHoje
        if (habitos.isEmpty()) {
            _checkinsHoje.value = emptyMap()
            return
        }
        viewModelScope.launch {
            try {
                val hoje = LocalDate.now().toString()
                val checks = mutableMapOf<Long, Boolean>()
                habitos.forEach { habito ->
                    try {
                        val historico = RetrofitClient.api.historicoCheckins(habito.habitId)
                        val temHoje = historico.any { it.dataCheckin == hoje }
                        checks[habito.habitId] = temHoje
                    } catch (_: Exception) {
                        checks[habito.habitId] = false
                    }
                }
                _checkinsHoje.value = checks
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    erro = "Erro ao carregar check‑ins: ${e.message}"
                )
            }
        }
    }

    fun adicionarHabito(
        nomeDigitado: String,
        categoria: String,
        icone: String,
        diasSelecionados: List<String>
    ) {
        val userId = _uiState.value.usuarioLogado?.id ?: return
        val diasConvertidos = diasSelecionados.mapNotNull { diasParaEnum[it] }
        if (diasConvertidos.isEmpty()) {
            _uiState.value = _uiState.value.copy(erro = "Selecione ao menos um dia da semana.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true, erro = null)
            try {
                RetrofitClient.api.criarHabito(
                    HabitoRequest(
                        usuarioId = userId,
                        habitName = nomeDigitado,
                        categoria = categoria,
                        habitIcon = icone,
                        diasDaSemana = diasConvertidos
                    )
                )
                _uiState.value = _uiState.value.copy(carregando = false, erro = null)
                fecharModal()
                carregarHabitosDeHoje()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = "Erro ao criar hábito: ${e.message}"
                )
            }
        }
    }

    fun editarHabito(
        habitoId: Long,
        nomeDigitado: String,
        categoria: String,
        icone: String,
        diasSelecionados: List<String>
    ) {
        if (!ehDiaEditavel()) return
        val userId = _uiState.value.usuarioLogado?.id ?: return
        viewModelScope.launch {
            try {
                RetrofitClient.api.editarHabito(
                    habitoId,
                    HabitoRequest(
                        usuarioId = userId,
                        habitName = nomeDigitado,
                        categoria = categoria,
                        habitIcon = icone,
                        diasDaSemana = diasSelecionados
                    )
                )
                carregarHabitosDeHoje()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    erro = "Erro ao editar hábito: ${e.message}"
                )
            }
        }
    }

    fun removerHabito(habitoId: Long) {
        if (!ehDiaEditavel()) return
        viewModelScope.launch {
            try {
                RetrofitClient.api.deletarHabito(habitoId)
                carregarHabitosDeHoje()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    erro = "Erro ao remover hábito: ${e.message}"
                )
            }
        }
    }

    // ── Checkin ────────────────────────────────────────────

    fun realizarCheckin(habitoId: Long) {
        if (!ehDiaEditavel()) return
        viewModelScope.launch {
            try {
                RetrofitClient.api.realizarCheckin(
                    CheckinRequest(
                        habitId = habitoId,
                        dataCheckin = LocalDate.now().toString()
                    )
                )
                _checkinsHoje.update { it + (habitoId to true) }
                carregarProgressoDeHoje()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    erro = "Erro ao realizar checkin: ${e.message}"
                )
            }
        }
    }

    fun alternarStatusDoHabito(habitoId: Long) {
        val concluido = _checkinsHoje.value[habitoId] ?: false
        if (!concluido) {
            realizarCheckin(habitoId)
        }
    }

    // ── Progresso ──────────────────────────────────────────

    fun carregarProgressoDeHoje() {
        val userId = _uiState.value.usuarioLogado?.id ?: return
        viewModelScope.launch {
            try {
                val progresso = RetrofitClient.api.progressoDoDia(userId)
                _uiState.value = _uiState.value.copy(progressoHoje = progresso)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    erro = "Erro ao carregar progresso: ${e.message}"
                )
            }
        }
    }

    fun calcularProgressoDoDia(): Int {
        val prog = _uiState.value.progressoHoje ?: return 0
        return if (prog.total > 0) ((prog.concluidos * 100) / prog.total).toInt() else 0
    }

    fun calcularMensagemMotivacional(): String {
        val prog = _uiState.value.progressoHoje ?: return estrategiaMsg.gerar(0)
        val percentual = if (prog.total > 0)
            ((prog.concluidos.toDouble() / prog.total) * 100).toInt()
        else 0
        return estrategiaMsg.gerar(percentual)
    }

    // ── Ranking ────────────────────────────────────────────

    fun carregarRanking() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true)
            try {
                val usuarios = RetrofitClient.api.listarUsuarios()
                val itens = usuarios.mapNotNull { usuario ->
                    try {
                        val prog = RetrofitClient.api.progressoDoDia(usuario.id)
                        val percentual = if (prog.total > 0)
                            ((prog.concluidos.toDouble() / prog.total) * 100).toInt()
                        else 0
                        RankingItem(usuario.nome, percentual, prog.concluidos.toInt())
                    } catch (e: Exception) { null }
                }.sortedByDescending { it.percentual }
                _uiState.value = _uiState.value.copy(carregando = false, ranking = itens)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = "Erro no ranking: ${e.message}"
                )
            }
        }
    }
    fun restaurarSessao(usuario: UsuarioResponse) {
        _uiState.value = _uiState.value.copy(usuarioLogado = usuario)
        carregarHabitosDeHoje()
    }
}