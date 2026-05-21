package com.example.yadot.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yadot.R
import com.example.yadot.Rotas
import com.example.yadot.ui.theme.Branco
import com.example.yadot.ui.theme.Preto
import com.example.yadot.viewmodel.HabitosViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Cadastrar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HabitosViewModel
) {
    var nome           by remember { mutableStateOf("") }
    var sobrenome      by remember { mutableStateOf("") }
    var email          by remember { mutableStateOf("") }
    var senha          by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var erroSenhas     by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Branco)
            .padding(horizontal = 40.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Image(
            painter = painterResource(id = R.drawable.logoyadot),
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp)
        )

        Text(
            text = "Cadastrar-se",
            style = TextStyle(fontSize = 40.sp, color = Preto, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 7.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Digite seu Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = sobrenome,
            onValueChange = { sobrenome = it },
            label = { Text("Digite seu Sobrenome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Digite seu Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = {
                senha = it
                erroSenhas = false
            },
            label = { Text("Digite sua Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Esconder senha" else "Mostrar senha")
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmarSenha,
            onValueChange = {
                confirmarSenha = it
                erroSenhas = false
            },
            label = { Text("Confirme sua senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation()
        )

        // Erro de senhas divergentes
        if (erroSenhas) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("As senhas não conferem", color = Color.Red, fontSize = 13.sp)
        }

        // Erro da API (email duplicado, etc.)
        if (uiState.erro != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = uiState.erro ?: "", color = Color.Red, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (senha == confirmarSenha) {
                    viewModel.cadastrar(
                        nome = nome,
                        sobrenome = sobrenome,
                        email = email,
                        senha = senha,
                        onSucesso = {
                            navController.navigate(Rotas.ENTRAR)
                        },
                        onErro = { /* ViewModel trata */ }
                    )
                } else {
                    erroSenhas = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Preto),
            enabled = !uiState.carregando
        ) {
            if (uiState.carregando) {
                CircularProgressIndicator(color = Branco, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Cadastrar", color = Branco, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = "yadoT©", color = Preto, fontSize = 15.sp)
        }
    }
}

