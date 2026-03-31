package com.example.yadot

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yadot.screen.Cadastrar
import com.example.yadot.screen.Entrar
import com.example.yadot.screen.HomePag
import com.example.yadot.screen.SemHabitos


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(modifier: Modifier = Modifier){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rotas.HOME) {

        composable(Rotas.HOME) {
            HomePag(modifier, navController)
        }
        composable(Rotas.ENTRAR) {
            Entrar(modifier, navController)
        }
        composable(Rotas.CADASTRAR) {
            Cadastrar(modifier, navController)
        }
        composable(Rotas.TELA_PRINCIPAL) {
            SemHabitos(modifier, navController, viewModel())
        }
    }
}
