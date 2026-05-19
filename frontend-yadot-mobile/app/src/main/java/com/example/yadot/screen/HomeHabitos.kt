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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.yadot.model.HabitoResponse
import com.example.yadot.ui.theme.Branco
import com.example.yadot.ui.theme.CinzaInativo
import com.example.yadot.ui.theme.FundoCinzaClaro
import com.example.yadot.ui.theme.Preto
import com.example.yadot.ui.theme.Transparente
import com.example.yadot.ui.theme.VerdeConcluido
import com.example.yadot.ui.theme.VermelhoErro
import com.example.yadot.viewmodel.HabitosViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SemHabitos(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HabitosViewModel = viewModel()  // ViewModel padrão se não for passado
) {
    // ========== ESTADOS DO VIEWMODEL ==========
    val habitosDoDia by viewModel.habitosDoDia.collectAsState()
    val progresso by viewModel.progresso.collectAsState()
    val estaCarregando by viewModel.estaCarregando.collectAsState()
    val mensagemErro by viewModel.mensagemErro.collectAsState()
    val usuarioLogado by viewModel.usuarioLogado.collectAsState()

    // ========== CARREGA DADOS AO INICIAR ==========
    LaunchedEffect(Unit) {
        viewModel.carregarHabitosDoDia()
        viewModel.carregarProgresso()
    }

    // ========== SE NÃO TIVER USUÁRIO, VOLTA PARA HOME ==========
    LaunchedEffect(usuarioLogado) {
        if (usuarioLogado == null) {
            navController.navigate(Rotas.HOME) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // ========== PORCENTAGEM CALCULADA ==========
    val porcentagemProgresso = viewModel.calcularPorcentagemProgresso().toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Branco)
            .padding(horizontal = 15.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ========== TÍTULO ==========
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Meus Hábitos",
                style = TextStyle(
                    fontSize = 50.sp,
                    color = Preto,
                    fontWeight = FontWeight.Bold
                )
            )
            // Botão de edição (aparece se tem hábitos)
            if (habitosDoDia.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Preto)
                        .clickable { viewModel.alternarModoEdicao() }
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = if (viewModel.modoEdicao) Icons.Filled.Check else Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = Branco,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // ========== MENSAGEM DE ERRO ==========
        mensagemErro?.let { erro ->
            Text(
                text = erro,
                color = VermelhoErro,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // ========== INDICADOR DE CARREGAMENTO ==========
        if (estaCarregando) {
            CircularProgressIndicator(
                color = Preto,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(3f))

        // ========== CONTEÚDO CENTRAL ==========
        if (!estaCarregando && habitosDoDia.isEmpty()) {
            // Estado vazio
            Column(
                modifier = Modifier.padding(top = 40.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Nenhum hábito encontrado",
                    style = TextStyle(
                        fontSize = 25.sp,
                        color = Preto,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    "Cadastre agora!",
                    style = TextStyle(
                        fontSize = 25.sp,
                        color = Preto,
                        fontWeight = FontWeight.Normal
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 50.dp, bottomEnd = 50.dp))
                        .clickable { viewModel.abrirModal() }
                        .background(Preto)
                        .padding(horizontal = 60.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+",
                        style = TextStyle(
                            fontSize = 80.sp,
                            color = Branco,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        } else {
            // Lista de hábitos
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(18f),
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
                        // Botão remover (modo edição)
                        if (viewModel.modoEdicao) {
                            Icon(
                                imageVector = Icons.Filled.RemoveCircle,
                                contentDescription = "Remover",
                                tint = VermelhoErro,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable { viewModel.deletarHabito(habito.habitId) }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        // Nome do hábito
                        Text(
                            text = habito.habitName,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Preto,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Círculo de check-in (fora do modo edição)
                        if (!viewModel.modoEdicao) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Transparente)  // Sempre começa transparente
                                    .border(
                                        width = 2.dp,
                                        color = CinzaInativo,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        // FAZ CHECK-IN NA API
                                        viewModel.realizarCheckin(habito.habitId)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Check aparece se já fez check-in hoje
                                // (precisa verificar com a API - por enquanto só mostra após clique)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(5f))

        // ========== BOTÃO CADASTRAR (MODO EDIÇÃO) ==========
        if (viewModel.modoEdicao) {
            Button(
                onClick = { viewModel.abrirModal() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Preto)
            ) {
                Text(
                    text = "Cadastrar Hábito",
                    color = Branco,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ========== GRÁFICO DE PROGRESSO ==========
        GraficoProgresso(porcentagem = porcentagemProgresso)

        Spacer(modifier = Modifier.weight(5f))

        // ========== RODAPÉ ==========
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Branco),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "yadoT©", color = Preto, fontSize = 15.sp)
        }
    }

    // ========== MODAL DE ADICIONAR HÁBITO ==========
    if (viewModel.mostrarModal) {
        ModalAdicionarHabito(
            viewModel = viewModel,
            aoFechar = { viewModel.fecharModal() },
            aoSalvar = { nome, categoria, icone ->
                viewModel.adicionarHabito(nome, categoria, icone)
            }
        )
    }
}

// ========== COMPONENTES AUXILIARES (MANTIDOS IGUAIS) ==========

@Composable
fun BarraDeDias(diaAtual: String, aoClicarNoDia: (String) -> Unit) {
    val diasDaSemana = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 1.dp)
    ) {
        items(diasDaSemana) { dia ->
            val corFundo = if (dia == diaAtual) Preto else CinzaInativo
            val corTexto = if (dia == diaAtual) Branco else Preto

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { aoClicarNoDia(dia) }
                    .background(corFundo)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dia,
                    style = TextStyle(fontSize = 20.sp, color = corTexto, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun GraficoProgresso(porcentagem: Int) {
    val progressoAnimado by animateFloatAsState(
        targetValue = porcentagem.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "animacao_progresso"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Progresso Atual",
            style = TextStyle(
                fontSize = 28.sp,
                color = Preto,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val espessuraDaLinha = 24.dp.toPx()

                drawArc(
                    color = FundoCinzaClaro,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = espessuraDaLinha)
                )

                if (progressoAnimado > 0f) {
                    drawArc(
                        color = VerdeConcluido,
                        startAngle = -90f,
                        sweepAngle = (progressoAnimado / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = espessuraDaLinha, cap = StrokeCap.Round)
                    )
                }
            }

            Text(
                text = "$porcentagem%",
                style = TextStyle(fontSize = 20.sp, color = Preto, fontWeight = FontWeight.Bold)
            )
        }
    }
}