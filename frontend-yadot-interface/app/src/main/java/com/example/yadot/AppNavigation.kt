package com.example.yadot

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Button

// Telas
import com.example.yadot.screen.Cadastrar
import com.example.yadot.screen.Entrar
import com.example.yadot.screen.HomePag
import com.example.yadot.screen.HomeHabitos
import com.example.yadot.screen.RankingOfensivaScreen
import com.example.yadot.viewmodel.SessionManager
import com.example.yadot.viewmodel.HabitosViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: HabitosViewModel = viewModel()
    val context = LocalContext.current

    // Define a tela inicial baseado na sessão
    val startDestination = if (SessionManager.isLoggedIn(context)) {
        // Recupera dados salvos e pré-carrega no ViewModel
        LaunchedEffect(Unit) {
            val id = SessionManager.getUserId(context)
            val email = SessionManager.getUserEmail(context)
            val nome = SessionManager.getUserName(context)
            // Cria um UsuarioResponse simples para o ViewModel
            val usuario = com.example.yadot.network.UsuarioResponse(
                id = id,
                nome = nome,
                sobrenome = "",
                email = email
            )
            viewModel.restaurarSessao(usuario) // método a ser criado no VM
        }
        Rotas.TELA_PRINCIPAL
    } else {
        Rotas.HOME
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Rotas.ENTRAR) {
            Entrar(modifier, navController, viewModel)
        }
        composable(Rotas.CADASTRAR) {
            Cadastrar(modifier, navController, viewModel)
        }
        composable(Rotas.HOME) {
            HomePag(modifier, navController, viewModel)
        }
        composable(Rotas.TELA_PRINCIPAL) {
            HomeHabitos(navController = navController, viewModel = viewModel)
        }
        composable(Rotas.RANKING) {
            RankingOfensivaScreen(viewModel = viewModel)
        }
    }
}