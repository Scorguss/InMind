package com.example.inmind

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Task(
    val name: String,
    val description: String,
    val date: String,
    val reminder: String,
    val isRecurrent: Boolean
)

@Composable
fun App() {
    MaterialTheme {
        var selectedTab by remember { mutableStateOf(0) }
        val tasks = remember { mutableStateListOf<Task>() }
        var showDialog by remember { mutableStateOf(false) }
        var selectedDate by remember { mutableStateOf("") }

        Scaffold(
            bottomBar = {
                BottomNavigation {
                    BottomNavigationItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tareas") },
                        label = { Text("Tareas") },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendario") },
                        label = { Text("Calendario") },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Menu, contentDescription = "Vacio") },
                        label = { Text("Vacio") },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (selectedTab) {
                    0 -> TasksScreen(tasks)
                    1 -> CalendarScreen { date ->
                        selectedDate = date
                        showDialog = true
                    }
                    2 -> EmptyScreen()
                }
            }
        }

        if (showDialog) {
            TaskDialog(
                date = selectedDate,
                onDismiss = { showDialog = false },
                onSave = { task ->
                    tasks.add(task)
                    showDialog = false
                    selectedTab = 0 // Ir a la pantalla de tareas al guardar
                }
            )
        }
    }
}

@Composable
fun TasksScreen(tasks: List<Task>) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tareas Pendientes", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        if (tasks.isEmpty()) {
            Text("No hay tareas programadas")
        } else {
            LazyColumn {
                items(tasks) { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(task.name, style = MaterialTheme.typography.subtitle1)
                            Text(task.description, style = MaterialTheme.typography.body2)
                            Text("Fecha: ${task.date} | Recordatorio: ${task.reminder}", style = MaterialTheme.typography.caption)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarScreen(onDateSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Calendario - Enero 2024", style = MaterialTheme.typography.h6)
        Text("Seleccione un día para programar una actividad")
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(31) { index ->
                val day = index + 1
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clickable { onDateSelected("$day/01/2024") }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(day.toString())
                }
            }
        }
    }
}

@Composable
fun TaskDialog(date: String, onDismiss: () -> Unit, onSave: (Task) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reminder by remember { mutableStateOf("Hora") }
    var isRecurrent by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Actividad - $date") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre Tarea") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Recordatorio:")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = reminder == "Hora", onClick = { reminder = "Hora" })
                    Text("Hora")
                    RadioButton(selected = reminder == "Diario", onClick = { reminder = "Diario" })
                    Text("Diario")
                    RadioButton(selected = reminder == "Semana", onClick = { reminder = "Semana" })
                    Text("Semana")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isRecurrent, onCheckedChange = { isRecurrent = it })
                    Text("Asignar a grupo recurrente")
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                if (name.isNotBlank()) {
                    onSave(Task(name, description, date, reminder, isRecurrent))
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EmptyScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Módulo Vacío")
    }
}
