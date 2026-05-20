package com.example.yadot.screen

import androidx.compose.foundation.clickable // ← Certifique-se de ter esse import no topo do arquivo!
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Cancel

private val Inter          = FontFamily.Default
private val DiaPreto       = Color(0xFF1A1A1A)
private val DiaBranco      = Color.White
private val DiaVerde       = Color(0xFF4CAF50)
private val DiaCinzaTexto  = Color(0xFFAAAAAA)
private val DiaCinzaFundo  = Color(0xFFE0E0E0)

// ════════════════════════════════════════════════════════════
//  PADRÕES DE PROJETO
//
//  1. STATE PATTERN — EstadoDia define o comportamento de cada
//     dia no scroll:
//       DiaAnterior → esta tela (somente leitura/congelada)
//       DiaAtual    → SemHabitos.kt (editável)
//       DiaFuturo   → SemHabitos.kt (cadastro)
//
//  2. OBSERVER PATTERN — ViewModel expõe StateFlow<UiState>.
//     collectAsState() re-renderiza automaticamente.
// ════════════════════════════════════════════════════════════

// ════════════════════════════════════════════════════════════
//  SEÇÃO 1 — MODELOS DE DADOS
// ════════════════════════════════════════════════════════════

data class HabitoDiaAnterior(
    val id: String,
    val nome: String,
    val icone: ImageVector,
    val foiConcluido: Boolean,
    val categoria: CategoriaHabito
)

enum class CategoriaHabito(val label: String) {
    EDUCACAO("Educação"),
    SAUDE("Saúde"),
    RESPONSABILIDADES("Responsabilidades")
}

data class DiaAnteriorUiState(
    val labelSemana: String = "Week 1",
    val diaAtual: String = "Seg",
    val habitos: List<HabitoDiaAnterior> = emptyList(),
    val progressoPercent: Int = 0,
    val ofensiva: Int = 1,
    val concluidosCount: Int = 0,
    val totalCount: Int = 0,
    val progressoCategoria: List<ProgressoCategoria> = emptyList(),
    val mensagemMotivacional: String = ""
)

data class ProgressoCategoria(
    val categoria: CategoriaHabito,
    val percent: Int
)

// ════════════════════════════════════════════════════════════
//  SEÇÃO 2 — STATE PATTERN
// ════════════════════════════════════════════════════════════

sealed class EstadoDia {
    object DiaAnterior : EstadoDia()
    object DiaAtual    : EstadoDia()
    object DiaFuturo   : EstadoDia()
}

// ════════════════════════════════════════════════════════════
//  SEÇÃO 3 — TELA PRINCIPAL
// ════════════════════════════════════════════════════════════

@Composable
fun TelaDiaAnterior(
    uiState: DiaAnteriorUiState,
    aoClicarNoDia: (String) -> Unit = {}
) {
    var animacaoIniciada by remember { mutableStateOf(false) }
    val progressoAnimado by animateFloatAsState(
        targetValue = if (animacaoIniciada) uiState.progressoPercent / 100f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "animacao_progresso"
    )
    LaunchedEffect(Unit) { animacaoIniciada = true }

    val config        = LocalConfiguration.current
    val altTela       = config.screenHeightDp.dp
    val largTela      = config.screenWidthDp.dp
    val espacoPequeno = altTela * 0.010f
    val espacoMedio   = altTela * 0.018f
    val espacoGrande  = altTela * 0.025f
    val paddingHoriz  = largTela * 0.055f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DiaBranco)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = paddingHoriz)
    ) {
        Spacer(Modifier.height(13.dp))

        // "Week 1" — 50sp
        Text(
            text = uiState.labelSemana,
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 50.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        )

        Spacer(Modifier.height(espacoPequeno))

        BarraDeDias(
            diaAtual = uiState.diaAtual,
            aoClicarNoDia = aoClicarNoDia
        )

        Spacer(Modifier.height(espacoMedio))

        SecaoListaHabitos(habitos = uiState.habitos)

        Spacer(Modifier.height(espacoGrande))

        SecaoProgresso(
            uiState = uiState,
            progressoAnimado = progressoAnimado,
            largTela = largTela
        )

        Spacer(Modifier.height(espacoGrande))
    }
}

