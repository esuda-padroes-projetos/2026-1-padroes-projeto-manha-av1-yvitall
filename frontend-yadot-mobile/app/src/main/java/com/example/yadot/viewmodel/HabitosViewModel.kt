package com.example.yadot.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import com.example.yadot.model.Habito
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

@RequiresApi(Build.VERSION_CODES.O)
class HabitosViewModel : ViewModel() {

    val diasDaSemana = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
    private val indexDeHoje = LocalDate.now().dayOfWeek.value - 1

    var diaSelecionado by mutableStateOf(diasDaSemana[indexDeHoje])
        private set

    var habitosCadastrados by mutableStateOf(mapOf<String, List<Habito>>())
        private set

    var mostrarModal by mutableStateOf(false)
        private set

    val categorias = listOf(
        "Educação", "Saúde", "Trabalho", "Estudos",
        "Responsabilidades", "Finanças", "Casa", "Lazer"
    )

    val iconesDisponiveis: List<Pair<String, ImageVector>> = listOf(
        "Star" to Icons.Filled.Star,
        "AttachMoney" to Icons.Filled.AttachMoney,
        "FitnessCenter" to Icons.Filled.FitnessCenter,
        "Book" to Icons.Filled.Book,
        "Home" to Icons.Filled.Home,
        "Work" to Icons.Filled.Work,
        "ShoppingCart" to Icons.Filled.ShoppingCart,
        "Favorite" to Icons.Filled.Favorite,
        "School" to Icons.Filled.School,
        "Code" to Icons.Filled.Code,
        "MusicNote" to Icons.Filled.MusicNote,
        "DirectionsRun" to Icons.Filled.DirectionsRun,
        "Restaurant" to Icons.Filled.Restaurant,
        "Bedtime" to Icons.Filled.Bedtime,
        "SelfImprovement" to Icons.Filled.SelfImprovement,
        "Brush" to Icons.Filled.Brush,
        "Pets" to Icons.Filled.Pets,
        "FlightTakeoff" to Icons.Filled.FlightTakeoff,
    )

    var modoEdicao by mutableStateOf(false)
        private set

    fun selecionarDia(diaClicado: String) {
        diaSelecionado = diaClicado
    }

    fun abrirModal() {
        mostrarModal = true
    }

    fun fecharModal() {
        mostrarModal = false
    }

    fun adicionarHabito(nomeDigitado: String, categoria: String, icone: String) {

        val novoHabito = Habito(
            id = (0..1000).random(),
            nome = nomeDigitado,
            concluido = false,
            categoria = categoria,
            icone = icone
        )

        val listaAtual = habitosCadastrados[diaSelecionado] ?: emptyList()
        habitosCadastrados = habitosCadastrados + (diaSelecionado to (listaAtual + novoHabito))

        fecharModal()
    }

    fun alternarStatusDoHabito(idDoHabito: Int) {
        val listaAtual = habitosCadastrados[diaSelecionado] ?: return

        val novaLista = listaAtual.map { habito ->
            if (habito.id == idDoHabito) habito.copy(concluido = !habito.concluido) else habito
        }

        habitosCadastrados = habitosCadastrados + (diaSelecionado to novaLista)
    }

    fun calcularProgressoDoDia(): Int {
        val habitosDoDia = habitosCadastrados[diaSelecionado] ?: emptyList()
        val total = habitosDoDia.size
        if (total == 0) return 0

        val concluidos = habitosDoDia.count { it.concluido }
        return (concluidos * 100) / total
    }

    fun alternarModoEdicao() {
        modoEdicao = !modoEdicao
    }

    fun removerHabito(idDoHabito: Int) {
        val listaAtual = habitosCadastrados[diaSelecionado] ?: return
        val novaLista = listaAtual.filter { it.id != idDoHabito }
        habitosCadastrados = habitosCadastrados + (diaSelecionado to novaLista)
    }
}