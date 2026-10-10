package com.example.appmovimientos.ui.screens.more

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FF))
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color(0xFFEDE9FE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color(0xFF7C3AED),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6366F1))
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!uiState.isEditing) {
                            // Modo Vista
                            ProfileItem(label = "Nombre completo", value = uiState.nombre)
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            ProfileItem(label = "Correo electrónico", value = uiState.correo)
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            ProfileItem(label = "Nombre de usuario", value = uiState.usuario)

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.startEditing() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Editar perfil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Modo Edición
                            Text("Editar Datos Personales", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))

                            OutlinedTextField(
                                value = uiState.nombre,
                                onValueChange = { viewModel.onNombreChange(it) },
                                label = { Text("Nombre completo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = uiState.correo,
                                onValueChange = {},
                                label = { Text("Correo electrónico (No editable)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                enabled = false,
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = uiState.usuario,
                                onValueChange = { viewModel.onUsuarioChange(it) },
                                label = { Text("Nombre de usuario") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.cancelEditing() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !uiState.isSaving
                                ) {
                                    Text("Cancelar", color = Color(0xFF64748B))
                                }

                                Button(
                                    onClick = {
                                        viewModel.saveProfile {
                                            Toast.makeText(context, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                    enabled = !uiState.isSaving
                                ) {
                                    if (uiState.isSaving) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Guardar", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        uiState.errorMessage?.let {
                            Text(it, color = Color.Red, fontSize = 13.sp)
                        }

                        uiState.successMessage?.let {
                            Text(it, color = Color(0xFF10B981), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 16.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
    }
}