// ════════════════════════════════════════════════════════════
//  SEÇÃO 4 — LISTA DE HÁBITOS
// ════════════════════════════════════════════════════════════

@Composable
private fun SecaoListaHabitos(habitos: List<HabitoDiaAnterior>) {
    Text(
        text = "Daily Tasks",
        style = TextStyle(
            fontFamily = Inter,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    )
    Spacer(Modifier.height(6.dp))
    habitos.forEach { habito ->
        LinhaHabito(habito = habito)
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun LinhaHabito(habito: HabitoDiaAnterior) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = habito.icone,
            contentDescription = habito.nome,
            tint = if (habito.foiConcluido) DiaCinzaTexto else Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = habito.nome,
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                textDecoration = if (habito.foiConcluido)
                    TextDecoration.LineThrough else TextDecoration.None,
                color = if (habito.foiConcluido) DiaCinzaTexto else Color.Black
            ),
            modifier = Modifier.weight(1f)
        )
        if (!habito.foiConcluido) {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = "Não concluído",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
//  SEÇÃO 3.5 — COMPONENTE COMPLEMENTAR: BARRA DE DIAS
// ════════════════════════════════════════════════════════════

@Composable
fun BarraDeDias(
    diaAtual: String,
    aoClicarNoDia: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dias = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        dias.forEach { dia ->
            val selecionado = (dia == diaAtual)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (selecionado) DiaPreto else Color.Transparent)
                    .clickable { aoClicarNoDia(dia) }
            ) {
                Text(
                    text = dia,
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 14.sp,
                        fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Medium,
                        color = if (selecionado) DiaBranco else DiaPreto
                    )
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
//  SEÇÃO 5 — PROGRESSO ATUAL
// ════════════════════════════════════════════════════════════

@Composable
private fun SecaoProgresso(
    uiState: DiaAnteriorUiState,
    progressoAnimado: Float,
    largTela: Dp
) {
    val tamanhoGrafico = largTela * 0.33f
    val tamanhoBadge   = largTela * 0.25f

    Text(
        text = "Progresso Atual",
        style = TextStyle(fontFamily = Inter, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = uiState.mensagemMotivacional,
        style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = Color.Gray),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GraficoCircularProgresso(progresso = progressoAnimado, percentual = uiState.progressoPercent, tamanho = tamanhoGrafico)
        BadgeOfensiva(count = uiState.ofensiva, tamanho = tamanhoBadge)
    }
    Spacer(Modifier.height(10.dp))
    Text(
        text = "Você concluiu ${uiState.concluidosCount} de ${uiState.totalCount} tarefas",
        style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = Color.Gray),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(16.dp))
    LinhaCategoriasProgresso(categorias = uiState.progressoCategoria)
}

// ════════════════════════════════════════════════════════════
//  SEÇÃO 6 — CATEGORIAS
// ════════════════════════════════════════════════════════════

@Composable
private fun LinhaCategoriasProgresso(categorias: List<ProgressoCategoria>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        categorias.forEach { cat ->
            val largura = when (cat.categoria) {
                CategoriaHabito.EDUCACAO          -> 57.dp
                CategoriaHabito.SAUDE             -> 37.dp
                CategoriaHabito.RESPONSABILIDADES -> 109.dp
            }
            ItemCategoria(progressoCategoria = cat, largura = largura)
        }
    }
}

@Composable
private fun ItemCategoria(progressoCategoria: ProgressoCategoria, largura: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(largura)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp)) {
            Canvas(modifier = Modifier.size(44.dp)) {
                val sw = 4.dp.toPx()
                val diam = size.minDimension - sw
                val tl = Offset(sw / 2, sw / 2)
                val sz = Size(diam, diam)
                drawArc(color = DiaCinzaFundo, startAngle = -90f, sweepAngle = 360f, useCenter = false, topLeft = tl, size = sz, style = Stroke(width = sw, cap = StrokeCap.Round))
                drawArc(color = DiaVerde, startAngle = -90f, sweepAngle = 360f * (progressoCategoria.percent / 100f), useCenter = false, topLeft = tl, size = sz, style = Stroke(width = sw, cap = StrokeCap.Round))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(text = progressoCategoria.categoria.label, style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = Color.Gray), textAlign = TextAlign.Center, softWrap = true, maxLines = 2)
        Text(text = "${progressoCategoria.percent}%", style = TextStyle(fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Black), textAlign = TextAlign.Center)
    }
}

