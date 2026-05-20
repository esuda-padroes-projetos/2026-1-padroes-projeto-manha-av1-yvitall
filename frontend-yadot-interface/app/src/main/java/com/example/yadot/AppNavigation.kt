package com.example.yadot

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Telas
import com.example.yadot.screen.Cadastrar
import com.example.yadot.screen.Entrar
import com.example.yadot.screen.HomePag
import com.example.yadot.screen.RankingOfensivaScreen
import com.example.yadot.viewmodel.HabitosViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    // ViewModel ÚNICA para o app todo (Padrão Observer)
    val viewModel: HabitosViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Rotas.   HOME  ) {

        // Tela de Login
        composable(Rotas.ENTRAR) {
            Entrar(modifier, navController, viewModel)
        }

        // Tela de Cadastro de Usuário
        composable(Rotas.CADASTRAR) {
            Cadastrar(modifier, navController, viewModel)
        }

        // A HOME ÚNICA (Aqui dentro você decide se mostra SemHabitos ou DailyTasks)
        composable(Rotas.HOME) {
            HomePag(modifier, navController, viewModel)
        }

        composable(Rotas.TELA_PRINCIPAL) {   // ← nova tela após login
            HomeHabitos(navController = navController, viewModel = viewModel)
        }

        // Ranking de ofensivas
        composable(Rotas.RANKING) {
            RankingOfensivaScreen(viewModel = viewModel)
        }

        // REMOVI a rota "DIA_ANTERIOR" e "TELA_PRINCIPAL" (SemHabitos)
        // porque elas devem ser componentes dentro da sua HomePag,
        // e não telas separadas que dão "pulo" na navegação.
    }
}