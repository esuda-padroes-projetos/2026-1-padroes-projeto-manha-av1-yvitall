package com.example.yadot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = Preto,
    onPrimary = Branco,
    background = Branco,
    onBackground = Preto,
    surface = Branco,
    onSurface = Preto,
    error = VermelhoErro,
)

@Composable
fun YADOTTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}