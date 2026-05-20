package com.example.yadot

object Rotas {
    const val HOME           = "homePag"
    const val ENTRAR         = "entrar"
    const val CADASTRAR      = "cadastrar"
    const val TELA_PRINCIPAL = "HomeHabitos"
    const val RANKING        = "ranking"
    const val DIA_ANTERIOR   = "dia_anterior/{dia}"   // rota com argumento

    // Função auxiliar para montar a rota com o dia real
    // Uso: navController.navigate(Rotas.diaAnterior("Seg"))
    fun diaAnterior(dia: String) = "dia_anterior/$dia"
}