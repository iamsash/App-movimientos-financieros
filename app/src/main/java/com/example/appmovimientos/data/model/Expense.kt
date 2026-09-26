package com.example.appmovimientos.data.model

data class Expense(
    val id: String = "",
    val amount: String = "",
    val description: String = "",
    val categoryId: String = "",
    val accountId: String = "",
    val date: String = "Hoy",
    val type: String = "Gasto" // Ingreso o Gasto
)
