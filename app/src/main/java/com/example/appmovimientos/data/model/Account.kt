package com.example.appmovimientos.data.model

data class Account(
    val id: String = "",
    val name: String = "",
    val type: String = "", // "Banco", "Efectivo", "Ahorros"
    val bank: String = "", // BCP, Interbank, etc.
    val balance: Double = 0.0
)
