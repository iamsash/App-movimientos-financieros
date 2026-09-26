package com.example.appmovimientos.data.model

data class Category(
    val id: String = "",
    val name: String = "",
    val type: String = "Gasto", // "Gasto" o "Ingreso"
    val icon: String = "ShoppingCart",
    val color: String = "#FF5722"
)
