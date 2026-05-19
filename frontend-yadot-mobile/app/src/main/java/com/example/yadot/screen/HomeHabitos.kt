package com.example.yadot.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.yadot.Rotas
import com.example.yadot.network.HabitoResponse
import com.example.yadot.ui.theme.*
import com.example.yadot.viewmodel.HabitosViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SemHabitos(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HabitosViewModel = viewModel()
) {
    // Lê tudo do uiState centralizado
    val uiState by viewModel.uiState.collectAsState()
    val modoEdicao by viewModel.modoEdicao.collectAsState()
    val mostrarModal by viewModel.mostrarModal.collectAsState()

    val habitosDoDia = uiState.habitosDeHoje
    val estaCarregando = uiState.carregando
    val mensagemErro = uiState.erro
    val usuarioLogado = uiState.usuarioLogado

    LaunchedEffect(Unit) {
        viewModel.carregarHabitosDeHoje()
        viewModel.carregarProgressoDeHoje()
    }

    // Redireciona se não estiver logado
    LaunchedEffect(usuarioLogado) {
        if (usuarioLogado == null) {
            navController.navigate(Rotas.HOME) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val progresso = uiState.progressoHoje
    val porcentagemProgresso = if ((progresso?.total ?: 0L) > 0L)
        ((progresso!!.concluidos.toDouble() / progresso.total) * 100).toInt()
    else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Branco)
            .padding(horizontal = 15.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Meus Hábitos",
                style = TextStyle(fontSize = 36.sp, color = Preto, fontWeight = FontWeight.Bold)
            )
            if (habitosDoDia.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Preto)
                        .clickable { viewModel.alternarModoEdicao() }
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = if (modoEdicao) Icons.Filled.Check else Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = Branco,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        mensagemErro?.let { erro ->
            Text(text = erro, color = VermelhoErro, fontSize = 14.sp)
        }

        if (estaCarregando) {
            CircularProgressIndicator(color = Preto, modifier = Modifier.padding(16.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (!estaCarregando && habitosDoDia.isEmpty()) {
            Column(
                modifier = Modifier.padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Nenhum hábito", style = TextStyle(fontSize = 25.sp, color = Preto, fontWeight = FontWeight.Bold))
                Text("Cadastre agora!", style = TextStyle(fontSize = 25.sp, color = Preto))
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 50.dp, bottomEnd = 50.dp))
                        .clickable { viewModel.abrirModal() }
                        .background(Preto)
                        .padding(horizontal = 60.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = TextStyle(fontSize = 80.sp, color = Branco, fontWeight = FontWeight.Bold))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(habitosDoDia) { habito: HabitoResponse ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Branco)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (modoEdicao) {
                            Icon(
                                imageVector = Icons.Filled.RemoveCircle,
                                contentDescription = "Remover",
                                tint = VermelhoErro,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable { viewModel.removerHabito(habito.habitId) }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Text(
                            text = habito.habitName,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Preto,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        if (!modoEdicao) {
                            // BUG CORRIGIDO — Box agora tem bloco de conteúdo { }
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Transparente)
                                    .border(2.dp, CinzaInativo, CircleShape)
                                    .clickable { viewModel.realizarCheckin(habito.habitId) },
                                contentAlignment = Alignment.Center
                            ) { } // ← bloco vazio mas obrigatório
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (modoEdicao) {
            Button(
                onClick = { viewModel.abrirModal() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Preto)
            ) {
                Text("Cadastrar Hábito", color = Branco, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        GraficoProgresso(porcentagem = porcentagemProgresso)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "yadoT©", color = Preto, fontSize = 15.sp)
    }

    if (mostrarModal) {
        ModalAdicionarHabito(
            viewModel = viewModel,
            aoFechar = { viewModel.fecharModal() },
            aoSalvar = { nome, categoria, icone, dias ->
                viewModel.adicionarHabito(nome, categoria, icone, dias)
            }
        )
    }
}

@Composable
fun GraficoProgresso(porcentagem: Int) {
    val progressoAnimado by animateFloatAsState(
        targetValue = porcentagem.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "animacao"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Progresso Atual", style = TextStyle(fontSize = 28.sp, color = Preto, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(20.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val espessura = 24.dp.toPx()
                drawArc(FundoCinzaClaro, 0f, 360f, false, style = Stroke(espessura))
                if (progressoAnimado > 0f) {
                    drawArc(VerdeConcluido, -90f, (progressoAnimado / 100f) * 360f, false,
                        style = Stroke(espessura, cap = StrokeCap.Round))
                }
            }
            Text("$porcentagem%", style = TextStyle(fontSize = 20.sp, color = Preto, fontWeight = FontWeight.Bold))
        }
    }
}