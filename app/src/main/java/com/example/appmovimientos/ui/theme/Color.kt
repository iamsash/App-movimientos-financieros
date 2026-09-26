package com.example.appmovimientos.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Colores personalizados
object AppColors {
    val PrimaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
    )
    val SecondaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF11998e), Color(0xFF38ef7d))
    )
    val ExpenseGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFff758c), Color(0xFFff7eb3))
    )
    val IncomeGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF56ab2f), Color(0xFFa8e6cf))
    )
    val CategoryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFffecd2), Color(0xFFfcb69f))
    )
    val AccountGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
    )
    val ExpenseCard = Color(0xFFFFF3E0)
}
