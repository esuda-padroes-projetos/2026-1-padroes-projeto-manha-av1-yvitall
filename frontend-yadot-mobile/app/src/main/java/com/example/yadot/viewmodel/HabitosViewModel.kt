package com.example.yadot.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yadot.api.RetrofitInstance
import com.example.yadot.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitosViewModel : ViewModel() {

    private val api = RetrofitInstance.apiService

    // ========== USUÁRIO ==========
    private val _usuarioLogado = MutableStateFlow<UsuarioResponse?>(null)
    val usuarioLogado: StateFlow<UsuarioResponse?> = _usuarioLogado

    // ========== HÁBITOS ==========
    private val _habitosDoDia = MutableStateFlow<List<HabitoResponse>>(emptyList())
    val habitosDoDia: StateFlow<List<HabitoResponse>> = _habitosDoDia

    // ========== PROGRESSO ==========
    private val _progresso = MutableStateFlow<ProgressoResponse?>(null)
    val progresso: StateFlow<ProgressoResponse?> = _progresso

    // ========== LOADING E ERRO ==========
    private val _estaCarregando = MutableStateFlow(false)
    val estaCarregando: StateFlow<Boolean> = _estaCarregando

    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro

    // ========== MODO EDIÇÃO ==========
    private val _modoEdicao = MutableStateFlow(false)
    val modoEdicao: StateFlow<Boolean> = _modoEdicao

    // ========== MODAL ==========
    private val _mostrarModal = MutableStateFlow(false)
    val mostrarModal: StateFlow<Boolean> = _mostrarModal

    // ========== DIAS DA SEMANA ==========
    val diasDaSemana = listOf("SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SABADO", "DOMINGO")

    // ========== CATEGORIAS ==========
    val categorias = listOf("SAUDE", "ESTUDO", "TRABALHO", "LAZER", "OUTROS")

    // ========== ÍCONES DISPONÍVEIS ==========
    val iconesDisponiveis: List<Pair<String, ImageVector>> = listOf(
        "Star" to Icons.Filled.Star,
        "Favorite" to Icons.Filled.Favorite,
        "Home" to Icons.Filled.Home,
        "Person" to Icons.Filled.Person,
        "Build" to Icons.Filled.Build,
        "Delete" to Icons.Filled.Delete,
        "CheckCircle" to Icons.Filled.CheckCircle,
        "Alarm" to Icons.Filled.Alarm,
        "WaterDrop" to Icons.Filled.WaterDrop,
        "MenuBook" to Icons.Filled.MenuBook,
        "Work" to Icons.Filled.Work,
        "Laptop" to Icons.Filled.Laptop
    )

    // ========== FUNÇÕES ==========

    fun alternarModoEdicao() {
        _modoEdicao.value = !_modoEdicao.value
    }

    fun abrirModal() {
        _mostrarModal.value = true
    }

    fun fecharModal() {
        _mostrarModal.value = false
    }

    fun limparErro() {
        _mensagemErro.value = null
    }

    // ========== AUTENTICAÇÃO ==========

    fun cadastrar(nome: String, sobrenome: String, email: String, senha: String) {
        viewModelScope.launch {
            _estaCarregando.value = true
            _mensagemErro.value = null
            try {
                val request = UsuarioCadastroRequest(nome, sobrenome, email, senha)
                val response = api.cadastrar(request)
                if (response.isSuccessful) {
                    _usuarioLogado.value = response.body()
                    carregarHabitosDoDia()
                } else {
                    _mensagemErro.value = "Erro ao cadastrar: ${response.message()}"
                }
            } catch (e: Exception) {
                _mensagemErro.value = "Falha na conexão: ${e.message}"
            } finally {
                _estaCarregando.value = false
            }
        }
    }

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            _estaCarregando.value = true
            _mensagemErro.value = null
            try {
                val request = UsuarioLoginRequest(email, senha)
                val response = api.login(request)
                if (response.isSuccessful) {
                    _usuarioLogado.value = response.body()
                    carregarHabitosDoDia()
                } else {
                    _mensagemErro.value = "Email ou senha incorretos"
                }
            } catch (e: Exception) {
                _mensagemErro.value = "Falha na conexão: ${e.message}"
            } finally {
                _estaCarregando.value = false
            }
        }
    }

    // ========== HÁBITOS ==========

    fun carregarHabitosDoDia() {
        val usuario = _usuarioLogado.value ?: return
        viewModelScope.launch {
            _estaCarregando.value = true
            try {
                val response = api.listarHabitosDoDia(usuario.id)
                if (response.isSuccessful) {
                    _habitosDoDia.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _mensagemErro.value = "Erro ao carregar hábitos: ${e.message}"
            } finally {
                _estaCarregando.value = false
            }
        }
    }

    fun adicionarHabito(nome: String, categoria: String, icone: String) {
        val usuario = _usuarioLogado.value ?: return
        viewModelScope.launch {
            _estaCarregando.value = true
            try {
                val request = HabitoRequest(
                    usuarioId = usuario.id,
                    habitName = nome,
                    categoria = categoria,
                    habitIcon = icone,
                    diasDaSemana = diasDaSemana
                )
                val response = api.criarHabito(request)
                if (response.isSuccessful) {
                    carregarHabitosDoDia()
                    fecharModal()
                } else {
                    _mensagemErro.value = "Erro ao criar hábito"
                }
            } catch (e: Exception) {
                _mensagemErro.value = "Erro: ${e.message}"
            } finally {
                _estaCarregando.value = false
            }
        }
    }

    fun deletarHabito(habitId: Long) {
        viewModelScope.launch {
            try {
                api.deletarHabito(habitId)
                carregarHabitosDoDia()
            } catch (e: Exception) {
                _mensagemErro.value = "Erro ao deletar: ${e.message}"
            }
        }
    }

    // ========== CHECK-IN ==========

    fun realizarCheckin(habitId: Long) {
        viewModelScope.launch {
            try {
                val hoje = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val request = CheckinRequest(habitId, hoje)
                api.realizarCheckin(request)
                carregarHabitosDoDia()
                carregarProgresso()
            } catch (e: Exception) {
                _mensagemErro.value = "Erro ao fazer check-in: ${e.message}"
            }
        }
    }

    fun carregarProgresso() {
        val usuario = _usuarioLogado.value ?: return
        viewModelScope.launch {
            try {
                val response = api.progressoDoDia(usuario.id)
                if (response.isSuccessful) {
                    _progresso.value = response.body()
                }
            } catch (e: Exception) {
                _mensagemErro.value = "Erro ao carregar progresso: ${e.message}"
            }
        }
    }

    fun calcularPorcentagemProgresso(): Float {
        val prog = _progresso.value ?: return 0f
        return if (prog.total > 0) {
            (prog.concluidos.toFloat() / prog.total.toFloat()) * 100f
        } else 0f
    }
}