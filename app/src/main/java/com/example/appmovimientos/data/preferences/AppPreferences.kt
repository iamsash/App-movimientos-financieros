package com.example.appmovimientos.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("econote_prefs", Context.MODE_PRIVATE)

    private val _modoOscuroFlow = MutableStateFlow(prefs.getBoolean("modo_oscuro", false))
    val modoOscuroFlow: StateFlow<Boolean> = _modoOscuroFlow.asStateFlow()

    var notificaciones: Boolean
        get() = prefs.getBoolean("notificaciones", true)
        set(value) {
            prefs.edit().putBoolean("notificaciones", value).apply()
        }

    var modoOscuro: Boolean
        get() = prefs.getBoolean("modo_oscuro", false)
        set(value) {
            prefs.edit().putBoolean("modo_oscuro", value).apply()
            _modoOscuroFlow.value = value
        }

    var moneda: String
        get() = prefs.getString("moneda", "S/.") ?: "S/."
        set(value) {
            prefs.edit().putString("moneda", value).apply()
        }

    var formatoFecha: String
        get() = prefs.getString("formato_fecha", "DD/MM/AAAA") ?: "DD/MM/AAAA"
        set(value) {
            prefs.edit().putString("formato_fecha", value).apply()
        }
}
