package com.example.appmovimientos.ui.screens.reportes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovimientos.ui.components.AnimatedButton
import com.example.appmovimientos.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF667eea))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FF))
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AnimatedButton(
                "Gastos e ingresos por Categoría",
                Icons.Filled.CheckCircle,
                AppColors.PrimaryGradient,
                { navController.navigate("reporteCategoria") },
                Modifier.fillMaxWidth()
            )
            AnimatedButton(
                "Movimientos por Mes",
                Icons.Filled.DateRange,
                AppColors.SecondaryGradient,
                { navController.navigate("reporteMes") },
                Modifier.fillMaxWidth()
            )
            AnimatedButton(
                "Reporte Total Mensual",
                Icons.Filled.Star,
                AppColors.CategoryGradient,
                { navController.navigate("reporteTotal") },
                Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteCategoriaScreen(
    navController: NavController,
    viewModel: ReportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte por Categoría", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Reporte por Categoría (${uiState.movimientos.size} movimientos)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteMesScreen(
    navController: NavController,
    viewModel: ReportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movimientos por Mes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Movimientos por Mes (${uiState.movimientos.size} movimientos)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteTotalScreen(
    navController: NavController,
    viewModel: ReportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte Total Mensual", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Reporte Total Mensual (${uiState.movimientos.size} movimientos)")
            }
        }
    }
}
