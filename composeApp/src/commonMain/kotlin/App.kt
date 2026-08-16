package com.example.inmind

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
    val id: Long = kotlin.random.Random.nextLong(),
    val name: String,
    val description: String,
    val date: String,
    val reminder: String,
    val recurrenceInterval: String = "Ninguno",
    val isRecurrent: Boolean,
    val group: String = "General",
    var isCompleted: Boolean = false
)

@Composable
fun App() {
    MaterialTheme {
        var selectedTab by remember { mutableStateOf(0) }
        val tasks = remember { mutableStateListOf<Task>() }
        val groups = remember { mutableStateListOf("General", "Trabajo", "Personal") }
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
                        },
                        onFinishTask = { task ->
                            if (task.isRecurrent) {
                                // En un entorno real, esto se resetearía con un Timer/Worker
                                // Por ahora lo marcamos como completado para que desaparezca de la vista actual
                                val index = tasks.indexOf(task)
                                if (index != -1) {
                                    tasks[index] = tasks[index].copy(isCompleted = true)
                                }
                            } else {
                                tasks.remove(task)
                            }
                        },
                        onDeleteTask = { task ->
                            tasks.remove(task)
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
                    2 -> EmptyScreen(tasks)
                }
            }
        }

        if (showDialog) {
            TaskDialog(
                initialDate = selectedDateForDialog,
                isDateEditable = isDateEditable,
                existingGroups = groups,
                onDismiss = { showDialog = false },
                onSave = { task, newGroup ->
                    if (newGroup != null && !groups.contains(newGroup)) {
                        groups.add(newGroup)
                    }
                    tasks.add(task)
                    showDialog = false
                    selectedTab = 0
                }
            )
        }
    }
}

