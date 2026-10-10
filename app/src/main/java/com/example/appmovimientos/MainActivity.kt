package com.example.appmovimientos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.appmovimientos.navigation.AppNavigation
import com.example.appmovimientos.ui.theme.AppMovimientosTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            AppMovimientosTheme {
                AppNavigation()
            }
        }
    }

}


