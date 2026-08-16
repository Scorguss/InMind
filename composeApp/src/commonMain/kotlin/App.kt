package com.example.inmind

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Task(
    val name: String,
    val description: String,
    val date: String,
    val reminder: String,
    val isRecurrent: Boolean,
    val group: String = "General"
)

@Composable
fun App() {
    MaterialTheme {
        var selectedTab by remember { mutableStateOf(0) }
        val tasks = remember { mutableStateListOf<Task>() }
        var showDialog by remember { mutableStateOf(false) }
        var selectedDateForDialog by remember { mutableStateOf("") }
        var isDateEditable by remember { mutableStateOf(false) }

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
                    0 -> TasksScreen(
                        tasks = tasks,
                        onAddTask = {
                            selectedDateForDialog = "01/01/2024" 
                            isDateEditable = true
                            showDialog = true
                        }
                    )
                    1 -> CalendarScreen(
                        tasks = tasks,
                        onDateSelected = { date ->
                            selectedDateForDialog = date
                            isDateEditable = false
                            showDialog = true
                        }
                    )
                    2 -> EmptyScreen()
                }
            }
        }

        if (showDialog) {
            TaskDialog(
                initialDate = selectedDateForDialog,
                isDateEditable = isDateEditable,
                onDismiss = { showDialog = false },
                onSave = { task ->
                    tasks.add(task)
                    showDialog = false
                    selectedTab = 0
                }
            )
        }
    }
}

@Composable
fun TasksScreen(tasks: List<Task>, onAddTask: () -> Unit) {
    var isGroupedView by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tareas", style = MaterialTheme.typography.h6)
            Row {
                IconButton(onClick = { isGroupedView = !isGroupedView }) {
                    Icon(
                        if (isGroupedView) Icons.Default.Menu else Icons.AutoMirrored.Filled.List,
                        contentDescription = "Cambiar Vista"
                    )
                }
                IconButton(onClick = onAddTask) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Tarea")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (tasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay tareas programadas")
            }
        } else {
            if (isGroupedView) {
                val groupedTasks = tasks.groupBy { it.group }
                LazyColumn {
                    groupedTasks.forEach { (group, tasksInGroup) ->
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                color = Color.LightGray.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = group,
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        items(tasksInGroup) { task ->
                            TaskCard(task)
                        }
                    }
                }
            } else {
                LazyColumn {
                    items(tasks) { task ->
                        TaskCard(task)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(task.name, style = MaterialTheme.typography.subtitle1)
            Text(task.description, style = MaterialTheme.typography.body2)
            Text(
                "Fecha: ${task.date} | Recordatorio: ${task.reminder}",
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
fun CalendarScreen(tasks: List<Task>, onDateSelected: (String) -> Unit) {
    var currentMonth by remember { mutableStateOf(1) }
    var currentYear by remember { mutableStateOf(2024) }
    
    val months = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (currentMonth == 1) {
                    currentMonth = 12
                    currentYear--
                } else {
                    currentMonth--
                }
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior")
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${months[currentMonth - 1]}",
                    style = MaterialTheme.typography.h6
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Año: $currentYear", fontSize = 14.sp)
                    IconButton(onClick = { currentYear++ }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Aumentar año", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { currentYear-- }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Disminuir año", modifier = Modifier.size(16.dp))
                    }
                }
            }

            IconButton(onClick = {
                if (currentMonth == 12) {
                    currentMonth = 1
                    currentYear++
                } else {
                    currentMonth++
                }
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Mes siguiente")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("D", "L", "M", "M", "J", "V", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(31) { index ->
                val day = index + 1
                val dateString = "${day.toString().padStart(2, '0')}/${currentMonth.toString().padStart(2, '0')}/$currentYear"
                val hasTask = tasks.any { it.date == dateString }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clickable { onDateSelected(dateString) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(day.toString())
                        if (hasTask) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDialog(
    initialDate: String,
    isDateEditable: Boolean,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(initialDate) }
    var reminder by remember { mutableStateOf("Hora") }
    var isRecurrent by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Actividad") },
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
                OutlinedTextField(
                    value = date,
                    onValueChange = { if (isDateEditable) date = it },
                    label = { Text("Fecha (DD/MM/AAAA)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isDateEditable
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Recordatorio:")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = reminder == "Hora", onClick = { reminder = "Hora" })
                    Text("H")
                    RadioButton(selected = reminder == "Diario", onClick = { reminder = "D" })
                    Text("D")
                    RadioButton(selected = reminder == "Semana", onClick = { reminder = "S" })
                    Text("S")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isRecurrent, onCheckedChange = { isRecurrent = it })
                    Text("Recurrente")
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                if (name.isNotBlank() && date.isNotBlank()) {
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
