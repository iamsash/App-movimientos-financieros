package com.example.appmovimientos.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmovimientos.ui.screens.categorias.CategoriasScreen
import com.example.appmovimientos.ui.screens.categorias.NuevaCategoriaScreen
import com.example.appmovimientos.ui.screens.cuentas.CuentasScreen
import com.example.appmovimientos.ui.screens.cuentas.NuevaCuentaScreen
import com.example.appmovimientos.ui.screens.login.LoginScreen
import com.example.appmovimientos.ui.screens.main.MainScreen
import com.example.appmovimientos.ui.screens.movimientos.ListaScreen
import com.example.appmovimientos.ui.screens.movimientos.RegistroScreen
import com.example.appmovimientos.ui.screens.registro.RegistroUsuarioScreen
import com.example.appmovimientos.ui.screens.reportes.ReporteCategoriaScreen
import com.example.appmovimientos.ui.screens.reportes.ReporteMesScreen
import com.example.appmovimientos.ui.screens.reportes.ReporteTotalScreen
import com.example.appmovimientos.ui.screens.reportes.ReportesScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val startDestination = if (currentUser != null) "main" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("main") { MainScreen(navController) }
        composable("registro") { RegistroScreen(navController) }
        composable("lista") { ListaScreen(navController) }
        composable("registroUsuario") { RegistroUsuarioScreen(navController) }
        composable("categorias") { CategoriasScreen(navController) }
        composable("nuevaCategoria") { NuevaCategoriaScreen(navController) }
        composable("cuentas") { CuentasScreen(navController) }
        composable("nuevaCuenta") { NuevaCuentaScreen(navController) }
        composable("reportes") { ReportesScreen(navController) }
        composable("reporteCategoria") { ReporteCategoriaScreen(navController) }
        composable("reporteMes") { ReporteMesScreen(navController) }
        composable("reporteTotal") { ReporteTotalScreen(navController) }
    }
}