@Composable
fun TasksScreen(
    tasks: List<Task>, 
    onAddTask: () -> Unit,
    onFinishTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    var isGroupedView by remember { mutableStateOf(false) }
    // Solo mostramos tareas que no han sido completadas
    val visibleTasks = tasks.filter { !it.isCompleted }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tareas Pendientes", style = MaterialTheme.typography.h6)
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

        if (visibleTasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay tareas pendientes", color = Color.Gray)
            }
        } else {
            if (isGroupedView) {
                val groupedTasks = visibleTasks.groupBy { it.group }
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
                        items(tasksInGroup, key = { it.id }) { task ->
                            TaskCard(task, onFinishTask, onDeleteTask)
                        }
                    }
                }
            } else {
                LazyColumn {
                    items(visibleTasks, key = { it.id }) { task ->
                        TaskCard(task, onFinishTask, onDeleteTask)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onFinish: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.name, style = MaterialTheme.typography.subtitle1)
                    Text(task.description, style = MaterialTheme.typography.body2)
                }
                Row {
                    IconButton(onClick = { onFinish(task) }) {
                        Icon(Icons.Default.Check, contentDescription = "Finalizar", tint = Color.Green)
                    }
                    IconButton(onClick = { onDelete(task) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        "Fecha: ${task.date} | Recordatorio: ${task.reminder}",
                        style = MaterialTheme.typography.caption
                    )
                    if (task.isRecurrent) {
                        Text(
                            "Recurrencia: ${task.recurrenceInterval}",
                            style = MaterialTheme.typography.caption,
                            color = Color.Blue
                        )
                    }
                }
                Text(
                    text = if (task.isRecurrent) "RECURRENTE (${task.group})" else task.group,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isRecurrent) Color.Blue else Color.DarkGray
                )
            }
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
                // En el calendario mostramos si hay tareas registradas (aunque estén ocultas por completadas)
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
                                    .background(if (tasks.any { it.date == dateString && !it.isCompleted }) Color.Gray else Color.LightGray)
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
    existingGroups: List<String>,
    onDismiss: () -> Unit,
    onSave: (Task, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(initialDate) }
    var reminder by remember { mutableStateOf("Hora") }
    var isRecurrent by remember { mutableStateOf(false) }
    var recurrenceInterval by remember { mutableStateOf("Día") }
    
    var selectedGroup by remember { mutableStateOf(existingGroups.firstOrNull() ?: "General") }
    var isCreatingNewGroup by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }
    var groupError by remember { mutableStateOf<String?>(null) }
    
    var expandedGroup by remember { mutableStateOf(false) }
    var expandedRecurrence by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Actividad") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Grupo:", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { 
                        isCreatingNewGroup = !isCreatingNewGroup
                        groupError = null
                    }) {
                        Icon(
                            if (isCreatingNewGroup) Icons.Default.Close else Icons.Default.AddCircle,
                            contentDescription = "Nuevo Grupo",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }

                if (isCreatingNewGroup) {
                    OutlinedTextField(
                        value = newGroupName,
                        onValueChange = { 
                            newGroupName = it
                            groupError = if (existingGroups.contains(it.trim())) "El grupo ya existe" else null
                        },
                        label = { Text("Nombre del Nuevo Grupo") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = groupError != null
                    )
                    if (groupError != null) {
                        Text(groupError!!, color = MaterialTheme.colors.error, fontSize = 12.sp)
                    }
                } else {
                    Box {
                        OutlinedButton(
                            onClick = { expandedGroup = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedGroup)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(expanded = expandedGroup, onDismissRequest = { expandedGroup = false }) {
                            existingGroups.forEach { group ->
                                DropdownMenuItem(onClick = {
                                    selectedGroup = group
                                    expandedGroup = false
                                }) {
                                    Text(group)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isRecurrent, onCheckedChange = { isRecurrent = it })
                    Text("Actividad Recurrente")
                }

                if (isRecurrent) {
                    Text("Repetir cada:", style = MaterialTheme.typography.caption)
                    Box {
                        OutlinedButton(
                            onClick = { expandedRecurrence = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(recurrenceInterval)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(expanded = expandedRecurrence, onDismissRequest = { expandedRecurrence = false }) {
                            listOf("Hora", "Día", "Semana", "Mes", "Año").forEach { interval ->
                                DropdownMenuItem(onClick = {
                                    recurrenceInterval = interval
                                    expandedRecurrence = false
                                }) {
                                    Text(interval)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Estilo Recordatorio:")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = reminder == "Hora", onClick = { reminder = "Hora" })
                    Text("H")
                    RadioButton(selected = reminder == "Diario", onClick = { reminder = "Diario" })
                    Text("D")
                    RadioButton(selected = reminder == "Semana", onClick = { reminder = "Semana" })
                    Text("S")
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                val finalGroupName = if (isCreatingNewGroup) newGroupName.trim() else selectedGroup
                val isGroupValid = if (isCreatingNewGroup) {
                    finalGroupName.isNotEmpty() && !existingGroups.contains(finalGroupName)
                } else true

                if (name.isNotBlank() && date.isNotBlank() && isGroupValid) {
                    onSave(
                        Task(
                            name = name, 
                            description = description, 
                            date = date, 
                            reminder = reminder, 
                            recurrenceInterval = if (isRecurrent) recurrenceInterval else "Ninguno",
                            isRecurrent = isRecurrent, 
                            group = finalGroupName
                        ),
                        if (isCreatingNewGroup) finalGroupName else null
                    )
                } else if (isCreatingNewGroup && finalGroupName.isEmpty()) {
                    groupError = "Nombre obligatorio"
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
fun EmptyScreen(tasks: List<Task>) {
    val recurrentTasks = tasks.filter { it.isRecurrent }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Registro de Actividades Recurrentes", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))
        if (recurrentTasks.isEmpty()) {
            Text("No hay actividades recurrentes configuradas")
        } else {
            LazyColumn {
                items(recurrentTasks) { task ->
                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp), elevation = 4.dp) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(task.name, fontWeight = FontWeight.Bold)
                            Text("Frecuencia: ${task.recurrenceInterval}", color = Color.Blue)
                            Text("Estado: ${if (task.isCompleted) "Esperando siguiente ciclo" else "Pendiente"}")
                            if (task.isCompleted) {
                                Button(onClick = { /* Simulación de reinicio */ }) {
                                    Text("Simular Siguiente Ciclo")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
