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
import com.example.yadot.model.Habito
import com.example.yadot.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

@RequiresApi(Build.VERSION_CODES.O)
class HabitosViewModel(
    private val estrategiaMsg: EstrategiaMsg = EstrategiaMsgPadrao()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitosUiState())
    val uiState: StateFlow<HabitosUiState> = _uiState

    val diasDaSemana = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
    private val indexDeHoje get() = LocalDate.now().dayOfWeek.value - 1
    // "get()" recalcula toda vez que é chamado — sem isso ficaria preso no dia do primeiro acesso

    var diaSelecionado by mutableStateOf(diasDaSemana[indexDeHoje])
        private set

    var mostrarModal by mutableStateOf(false)
        private set

    var modoEdicao by mutableStateOf(false)
        private set

    val categorias = listOf(
        "EDUCACAO", "SAUDE", "TRABALHO", "ESTUDOS",
        "RESPONSABILIDADES", "FINANCAS", "CASA", "LAZER"
        // valores em maiúsculo sem acento — exatamente como estão no enum Java
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

    // ── Navegação e modal ──────────────────────────────────

    fun selecionarDia(diaClicado: String) {
        diaSelecionado = diaClicado
    }

    fun abrirModal()         { mostrarModal = true  }
    fun fecharModal()        { mostrarModal = false }
    fun alternarModoEdicao() { modoEdicao = !modoEdicao }

    // ── Regra de edição ────────────────────────────────────

    fun ehDiaEditavel(): Boolean {
        // Compara o dia selecionado com o dia real de hoje
        // Se o usuário está em "Ter" e hoje é "Qua" → não pode editar
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
                carregarHabitosDeHoje() // já carrega os hábitos ao logar
                onSucesso()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(carregando = false)
                onErro("Erro no login: ${e.message}")
                // agora o erro aparece na tela — a tela recebe via onErro
            }
        }
    }

    fun cadastrar(
        nome: String,
        sobrenome: String,  // adicionado — era sempre vazio antes
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
                        sobrenome = sobrenome, // agora vem da tela de cadastro
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
                _uiState.value = _uiState.value.copy(carregando = false)
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = "Erro ao carregar hábitos: ${e.message}"
                )
            }
        }
    }

    fun adicionarHabito(
        nomeDigitado: String,
        categoria: String,
        icone: String,
        diasSelecionados: List<String> // quais dias o hábito se repete
    ) {
        val userId = _uiState.value.usuarioLogado?.id ?: return
        // Bug corrigido: agora chama a API de verdade em vez de só salvar localmente

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(carregando = true)
            try {
                RetrofitClient.api.criarHabito(
                    HabitoRequest(
                        usuarioId = userId,
                        habitName = nomeDigitado,
                        categoria = categoria,
                        habitIcon = icone,
                        diasDaSemana = diasSelecionados
                    )
                )
                fecharModal()
                carregarHabitosDeHoje() // recarrega a lista após criar
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
        if (!ehDiaEditavel()) return // bloqueia se não for hoje
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
        if (!ehDiaEditavel()) return // bloqueia se não for hoje
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
        if (!ehDiaEditavel()) return // não permite checkin em dias passados
        viewModelScope.launch {
            try {
                RetrofitClient.api.realizarCheckin(
                    CheckinRequest(
                        habitId = habitoId,
                        dataCheckin = LocalDate.now().toString() // "2026-05-17"
                    )
                )
                carregarProgressoDeHoje()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    erro = "Erro ao realizar checkin: ${e.message}"
                )
            }
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
}