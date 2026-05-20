package com.example.yadot.screen

// ─────────────────────────────────────────────────────────────
//  RankingOfensivaScreen.kt
//
//  PADRÕES DE PROJETO:
//
//  1. OBSERVER PATTERN
//     A tela observa o uiState do HabitosViewModel via
//     collectAsState(). Quando carregarRanking() termina,
//     o StateFlow emite o novo estado e a tela se atualiza
//     automaticamente — sem nenhum "pull" manual.
//
//  2. STRATEGY PATTERN
//     A função calcularMensagemMotivacional() no ViewModel
//     usa a EstrategiaMsg injetada. A tela não sabe qual
//     estratégia está sendo usada — só chama a função.
// ─────────────────────────────────────────────────────────────

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yadot.viewmodel.HabitosViewModel
import com.example.yadot.viewmodel.RankingItem
import kotlin.math.roundToInt

// ════════════════════════════════════════════════════════════
//  TELA PRINCIPAL
// ════════════════════════════════════════════════════════════

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RankingOfensivaScreen(viewModel: HabitosViewModel) {

    // OBSERVER: observa o uiState do ViewModel
    // Sempre que carregarRanking() terminar, esta tela
    // se atualiza automaticamente com os dados novos.
    val uiState by viewModel.uiState.collectAsState()

    // Carrega o ranking assim que a tela abre
    LaunchedEffect(Unit) {
        viewModel.carregarRanking()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Ranking",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

        when {
            // Carregando — mostra spinner centralizado
            uiState.carregando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            // Erro — mostra mensagem
            uiState.erro != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.erro ?: "Erro desconhecido",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }

            // Sem usuários ainda
            uiState.ranking.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum usuário no ranking ainda.", color = Color.Gray, fontSize = 14.sp)
                }
            }

            // Ranking carregado — renderiza a lista
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(uiState.ranking) { index, item ->
                        ItemRanking(index = index, item = item)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "yadoT©",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 50.dp),
            fontSize = 14.sp,
            color = Color.LightGray
        )
    }
}

// ════════════════════════════════════════════════════════════
//  COMPONENTES INTERNOS
// ════════════════════════════════════════════════════════════

/** Card de cada posição do ranking */
@Composable
private fun ItemRanking(index: Int, item: RankingItem) {
    val isPrimeiro = index == 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isPrimeiro) Color(0xFFF2F2F2) else Color(0xFFF9F9F9),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        if (isPrimeiro) {
            // Card do 1º lugar — layout expandido
            Column {
                Text(
                    text = "#1 LUGAR",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(item.nome, fontSize = 32.sp, fontWeight = FontWeight.Black)
                        Text(
                            "${item.percentual}% concluído",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    }
                    IndicadorOfensiva(ofensivaDias = item.ofensivaDias, tamanho = 60)
                }
            }
        } else {
            // Card das demais posições
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${index + 1}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(28.dp)
                    )
                    Column {
                        Text(item.nome, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("${item.percentual}%", fontSize = 14.sp, color = Color.Gray)
                    }
                }
                IndicadorOfensiva(ofensivaDias = item.ofensivaDias, tamanho = 36)
            }
        }
    }
}

/**
 * Badge de ofensiva: círculo preto com o número de dias
 * consecutivos + label "dias" ao lado.
 * Recebe diretamente o valor real da API — sem conversão.
 */
@Composable
private fun IndicadorOfensiva(ofensivaDias: Int, tamanho: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(tamanho.dp)
                .background(Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$ofensivaDias",
                color = Color.White,
                fontSize = (tamanho * 0.45).sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "dias",
            fontSize = (tamanho * 0.35).sp,
            fontWeight = FontWeight.Light,
            color = Color.Gray
        )
    }
}

// ════════════════════════════════════════════════════════════
//  PREVIEW (dados fake — só para o Android Studio)
// ════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun RankingPreview() {
    // Preview com dados estáticos (não chama a API)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("Ranking", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(48.dp))

        val fakeLista = listOf(
            RankingItem("Lucas",   80, 6),
            RankingItem("Yuri",    75, 5),
            RankingItem("Sophia",  65, 4),
            RankingItem("Mateus",  60, 3),
            RankingItem("Gustavo", 55, 2),
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            itemsIndexed(fakeLista) { index, item ->
                ItemRanking(index = index, item = item)
            }
        }
    }
}