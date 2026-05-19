package com.example.yadot.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.yadot.R
import com.example.yadot.Rotas
import com.example.yadot.ui.theme.Branco
import com.example.yadot.ui.theme.Preto
import com.example.yadot.ui.theme.VermelhoErro
import com.example.yadot.viewmodel.HabitosViewModel

@Composable
fun Entrar(modifier: Modifier = Modifier, navController: NavHostController) {

    // ========== VIEWMODEL ==========
    val viewModel: HabitosViewModel = viewModel()
    val estaCarregando by viewModel.estaCarregando.collectAsState()
    val mensagemErro by viewModel.mensagemErro.collectAsState()
    val usuarioLogado by viewModel.usuarioLogado.collectAsState()

    // ========== Navega automaticamente quando login der certo ==========
    LaunchedEffect(usuarioLogado) {
        if (usuarioLogado != null) {
            navController.navigate(Rotas.TELA_PRINCIPAL) {
                popUpTo(Rotas.HOME) { inclusive = true }
            }
        }
    }

    // ========== CAMPOS DO FORMULÁRIO ==========
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erroValidacao by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Branco)
            .padding(horizontal = 40.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Image(
            painter = painterResource(id = R.drawable.logoyadot),
            contentDescription = "Logo",
            modifier = Modifier
                .height(120.dp)
                .size(80.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Entrar",
            style = TextStyle(
                fontSize = 50.sp,
                color = Preto,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(top = 7.dp)
        )

        Spacer(modifier = Modifier.height(70.dp))

        // ========== CAMPO EMAIL ==========
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                erroValidacao = null
                viewModel.limparErro()
            },
            label = { Text(text = "Digite seu Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estaCarregando
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ========== CAMPO SENHA ==========
        OutlinedTextField(
            value = senha,
            onValueChange = {
                senha = it
                erroValidacao = null
                viewModel.limparErro()
            },
            label = { Text(text = "Digite sua Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            enabled = !estaCarregando
        )

        // ========== MENSAGENS DE ERRO ==========
        if (erroValidacao != null) {
            Text(
                text = erroValidacao!!,
                color = VermelhoErro,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (mensagemErro != null) {
            Text(
                text = mensagemErro!!,
                color = VermelhoErro,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ========== BOTÃO ENTRAR ==========
        Column(
            modifier = Modifier
                .background(color = Branco)
                .padding(horizontal = 40.dp, vertical = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    when {
                        email.isBlank() -> erroValidacao = "Email é obrigatório"
                        senha.isBlank() -> erroValidacao = "Senha é obrigatória"
                        else -> {
                            erroValidacao = null
                            viewModel.login(
                                email = email.trim(),
                                senha = senha,
                                onSucesso = { /* navegação já feita pelo LaunchedEffect */ },
                                onErro = { erro -> erroValidacao = erro }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Preto),
                enabled = !estaCarregando
            ) {
                if (estaCarregando) {
                    // Mostra loading enquanto faz login
                    CircularProgressIndicator(
                        color = Branco,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Entrar",
                        color = Branco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(5f))

        // ========== RODAPÉ ==========
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Branco),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "yadoT©",
                color = Preto,
                fontSize = 15.sp
            )
        }
    }
}