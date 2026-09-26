package com.example.appmovimientos.ui.screens.categorias

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovimientos.data.model.Category
import com.example.appmovimientos.ui.components.CategoryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    navController: NavController,
    viewModel: CategoriasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf<Category?>(null) }
    var categoriaAEditar by remember { mutableStateOf<Category?>(null) }
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    val categorias = uiState.categorias

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Categorías",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6366F1)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("nuevaCategoria") },
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Nueva Categoría",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF0F4FF),
                            Color(0xFFE8EEFF)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Total de Categorías",
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${categorias.size}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = TextStyle(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF6366F1),
                                                Color(0xFF8B5CF6)
                                            )
                                        )
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF6366F1),
                                                Color(0xFF8B5CF6)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Menu,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFECFDF5)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "${categorias.count { it.type == "Ingreso" }}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                    Text(
                                        "Ingresos",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFEF2F2)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "${categorias.count { it.type == "Gasto" }}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444)
                                    )
                                    Text(
                                        "Gastos",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filtroSeleccionado == "Todas",
                        onClick = { filtroSeleccionado = "Todas" },
                        label = {
                            Text(
                                "Todas (${categorias.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6366F1),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF64748B),
                            iconColor = Color(0xFF64748B)
                        )
                    )

                    FilterChip(
                        selected = filtroSeleccionado == "Ingreso",
                        onClick = { filtroSeleccionado = "Ingreso" },
                        label = {
                            Text(
                                "Ingresos (${categorias.count { it.type == "Ingreso" }})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF10B981),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF64748B),
                            iconColor = Color(0xFF64748B)
                        )
                    )

                    FilterChip(
                        selected = filtroSeleccionado == "Gasto",
                        onClick = { filtroSeleccionado = "Gasto" },
                        label = {
                            Text(
                                "Gastos (${categorias.count { it.type == "Gasto" }})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEF4444),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF64748B),
                            iconColor = Color(0xFF64748B)
                        )
                    )
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF6366F1)
                        )
                    }
                } else if (categorias.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(
                                        color = Color(0xFFF0F4FF),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Menu,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color(0xFF6366F1)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                "No hay categorías aún",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Crea tu primera categoría para organizar",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "mejor tus gastos e ingresos",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { navController.navigate("nuevaCategoria") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6366F1)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(50.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Crear Primera Categoría",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                } else {
                    val categoriasFiltradas = when (filtroSeleccionado) {
                        "Ingreso" -> categorias.filter { it.type == "Ingreso" }
                        "Gasto" -> categorias.filter { it.type == "Gasto" }
                        else -> categorias
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    when (filtroSeleccionado) {
                                        "Ingreso" -> "Categorías de Ingreso (${categoriasFiltradas.size})"
                                        "Gasto" -> "Categorías de Gasto (${categoriasFiltradas.size})"
                                        else -> "Todas las categorías (${categoriasFiltradas.size})"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )

                                if (filtroSeleccionado != "Todas") {
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = when (filtroSeleccionado) {
                                                "Ingreso" -> Color(0xFFECFDF5)
                                                else -> Color(0xFFFEF2F2)
                                            }
                                        )
                                    ) {
                                        Text(
                                            filtroSeleccionado,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = when (filtroSeleccionado) {
                                                "Ingreso" -> Color(0xFF10B981)
                                                else -> Color(0xFFEF4444)
                                            },
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (categoriasFiltradas.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Filled.List,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = Color(0xFF94A3B8)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            "No hay categorías de $filtroSeleccionado",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF475569)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Crea una nueva categoría para este tipo",
                                            fontSize = 13.sp,
                                            color = Color(0xFF94A3B8),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            itemsIndexed(categoriasFiltradas) { _, category ->
                                CategoryCard(
                                    category = category,
                                    onEdit = { categoriaAEditar = category },
                                    onDelete = { showDeleteDialog = category }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { category ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            icon = {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Eliminar Categoría",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar la categoría '${category.name}'? Esta acción no se puede deshacer.",
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(
                            category = category,
                            onSuccess = {
                                Toast.makeText(context, "✓ Categoría eliminada", Toast.LENGTH_SHORT).show()
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = null },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    categoriaAEditar?.let { categoria ->
        EditarCategoriaDialog(
            categoria = categoria,
            onDismiss = { categoriaAEditar = null },
            onSave = { categoriaActualizada, nombre, tipo, icono ->
                viewModel.updateCategory(
                    category = categoriaActualizada,
                    nombre = nombre,
                    tipo = tipo,
                    icono = icono,
                    onSuccess = {
                        Toast.makeText(context, "✓ Categoría actualizada", Toast.LENGTH_SHORT).show()
                        categoriaAEditar = null
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
fun EditarCategoriaDialog(
    categoria: Category,
    onDismiss: () -> Unit,
    onSave: (Category, String, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(categoria.name) }
    var tipo by remember { mutableStateOf(categoria.type) }
    var iconoSeleccionado by remember { mutableStateOf(categoria.icon) }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedIcono by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val iconos = mapOf(
        "ShoppingCart" to Icons.Filled.ShoppingCart,
        "Work" to Icons.Filled.Build,
        "Home" to Icons.Filled.Home,
        "Food" to Icons.Filled.Place,
        "Transport" to Icons.Filled.Face,
        "Health" to Icons.Filled.Favorite,
        "Entertainment" to Icons.Filled.FavoriteBorder,
        "Education" to Icons.Filled.Info,
        "Salary" to Icons.Filled.Done,
        "Gift" to Icons.Filled.MailOutline
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Categoría", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Box {
                    OutlinedButton(
                        onClick = { expandedTipo = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            if (tipo == "Ingreso") Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(tipo)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Gasto") },
                            onClick = {
                                tipo = "Gasto"
                                expandedTipo = false
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.Red)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ingreso") },
                            onClick = {
                                tipo = "Ingreso"
                                expandedTipo = false
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null, tint = Color.Green)
                            }
                        )
                    }
                }

                Box {
                    OutlinedButton(
                        onClick = { expandedIcono = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            iconos[iconoSeleccionado] ?: Icons.Filled.Menu,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(iconoSeleccionado)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expandedIcono,
                        onDismissRequest = { expandedIcono = false }
                    ) {
                        iconos.forEach { (key, icon) ->
                            DropdownMenuItem(
                                text = { Text(key) },
                                onClick = {
                                    iconoSeleccionado = key
                                    expandedIcono = false
                                },
                                leadingIcon = { Icon(icon, contentDescription = null) }
                            )
                        }
                    }
                }

                errorMessage?.let {
                    Text(it, color = Color.Red, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        errorMessage = "El nombre es obligatorio"
                    } else {
                        onSave(categoria, nombre, tipo, iconoSeleccionado)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCategoriaScreen(
    navController: NavController,
    viewModel: CategoriasViewModel = viewModel()
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Gasto") }
    var iconoSeleccionado by remember { mutableStateOf("ShoppingCart") }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedIcono by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val iconos = mapOf(
        "ShoppingCart" to Icons.Filled.ShoppingCart,
        "Work" to Icons.Filled.Build,
        "Home" to Icons.Filled.Home,
        "Food" to Icons.Filled.Place,
        "Transport" to Icons.Filled.Face,
        "Health" to Icons.Filled.Favorite,
        "Entertainment" to Icons.Filled.FavoriteBorder,
        "Education" to Icons.Filled.Info,
        "Salary" to Icons.Filled.Done,
        "Gift" to Icons.Filled.MailOutline
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Categoría", fontWeight = FontWeight.Bold) },
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
                        "Crear Nueva Categoría",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre de la categoría") },
                        leadingIcon = { Icon(Icons.Filled.Menu, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box {
                        OutlinedButton(
                            onClick = { expandedTipo = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                if (tipo == "Ingreso") Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowUp,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tipo)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = expandedTipo,
                            onDismissRequest = { expandedTipo = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Gasto") },
                                onClick = { tipo = "Gasto"; expandedTipo = false },
                                leadingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Ingreso") },
                                onClick = { tipo = "Ingreso"; expandedTipo = false },
                                leadingIcon = { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box {
                        OutlinedButton(
                            onClick = { expandedIcono = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                iconos[iconoSeleccionado] ?: Icons.Filled.Menu,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Seleccionar icono")
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = expandedIcono,
                            onDismissRequest = { expandedIcono = false }
                        ) {
                            iconos.forEach { (key, icon) ->
                                DropdownMenuItem(
                                    text = { Text(key) },
                                    onClick = { iconoSeleccionado = key; expandedIcono = false },
                                    leadingIcon = { Icon(icon, contentDescription = null) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.createCategory(
                                nombre = nombre,
                                tipo = tipo,
                                icono = iconoSeleccionado,
                                onSuccess = {
                                    Toast.makeText(context, "Categoría creada exitosamente", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = { err ->
                                    errorMessage = err
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = nombre.isNotBlank()
                    ) {
                        Text("Crear Categoría", fontSize = 16.sp)
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
