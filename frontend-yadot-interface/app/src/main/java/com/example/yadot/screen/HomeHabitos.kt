package com.example.yadot.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.yadot.viewmodel.HabitosViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeHabitos(
    navController: NavHostController,
    viewModel: HabitosViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Dispara a busca de dados assim que a tela é montada
    LaunchedEffect(Unit) {
        viewModel.carregarHabitosDeHoje()
        viewModel.carregarProgressoDeHoje()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.abrirModal() }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Mensagem Motivacional vinda do Strategy Pattern do seu VM
            Text(
                text = viewModel.calcularMensagemMotivacional(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Selector de Dias da Semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                viewModel.diasDaSemana.forEach { dia ->
                    Button(
                        onClick = { viewModel.selecionarDia(dia) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.diaSelecionado == dia) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(dia)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de Hábitos trazidos da API Spring Boot
            if (uiState.carregando) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(uiState.habitosDeHoje) { habito ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = habito.habitName)

                                // Botão de Check-in mapeado para a API
                                Button(
                                    onClick = { viewModel.realizarCheckin(habito.habitId) },
                                    enabled = viewModel.ehDiaEditavel() // trava se for dia retroativo
                                ) {
                                    Text("Feito")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Gerenciamento do Estado do Modal direto pelo ViewModel
    if (viewModel.mostrarModal) {
        // Seu componente de Dialog/Modal para Adicionar Habito entra aqui
        // Chamando viewModel.adicionarHabito(...) no clique de salvar
    }
}