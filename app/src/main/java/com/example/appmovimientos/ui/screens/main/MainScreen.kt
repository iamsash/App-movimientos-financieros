package com.example.appmovimientos.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appmovimientos.R
import com.example.appmovimientos.data.model.Account
import com.example.appmovimientos.data.model.Expense
import com.example.appmovimientos.ui.components.AnimatedButton
import com.example.appmovimientos.ui.theme.AppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val cuentas = uiState.cuentas
    val movimientos = uiState.movimientos
    val balanceTotal = uiState.balanceTotal
    val gastoTotal = uiState.gastoTotal
    val ingresoTotal = uiState.ingresoTotal
    var cuentaSeleccionada by remember { mutableStateOf<Account?>(null) }

    val periodoSeleccionado = uiState.periodoSeleccionado
    var expandedPeriodo by remember { mutableStateOf(false) }
    val gastosMensuales = uiState.gastosMensuales
    val categoriasMasGastadas = uiState.categoriasMasGastadas
    val categorias = uiState.categorias
    val movimientosFiltrados = viewModel.getFilteredMovimientos()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = Color.White
            ) {
                EconoteDrawerContent(
                    currentRoute = "main",
                    onItemSelected = { route ->
                        scope.launch { drawerState.close() }
                        if (route != "main") {
                            navController.navigate(route)
                        }
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        viewModel.signOut {
                            navController.navigate("login") {
                                popUpTo("main") { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ECONOTE", fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("perfil") }) {
                            Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color(0xFF6366F1))
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 📊 Card de Balance Total
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(AppColors.PrimaryGradient)
                                    .padding(24.dp)
                            ) {
                                Column {
                                    Text(
                                        "Balance Total",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        "S/. %.2f".format(balanceTotal),
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Filled.KeyboardArrowUp,
                                                    contentDescription = null,
                                                    tint = Color(0xFF4CAF50),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text("Ingresos", color = Color.White, fontSize = 14.sp)
                                            }
                                            Text(
                                                "S/. %.2f".format(ingresoTotal),
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Filled.ArrowDropDown,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFF5722),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text("Gastos", color = Color.White, fontSize = 14.sp)
                                            }
                                            Text(
                                                "S/. %.2f".format(gastoTotal),
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 🆕 Selector de Período
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Período de análisis",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box {
                                    OutlinedButton(
                                        onClick = { expandedPeriodo = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Filled.DateRange, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(periodoSeleccionado)
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                    }
                                    DropdownMenu(
                                        expanded = expandedPeriodo,
                                        onDismissRequest = { expandedPeriodo = false }
                                    ) {
                                        listOf("Mes Actual", "Últimos 3 Meses", "Último Año", "Todo").forEach { periodo ->
                                            DropdownMenuItem(
                                                text = { Text(periodo) },
                                                onClick = {
                                                    viewModel.setPeriodo(periodo)
                                                    expandedPeriodo = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 🆕 Top 5 Categorías con Más Gastos
                    if (categoriasMasGastadas.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Top 5 Categorías",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                        TextButton(onClick = { navController.navigate("reporteCategoria") }) {
                                            Text("Ver todo", fontSize = 12.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    categoriasMasGastadas.forEach { (categoriaId, monto) ->
                                        val categoria = categorias[categoriaId]
                                        val porcentaje = if (gastoTotal > 0) (monto / gastoTotal) * 100 else 0.0

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                when (categoria?.icon) {
                                                    "Work" -> Icons.Filled.Build
                                                    "Home" -> Icons.Filled.Home
                                                    "Food" -> Icons.Filled.Place
                                                    "Transport" -> Icons.Filled.Face
                                                    "Health" -> Icons.Filled.Favorite
                                                    else -> Icons.Filled.Menu
                                                },
                                                contentDescription = null,
                                                tint = Color(0xFFFF5722),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    categoria?.name ?: "Sin categoría",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(6.dp)
                                                        .background(Color(0xFFE0E0E0), RoundedCornerShape(3.dp))
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth(fraction = (porcentaje / 100).toFloat())
                                                            .height(6.dp)
                                                            .background(
                                                                AppColors.ExpenseGradient,
                                                                RoundedCornerShape(3.dp)
                                                            )
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    "S/. %.0f".format(monto),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFF5722)
                                                )
                                                Text(
                                                    "%.1f%%".format(porcentaje),
                                                    fontSize = 11.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 🆕 Gráfico de Tendencia Mensual
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Tendencia de Gastos",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                    TextButton(onClick = { navController.navigate("reporteTotal") }) {
                                        Text("Ver detalle", fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                val ultimos6Meses = gastosMensuales.toList()
                                    .sortedByDescending { it.first }
                                    .take(6)
                                    .reversed()

                                if (ultimos6Meses.isNotEmpty()) {
                                    val maxGasto = ultimos6Meses.maxOfOrNull { it.second } ?: 1.0

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        ultimos6Meses.forEach { (mesAnio, monto) ->
                                            val altura = ((monto / maxGasto) * 100).toFloat()
                                            val mes = mesAnio.split("/").firstOrNull() ?: ""
                                            val nombreMes = when(mes) {
                                                "1" -> "Ene"
                                                "2" -> "Feb"
                                                "3" -> "Mar"
                                                "4" -> "Abr"
                                                "5" -> "May"
                                                "6" -> "Jun"
                                                "7" -> "Jul"
                                                "8" -> "Ago"
                                                "9" -> "Sep"
                                                "10" -> "Oct"
                                                "11" -> "Nov"
                                                "12" -> "Dic"
                                                else -> mes
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(32.dp)
                                                        .height((altura * 0.8f).dp)
                                                        .background(
                                                            AppColors.ExpenseGradient,
                                                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                                        )
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    nombreMes,
                                                    fontSize = 10.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        "No hay datos suficientes",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 32.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 📱 Botones de Acción Principales
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AnimatedButton(
                                    text = "Registrar",
                                    icon = Icons.Filled.Add,
                                    gradient = AppColors.ExpenseGradient,
                                    onClick = { navController.navigate("registro") },
                                    modifier = Modifier.weight(1f)
                                )
                                AnimatedButton(
                                    text = "Categorías",
                                    icon = Icons.Filled.Menu,
                                    gradient = AppColors.CategoryGradient,
                                    onClick = { navController.navigate("categorias") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            AnimatedButton(
                                text = "Ver Reportes Completos",
                                icon = Icons.Filled.Star,
                                gradient = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFF6B6B), Color(0xFFFFE66D))
                                ),
                                onClick = { navController.navigate("reportes") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // 💳 Sección de Cuentas
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Mis Cuentas (${cuentas.size})",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = { navController.navigate("cuentas") }) {
                                    Text("Administrar")
                                }
                            }

                            if (cuentas.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Filled.MailOutline,
                                                contentDescription = null,
                                                modifier = Modifier.size(48.dp),
                                                tint = Color.Gray
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("No hay cuentas", color = Color.Gray, fontSize = 16.sp)
                                            TextButton(onClick = { navController.navigate("nuevaCuenta") }) {
                                                Text("Crear primera cuenta")
                                            }
                                        }
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    cuentas.take(3).forEach { cuenta ->
                                        CuentaCardCompacta(
                                            cuenta = cuenta,
                                            onClick = { cuentaSeleccionada = cuenta }
                                        )
                                    }
                                    if (cuentas.size > 3) {
                                        TextButton(
                                            onClick = { navController.navigate("cuentas") },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Ver todas las cuentas (${cuentas.size})")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 📋 Movimientos Recientes
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Movimientos Recientes",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                TextButton(onClick = { navController.navigate("lista") }) {
                                    Text("Ver todos")
                                }
                            }

                            if (movimientosFiltrados.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Filled.DateRange,
                                                contentDescription = null,
                                                modifier = Modifier.size(48.dp),
                                                tint = Color.Gray
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("No hay movimientos", color = Color.Gray, fontSize = 16.sp)
                                        }
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    movimientosFiltrados.take(5).forEach { movimiento ->
                                        MovimientoCardCompacto(
                                            movimiento = movimiento,
                                            cuenta = cuentas.find { it.id == movimiento.accountId }
                                        )
                                    }
                                    if (movimientosFiltrados.size > 5) {
                                        TextButton(
                                            onClick = { navController.navigate("lista") },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Ver todos los movimientos (${movimientos.size} total)")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    cuentaSeleccionada?.let { cuenta ->
        MovimientosPorCuentaDialog(
            cuenta = cuenta,
            movimientos = movimientos.filter { it.accountId == cuenta.id },
            onDismiss = { cuentaSeleccionada = null },
            onDeleteMovimiento = { movimiento ->
                viewModel.deleteMovement(
                    expense = movimiento,
                    onSuccess = {
                        Toast.makeText(context, "Movimiento eliminado", Toast.LENGTH_SHORT).show()
                    },
                    onError = { err ->
                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}

@Composable
fun EconoteDrawerContent(
    currentRoute: String,
    onItemSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "ECONOTE",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(12.dp))

        // --- Main Menu Items ---
        DrawerItem(
            icon = Icons.Filled.Home,
            label = "Inicio",
            isSelected = currentRoute == "main",
            onClick = { onItemSelected("main") }
        )
        DrawerItem(
            icon = Icons.Filled.List,
            label = "Movimientos",
            isSelected = currentRoute == "lista",
            onClick = { onItemSelected("lista") }
        )
        DrawerItem(
            icon = Icons.Filled.AccountBox,
            label = "Mis cuentas",
            isSelected = currentRoute == "cuentas",
            onClick = { onItemSelected("cuentas") }
        )
        DrawerItem(
            icon = Icons.Filled.Menu,
            label = "Categorías",
            isSelected = currentRoute == "categorias",
            onClick = { onItemSelected("categorias") }
        )
        DrawerItem(
            icon = Icons.Filled.Star,
            label = "Reportes y estadísticas",
            isSelected = currentRoute == "reportes",
            onClick = { onItemSelected("reportes") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "MI ECONOTE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        // --- Secondary Menu Items ---
        DrawerItem(
            icon = Icons.Filled.Person,
            label = "Mi perfil",
            isSelected = currentRoute == "perfil",
            onClick = { onItemSelected("perfil") }
        )
        DrawerItem(
            icon = Icons.Filled.Settings,
            label = "Configuración",
            isSelected = currentRoute == "configuracion",
            onClick = { onItemSelected("configuracion") }
        )
        DrawerItem(
            icon = Icons.Filled.Lock,
            label = "Privacidad y seguridad",
            isSelected = currentRoute == "privacidadSeguridad",
            onClick = { onItemSelected("privacidadSeguridad") }
        )
        DrawerItem(
            icon = Icons.Filled.Info,
            label = "Ayuda y acerca de",
            isSelected = currentRoute == "ayuda",
            onClick = { onItemSelected("ayuda") }
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(16.dp))

        // --- Logout ---
        DrawerItem(
            icon = Icons.Filled.ExitToApp,
            label = "Cerrar sesión",
            isSelected = false,
            isDestructive = true,
            onClick = onLogout
        )
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFEDE9FE) else Color.Transparent
    val contentColor = if (isDestructive) Color(0xFFEF4444) else if (isSelected) Color(0xFF7C3AED) else Color(0xFF475569)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

// 🆕 Card de Cuenta Compacta
@Composable
fun CuentaCardCompacta(
    cuenta: Account,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Email,
                contentDescription = null,
                tint = Color(0xFF667eea),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cuenta.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                if (cuenta.bank.isNotEmpty()) {
                    Text(
                        cuenta.bank,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(
                "S/. ${String.format("%.2f", cuenta.balance)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

// 🆕 Card de Movimiento Compacto
@Composable
fun MovimientoCardCompacto(
    movimiento: Expense,
    cuenta: Account?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (movimiento.type == "Gasto")
                Color(0xFFFFF8F0) else Color(0xFFF1F8F4)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (movimiento.type == "Gasto")
                    Icons.Filled.ArrowDropDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = null,
                tint = if (movimiento.type == "Gasto")
                    Color(0xFFFF5722) else Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    movimiento.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    "${cuenta?.name ?: "Cuenta"} • ${movimiento.date}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Text(
                "${if (movimiento.type == "Gasto") "-" else "+"}S/. ${movimiento.amount}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (movimiento.type == "Gasto")
                    Color(0xFFFF5722) else Color(0xFF4CAF50)
            )
        }
    }
}
// 🔹 Tarjeta de Cuenta Detallada
@Composable
fun CuentaCardDetallada(
    cuenta: Account,
    gastoTotal: Double,
    cantidadMovimientos: Int,
    onClick: () -> Unit
) {
    // Gradiente simple y universal
    val accountGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8360c3),  // Azul
            Color(0xFF2ebf91)   // Azul claro
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(accountGradient)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            cuenta.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (cuenta.bank.isNotEmpty()) {
                            Text(
                                cuenta.bank,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                    Icon(
                        Icons.Filled.Email,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Saldo disponible",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                Text(
                    "S/. ${String.format("%.2f", cuenta.balance)}",  // ✅ CAMBIO: Formatear el Double
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Gastado",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Text(
                            "S/. %.2f".format(gastoTotal),
                            color = Color(0xFFFFB74D),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Movimientos",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Text(
                            "$cantidadMovimientos",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
// 🔹 Tarjeta de Movimiento
@Composable
fun MovimientoCard(
    movimiento: Expense,
    cuenta: Account?,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (movimiento.type == "Gasto")
                Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (movimiento.type == "Gasto")
                            AppColors.ExpenseGradient else AppColors.IncomeGradient,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (movimiento.type == "Gasto")
                        Icons.Filled.ArrowDropDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    movimiento.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    cuenta?.name ?: "Cuenta desconocida",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    movimiento.date,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (movimiento.type == "Gasto") "-" else "+"}S/. ${movimiento.amount}",
                    color = if (movimiento.type == "Gasto")
                        Color(0xFFFF5722) else Color(0xFF4CAF50),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Movimiento") },
            text = { Text("¿Estás seguro de eliminar este movimiento?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// 🔹 Diálogo de Movimientos por Cuenta
@Composable
fun MovimientosPorCuentaDialog(
    cuenta: Account,
    movimientos: List<Expense>,
    onDismiss: () -> Unit,
    onDeleteMovimiento: (Expense) -> Unit
) {
    val gastoTotal = movimientos
        .filter { it.type == "Gasto" }
        .sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val ingresoTotal = movimientos
        .filter { it.type == "Ingreso" }
        .sumOf { it.amount.toDoubleOrNull() ?: 0.0 }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(cuenta.name, fontWeight = FontWeight.Bold)
                Text(
                    "Saldo: S/. ${String.format("%.2f", cuenta.balance)}",  // ✅ CAMBIO
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Resumen
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ingresos", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "S/. %.2f".format(ingresoTotal),
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Gastos", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "S/. %.2f".format(gastoTotal),
                                color = Color(0xFFFF5722),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "${movimientos.size}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (movimientos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay movimientos", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(movimientos.size) { index ->
                            val mov = movimientos[index]
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (mov.type == "Gasto")
                                        Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            mov.description,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            mov.date,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Text(
                                        "${if (mov.type == "Gasto") "-" else "+"}S/. ${mov.amount}",
                                        color = if (mov.type == "Gasto")
                                            Color(0xFFFF5722) else Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
