package com.example.yadot.model


data class Habito(
    val id: Int,
    val nome: String,
    val concluido: Boolean,
    val categoria: String = "",
    val icone: String = "Star"
)