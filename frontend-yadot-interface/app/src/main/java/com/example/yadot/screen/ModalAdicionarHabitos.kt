package com.example.yadot.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    aoSalvar: (String, String, String, List<String>) -> Unit,
    erro: String? = null,
    carregando: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var nomeDoHabito by remember { mutableStateOf("") }
    var erroNome by remember { mutableStateOf(false) }
    var categoriaSelecionada by remember { mutableStateOf("") }
    var dropdownAberto by remember { mutableStateOf(false) }
    var iconeSelecionado by remember { mutableStateOf("Star") }
    var adicionarAoCalendar by remember { mutableStateOf(false) }

    val diasDisponiveis = viewModel.diasDaSemana
    var diasSelecionados by remember { mutableStateOf(listOf(viewModel.diaSelecionado)) }

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
            // Cabeçalho com Cancelar e Salvar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = aoFechar) {
                    Text("Cancelar", color = VermelhoErro, fontWeight = FontWeight.Bold)
                }
                TextButton(
                    onClick = {
                        if (nomeDoHabito.isNotBlank()) {
                            aoSalvar(nomeDoHabito.trim(), categoriaSelecionada, iconeSelecionado, diasSelecionados)
                        } else {
                            erroNome = true
                        }
                    },
                    enabled = !carregando
                ) {
                    if (carregando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = CinzaEscuro
                        )
                    } else {
                        Text("Salvar", color = CinzaEscuro, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Adicionar Hábito",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Preto
            )

            // Exibição de erro (se houver)
            if (erro != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = erro,
                    color = VermelhoErro,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo nome
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
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = Preto,
                    focusedBorderColor = Preto
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown de categoria
            Text("Categoria", fontWeight = FontWeight.Bold, color = CinzaEscuro)
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = dropdownAberto,
                onExpandedChange = { dropdownAberto = !dropdownAberto }
            ) {
                OutlinedTextField(
                    value = viewModel.categoriasDisplay[categoriaSelecionada] ?: categoriaSelecionada,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecione") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownAberto)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedBorderColor = Preto,
                        focusedBorderColor = Preto
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropdownAberto,
                    onDismissRequest = { dropdownAberto = false }
                ) {
                    viewModel.categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(viewModel.categoriasDisplay[categoria] ?: categoria) },
                            onClick = {
                                categoriaSelecionada = categoria
                                dropdownAberto = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dias da semana
            Text("Repetir nos dias", fontWeight = FontWeight.Bold, color = CinzaEscuro)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),   // <-- scroll horizontal
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                diasDisponiveis.forEach { dia ->
                    val ativo = dia in diasSelecionados
                    FilterChip(
                        selected = ativo,
                        onClick = {
                            diasSelecionados = if (ativo) {
                                diasSelecionados - dia
                            } else {
                                diasSelecionados + dia
                            }
                        },
                        label = { Text(dia) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Preto,
                            selectedLabelColor = Branco
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid de ícones
            Text("Ícone", fontWeight = FontWeight.Bold, color = CinzaEscuro)
            Spacer(modifier = Modifier.height(8.dp))
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
                            .clickable { iconeSelecionado = nomeIcone }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vetorIcone,
                            contentDescription = nomeIcone,
                            tint = if (selecionado) Branco else CinzaEscuro,
                            modifier = Modifier.size(24.dp)   // Ícone agora visível
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox Google Calendar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { adicionarAoCalendar = !adicionarAoCalendar }
            ) {
                Checkbox(
                    checked = adicionarAoCalendar,
                    onCheckedChange = { adicionarAoCalendar = it },
                    colors = CheckboxDefaults.colors(checkedColor = Preto)
                )
                Text(
                    text = "Adicionar ao Google Calendar",
                    fontWeight = FontWeight.Bold,
                    color = Preto
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}