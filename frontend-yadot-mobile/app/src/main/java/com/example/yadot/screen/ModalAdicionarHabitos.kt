package com.example.yadot.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yadot.ui.theme.Branco
import com.example.yadot.ui.theme.CinzaEscuro
import com.example.yadot.ui.theme.CinzaInativo
import com.example.yadot.ui.theme.FundoModal
import com.example.yadot.ui.theme.Preto
import com.example.yadot.ui.theme.VermelhoErro
import com.example.yadot.viewmodel.HabitosViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalAdicionarHabito(
    viewModel: HabitosViewModel,
    aoFechar: () -> Unit,
    aoSalvar: (String, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ========== ESTADOS DO VIEWMODEL ==========
    val estaCarregando by viewModel.estaCarregando.collectAsState()

    // ========== CAMPOS DO FORMULÁRIO ==========
    var nomeDoHabito by remember { mutableStateOf("") }
    var erroNome by remember { mutableStateOf(false) }
    var erroCategoria by remember { mutableStateOf(false) }
    var categoriaSelecionada by remember { mutableStateOf("") }
    var dropdownAberto by remember { mutableStateOf(false) }
    var iconeSelecionado by remember { mutableStateOf("Star") }
    var adicionarAoCalendar by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = aoFechar,
        sheetState = sheetState,
        containerColor = FundoModal
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            // ========== CABEÇALHO ==========
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = aoFechar,
                    enabled = !estaCarregando
                ) {
                    Text("Cancelar", color = VermelhoErro, fontWeight = FontWeight.Bold)
                }
                TextButton(
                    onClick = {
                        // Validação
                        var temErro = false

                        if (nomeDoHabito.isBlank()) {
                            erroNome = true
                            temErro = true
                        }

                        if (categoriaSelecionada.isBlank()) {
                            erroCategoria = true
                            temErro = true
                        }

                        if (!temErro) {
                            // Chama o callback que está conectado ao ViewModel
                            aoSalvar(nomeDoHabito.trim(), categoriaSelecionada, iconeSelecionado)
                        }
                    },
                    enabled = !estaCarregando
                ) {
                    if (estaCarregando) {
                        CircularProgressIndicator(
                            color = CinzaEscuro,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Salvar", color = CinzaEscuro, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========== TÍTULO ==========
            Text(
                text = "Adicionar Hábito",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Preto
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ========== CAMPO NOME ==========
            Text("Nome do hábito", fontWeight = FontWeight.Bold, color = CinzaEscuro)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nomeDoHabito,
                onValueChange = {
                    nomeDoHabito = it
                    erroNome = false
                },
                placeholder = { Text("Ex.: Estudar, Exercitar...") },
                isError = erroNome,
                supportingText = {
                    if (erroNome) Text("Digite um nome para o hábito", color = VermelhoErro)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !estaCarregando,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = Preto,
                    focusedBorderColor = Preto
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ========== DROPDOWN DE CATEGORIA ==========
            Text("Categoria", fontWeight = FontWeight.Bold, color = CinzaEscuro)
            Spacer(modifier = Modifier.height(8.dp))

            // Indicador de erro da categoria
            if (erroCategoria) {
                Text(
                    "Selecione uma categoria",
                    color = VermelhoErro,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            ExposedDropdownMenuBox(
                expanded = dropdownAberto,
                onExpandedChange = {
                    if (!estaCarregando) {
                        dropdownAberto = !dropdownAberto
                        erroCategoria = false
                    }
                }
            ) {
                OutlinedTextField(
                    value = categoriaSelecionada,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecione uma categoria") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownAberto)
                    },
                    isError = erroCategoria,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !estaCarregando,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedBorderColor = if (erroCategoria) VermelhoErro else Preto,
                        focusedBorderColor = if (erroCategoria) VermelhoErro else Preto
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropdownAberto,
                    onDismissRequest = { dropdownAberto = false }
                ) {
                    viewModel.categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = categoria,
                                    fontSize = 16.sp
                                )
                            },
                            onClick = {
                                categoriaSelecionada = categoria
                                dropdownAberto = false
                                erroCategoria = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========== GRID DE ÍCONES ==========
            Text("Ícone", fontWeight = FontWeight.Bold, color = CinzaEscuro)
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.iconesDisponiveis.isEmpty()) {
                Text(
                    "Nenhum ícone disponível",
                    color = CinzaInativo,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.iconesDisponiveis) { (nomeIcone, vetorIcone) ->
                        val selecionado = iconeSelecionado == nomeIcone
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selecionado) Preto else CinzaInativo)
                                .clickable(enabled = !estaCarregando) {
                                    iconeSelecionado = nomeIcone
                                }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = vetorIcone,
                                contentDescription = nomeIcone,
                                tint = if (selecionado) Branco else CinzaEscuro,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========== CHECKBOX GOOGLE CALENDAR (FUTURO) ==========
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = !estaCarregando) {
                        adicionarAoCalendar = !adicionarAoCalendar
                    }
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = adicionarAoCalendar,
                    onCheckedChange = { adicionarAoCalendar = it },
                    colors = CheckboxDefaults.colors(checkedColor = Preto),
                    enabled = !estaCarregando
                )
                Text(
                    text = "Adicionar ao Google Calendar",
                    fontWeight = FontWeight.Bold,
                    color = Preto,
                    fontSize = 14.sp
                )
            }

            // ========== ESPAÇO FINAL ==========
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}