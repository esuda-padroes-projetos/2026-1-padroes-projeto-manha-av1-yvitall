package com.example.yadot.model

// ========== USUÁRIO ==========

// O que enviamos para cadastrar
data class UsuarioCadastroRequest(
    val nome: String,
    val sobrenome: String,
    val email: String,
    val senhaHash: String
)

// O que enviamos para login
data class UsuarioLoginRequest(
    val email: String,
    val senhaHash: String
)

// O que recebemos como resposta
data class UsuarioResponse(
    val id: Long,
    val nome: String,
    val sobrenome: String,
    val email: String
)

// ========== HÁBITO ==========

data class HabitoRequest(
    val usuarioId: Long,
    val habitName: String,
    val categoria: String,
    val habitIcon: String,
    val diasDaSemana: List<String>  // Ex: ["SEGUNDA", "TERCA"]
)

data class HabitoResponse(
    val habitId: Long,
    val habitName: String,
    val categoria: String,
    val habitIcon: String,
    val diasDaSemana: List<String>
)

// ========== CHECK-IN ==========

data class CheckinRequest(
    val habitId: Long,
    val dataCheckin: String  // Formato: "2024-01-15"
)

data class CheckinResponse(
    val checkinId: Long,
    val habitId: Long,
    val dataCheckin: String
)

// ========== PROGRESSO ==========

data class ProgressoResponse(
    val concluidos: Long,   // Quantos check-ins feitos hoje
    val pendentes: Long,     // Quantos ainda faltam
    val total: Long          // Total de hábitos do dia
)