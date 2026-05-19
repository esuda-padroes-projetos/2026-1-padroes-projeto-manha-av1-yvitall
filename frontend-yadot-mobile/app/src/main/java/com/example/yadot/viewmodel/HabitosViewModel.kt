package com.example.yadot.viewmodel

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

    // ========== CLIENTE HTTP ==========
    private val api = RetrofitInstance.apiService

    // ========== ESTADOS DA TELA ==========
    // São variáveis que a UI observa e se atualiza automaticamente

    private val _usuarioLogado = MutableStateFlow<UsuarioResponse?>(null)
    val usuarioLogado: StateFlow<UsuarioResponse?> = _usuarioLogado

    private val _habitosDoDia = MutableStateFlow<List<HabitoResponse>>(emptyList())
    val habitosDoDia: StateFlow<List<HabitoResponse>> = _habitosDoDia

    private val _progresso = MutableStateFlow<ProgressoResponse?>(null)
    val progresso: StateFlow<ProgressoResponse?> = _progresso

    private val _estaCarregando = MutableStateFlow(false)
    val estaCarregando: StateFlow<Boolean> = _estaCarregando

    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro

    // ========== DIAS DA SEMANA (para novos hábitos) ==========
    val diasDaSemana = listOf("SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SABADO", "DOMINGO")

    // ========== CATEGORIAS ==========
    val categorias = listOf("SAUDE", "ESTUDO", "TRABALHO", "LAZER", "OUTROS")

    // ========== FUNÇÕES DE AUTENTICAÇÃO ==========

    fun cadastrar(nome: String, sobrenome: String, email: String, senha: String) {
        viewModelScope.launch {
            _estaCarregando.value = true
            _mensagemErro.value = null

            try {
                val request = UsuarioCadastroRequest(
                    nome = nome,
                    sobrenome = sobrenome,
                    email = email,
                    senhaHash = senha  // O backend aplica o hash
                )

                val response = api.cadastrar(request)

                if (response.isSuccessful) {
                    _usuarioLogado.value = response.body()
                    // Após cadastrar, carrega os hábitos
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
                val request = UsuarioLoginRequest(
                    email = email,
                    senhaHash = senha
                )

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

    // ========== FUNÇÕES DE HÁBITOS ==========

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
                    diasDaSemana = diasDaSemana  // Por padrão, todos os dias
                )

                val response = api.criarHabito(request)
                if (response.isSuccessful) {
                    carregarHabitosDoDia()  // Recarrega a lista
                } else {
                    _mensagemErro.value = "Erro ao criar hábito: ${response.message()}"
                }
            } catch (e: Exception) {
                _mensagemErro.value = "Falha na conexão: ${e.message}"
            } finally {
                _estaCarregando.value = false
            }
        }
    }

    fun deletarHabito(habitId: Long) {
        viewModelScope.launch {
            try {
                val response = api.deletarHabito(habitId)
                if (response.isSuccessful) {
                    carregarHabitosDoDia()  // Atualiza a lista
                }
            } catch (e: Exception) {
                _mensagemErro.value = "Erro ao deletar: ${e.message}"
            }
        }
    }

    // ========== FUNÇÕES DE CHECK-IN ==========

    fun realizarCheckin(habitId: Long) {
        viewModelScope.launch {
            try {
                val hoje = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val request = CheckinRequest(
                    habitId = habitId,
                    dataCheckin = hoje
                )

                val response = api.realizarCheckin(request)
                if (response.isSuccessful) {
                    carregarHabitosDoDia()
                    carregarProgresso()
                }
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

    // ========== FUNÇÃO AUXILIAR ==========

    fun calcularPorcentagemProgresso(): Float {
        val prog = _progresso.value ?: return 0f
        return if (prog.total > 0) {
            (prog.concluidos.toFloat() / prog.total.toFloat()) * 100f
        } else 0f
    }

    fun limparErro() {
        _mensagemErro.value = null
    }
}