package com.example.appmovimientos.ui.screens.cuentas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovimientos.data.model.Account
import com.example.appmovimientos.ui.components.AccountCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasScreen(
    navController: NavController,
    viewModel: CuentasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf<Account?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrar Cuentas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("nuevaCuenta") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva Cuenta")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.cuentas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.AccountBox,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No hay cuentas registradas", fontSize = 18.sp, color = Color.Gray)
                        Text("Presiona + para crear una", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(uiState.cuentas) { _, account ->
                        AccountCard(
                            account = account,
                            onEdit = { /* TODO: Implementar edición */ },
                            onDelete = { showDeleteDialog = account }
                        )
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { account ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Cuenta") },
            text = { Text("¿Estás seguro de que deseas eliminar la cuenta '${account.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount(
                            account = account,
                            onSuccess = {
                                Toast.makeText(context, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        )
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCuentaScreen(
    navController: NavController,
    viewModel: CuentasViewModel = viewModel()
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Banco") }
    var banco by remember { mutableStateOf("BCP") }
    var saldoInicial by remember { mutableStateOf("") }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedBanco by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val tiposCuenta = listOf("Banco", "Efectivo", "Ahorros", "Tarjeta de Crédito")
    val bancos = listOf("BCP", "Interbank", "BBVA", "Scotiabank", "Banco de la Nación", "BanBif", "Otro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Cuenta", fontWeight = FontWeight.Bold) },
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
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Crear Nueva Cuenta",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre de la cuenta") },
                        leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expandedTipo = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                when (tipo) {
                                    "Banco" -> Icons.Filled.Email
                                    "Efectivo" -> Icons.Filled.Done
                                    "Ahorros" -> Icons.Filled.ThumbUp
                                    else -> Icons.Filled.Email
                                },
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tipo)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = expandedTipo,
                            onDismissRequest = { expandedTipo = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            tiposCuenta.forEach { tipoItem ->
                                DropdownMenuItem(
                                    text = { Text(tipoItem) },
                                    onClick = {
                                        tipo = tipoItem
                                        expandedTipo = false
                                        if (tipoItem == "Efectivo") banco = "" else if (banco.isBlank()) banco = "BCP"
                                    },
                                    leadingIcon = {
                                        Icon(
                                            when (tipoItem) {
                                                "Banco" -> Icons.Filled.Email
                                                "Efectivo" -> Icons.Filled.Done
                                                "Ahorros" -> Icons.Filled.ThumbUp
                                                else -> Icons.Filled.Email
                                            },
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (tipo != "Efectivo") {
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedBanco = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (banco.isEmpty()) "Seleccionar banco" else banco)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = expandedBanco,
                                onDismissRequest = { expandedBanco = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                bancos.forEach { bancoItem ->
                                    DropdownMenuItem(
                                        text = { Text(bancoItem) },
                                        onClick = {
                                            banco = bancoItem
                                            expandedBanco = false
                                        },
                                        leadingIcon = { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = saldoInicial,
                        onValueChange = { saldoInicial = it },
                        label = { Text("Saldo inicial (S/.)") },
                        leadingIcon = { Icon(Icons.Filled.Done, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.createAccount(
                                nombre = nombre,
                                tipo = tipo,
                                banco = banco,
                                saldoInicial = saldoInicial,
                                onSuccess = {
                                    Toast.makeText(context, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = { errorMsg ->
                                    errorMessage = errorMsg
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = nombre.isNotBlank() && saldoInicial.isNotBlank()
                    ) {
                        Text("Crear Cuenta", fontSize = 16.sp)
                    }

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
