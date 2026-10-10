package com.example.appmovimientos.ui.screens.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appmovimientos.R

@Composable
fun TechFinancialBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF13092A), // Dark navy purple top
                        Color(0xFF0D061D), // Deep dark center
                        Color(0xFF190B36)  // Rich purple bottom
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Diagonal and polygonal grid lines
            drawLine(
                color = Color(0xFFC084FC).copy(alpha = 0.25f),
                start = androidx.compose.ui.geometry.Offset(0f, height * 0.2f),
                end = androidx.compose.ui.geometry.Offset(width * 0.7f, 0f),
                strokeWidth = 2f
            )
            drawLine(
                color = Color(0xFF9333EA).copy(alpha = 0.2f),
                start = androidx.compose.ui.geometry.Offset(width * 0.3f, height),
                end = androidx.compose.ui.geometry.Offset(width, height * 0.6f),
                strokeWidth = 1.5f
            )
            drawLine(
                color = Color(0xFFE879F9).copy(alpha = 0.2f),
                start = androidx.compose.ui.geometry.Offset(width, height * 0.2f),
                end = androidx.compose.ui.geometry.Offset(width * 0.4f, height * 0.8f),
                strokeWidth = 2f
            )

            // Draw subtle ascending financial bar charts on top right
            val barWidth = 18f
            val barSpacing = 12f
            val startX = width - 150f
            val startY = height * 0.25f
            val heights = listOf(40f, 65f, 95f, 130f, 175f)

            heights.forEachIndexed { index, h ->
                val x = startX + (index * (barWidth + barSpacing))
                val y = startY - h
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE879F9).copy(alpha = 0.3f),
                            Color(0xFF9333EA).copy(alpha = 0.15f)
                        )
                    ),
                    topLeft = androidx.compose.ui.geometry.Offset(x, y + 100f),
                    size = androidx.compose.ui.geometry.Size(barWidth, h)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TechFinancialBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Logo personalizado ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("ECONOTE", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = { viewModel.onCorreoChange(it) },
                        label = { Text("Correo electrónico o Usuario") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.contrasena,
                        onValueChange = { viewModel.onContrasenaChange(it) },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.toggleMostrarContrasena() }) {
                                Icon(
                                    if (uiState.mostrarContrasena) Icons.Filled.Lock else Icons.Filled.Lock,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (uiState.mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.login(
                                onNavigateMain = {
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateTerminos = {
                                    navController.navigate("legacyAceptacion") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Iniciar Sesión", fontSize = 16.sp, color = Color.White)
                        }
                    }

                    uiState.errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { navController.navigate("registroUsuario") }) {
                        Text("¿No tienes cuenta?", color = Color.Gray)
                        Text("Regístrate aquí", color = Color.Red)
                    }
                }
            }
        }
    }
}
