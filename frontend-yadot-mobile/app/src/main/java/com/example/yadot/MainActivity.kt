package com.example.yadot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomePag(onLoginClick = {}, onRegisterClick = {})

        }
    }
}

@Composable

fun HomePag(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 80.dp).background(Color.White),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Distrações",
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color.LightGray,
                    textDecoration = TextDecoration.LineThrough
                )
            )
            Text(
                text = "Procrastinação",
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color.LightGray,
                    textDecoration = TextDecoration.LineThrough
                ),
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "Foco & Produtividade",
                style = TextStyle(
                    fontSize = 35.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 7.dp)
            )
            Text(
                text = "yadoT",
                style = TextStyle(
                    fontSize = 80.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 7.dp)
            )
            Text(
                text = "Tenha o controle de seus hábitos\nna palma de sua mão.",
                style = TextStyle(
                    fontSize = 17.sp,
                    color = Color.Gray,
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
                    onClick = { onLoginClick() },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Entrar",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Ou",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Button(
                    onClick = { onRegisterClick() },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Cadastrar",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "yadoT©",
                    color = Color.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 120.dp)
                )
            }
        }
    }

}

@Preview
@Composable
fun HomePreview(){
    HomePag(onLoginClick = {}, onRegisterClick = {})
}