// ════════════════════════════════════════════════════════════
//  SEÇÃO 7 — GRÁFICO CIRCULAR
// ════════════════════════════════════════════════════════════

@Composable
private fun GraficoCircularProgresso(progresso: Float, percentual: Int, tamanho: Dp) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(tamanho)) {
        Canvas(modifier = Modifier.size(tamanho)) {
            val sw = (tamanho.value * 0.11f).dp.toPx()
            val diam = this.size.minDimension - sw
            val tl = Offset(sw / 2, sw / 2)
            val sz = Size(diam, diam)
            drawArc(color = DiaCinzaFundo, startAngle = -90f, sweepAngle = 360f, useCenter = false, topLeft = tl, size = sz, style = Stroke(width = sw, cap = StrokeCap.Round))
            drawArc(color = DiaVerde, startAngle = -90f, sweepAngle = 360f * progresso, useCenter = false, topLeft = tl, size = sz, style = Stroke(width = sw, cap = StrokeCap.Round))
        }
        Text(text = "$percentual%", style = TextStyle(fontFamily = Inter, fontSize = (tamanho.value * 0.16f).sp, fontWeight = FontWeight.Bold, color = Color.Black))
    }
}

// ════════════════════════════════════════════════════════════
//  SEÇÃO 8 — BADGE OFENSIVA
// ════════════════════════════════════════════════════════════

@Composable
private fun BadgeOfensiva(count: Int, tamanho: Dp) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(tamanho).clip(CircleShape).background(DiaPreto)) {
            Text(text = "🔥", fontSize = (tamanho.value * 0.42f).sp)
        }
        Spacer(Modifier.width(10.dp))
        Text(text = "$count", style = TextStyle(fontFamily = Inter, fontSize = (tamanho.value * 0.55f).sp, fontWeight = FontWeight.ExtraBold, color = Color.Black))
    }
}

// ════════════════════════════════════════════════════════════
//  SEÇÃO 9 — PREVIEW
// ════════════════════════════════════════════════════════════

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewTelaDiaAnterior() {
    TelaDiaAnterior(
        uiState = DiaAnteriorUiState(
            labelSemana = "Week 1", diaAtual = "Seg",
            habitos = listOf(
                HabitoDiaAnterior("1", "5h30",      Icons.Default.WbSunny,       true,  CategoriaHabito.SAUDE),
                HabitoDiaAnterior("2", "Creatina",  Icons.Default.Science,       true,  CategoriaHabito.SAUDE),
                HabitoDiaAnterior("3", "Faculdade", Icons.Default.School,        true,  CategoriaHabito.EDUCACAO),
                HabitoDiaAnterior("4", "Trabalho",  Icons.Default.Work,          true,  CategoriaHabito.RESPONSABILIDADES),
                HabitoDiaAnterior("5", "Academia",  Icons.Default.FitnessCenter, false, CategoriaHabito.SAUDE),
                HabitoDiaAnterior("6", "Estudos",   Icons.Default.MenuBook,      true,  CategoriaHabito.EDUCACAO),
            ),
            progressoPercent = 80, ofensiva = 1, concluidosCount = 5, totalCount = 6,
            mensagemMotivacional = "Dia super produtivo! O ritmo está excelente.",
            progressoCategoria = listOf(
                ProgressoCategoria(CategoriaHabito.EDUCACAO, 100),
                ProgressoCategoria(CategoriaHabito.SAUDE, 50),
                ProgressoCategoria(CategoriaHabito.RESPONSABILIDADES, 100),
            )
        )
    )
}
