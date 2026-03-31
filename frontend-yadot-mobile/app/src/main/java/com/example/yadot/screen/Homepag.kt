package com.example.yadot.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yadot.Rotas
import com.example.yadot.ui.theme.Branco
import com.example.yadot.ui.theme.CinzaInativo
import com.example.yadot.ui.theme.CinzaTexto
import com.example.yadot.ui.theme.Preto

@Composable
fun HomePag(modifier: Modifier, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Branco)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 80.dp).background(Branco),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Distrações",
                style = TextStyle(
                    fontSize = 30.sp,
                    color = CinzaInativo,
                    textDecoration = TextDecoration.LineThrough
                )
            )
            Text(
                text = "Procrastinação",
                style = TextStyle(
                    fontSize = 30.sp,
                    color = CinzaInativo,
                    textDecoration = TextDecoration.LineThrough
                ),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "Foco & Produtividade",
                style = TextStyle(
                    fontSize = 35.sp,
                    color = Preto,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 7.dp)
            )
            Text(
                text = "yadoT",
                style = TextStyle(
                    fontSize = 80.sp,
                    color = Preto,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 7.dp)
            )
            Text(
                text = "Tenha o controle de seus hábitos\nna palma de sua mão.",
                style = TextStyle(
                    fontSize = 17.sp,
                    color = CinzaTexto,
                    lineHeight = 17.sp
                ),
                modifier = Modifier.padding(top = 5.dp)
            )

            Spacer(modifier = Modifier.weight(5f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Button(
                    onClick = {
                        navController.navigate(Rotas.ENTRAR)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Preto)
                ) {
                    Text(
                        text = "Entrar",
                        color = Branco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Ou",
                    color = CinzaTexto,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Button(
                    onClick = {
                        navController.navigate(Rotas.CADASTRAR)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Preto)
                ) {
                    Text(
                        text = "Cadastrar",
                        color = Branco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "yadoT©",
                    color = Preto,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 120.dp)
                )
            }
        }
    }
}

