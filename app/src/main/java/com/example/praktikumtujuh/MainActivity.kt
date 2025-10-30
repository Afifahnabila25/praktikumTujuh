package com.example.praktikumtujuh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.praktikumtujuh.ui.theme.PraktikumTujuhTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Edit
class MainActivity : ComponentActivity() {

    // Inisialisasi Database dan ViewModel
    private val database by lazy { TaskDatabase.getDatabase(application) }
    private val repository by lazy { TaskRepository(database.taskDao()) }
    private val viewModelFactory by lazy { TaskViewModelFactory(repository) }
    private val viewModel: TaskViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Di dalam MainActivity.kt
        setContent {
            PraktikumTujuhTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val tasks = viewModel.allTasks.collectAsState(initial = emptyList()).value
                    TaskScreen(
                        tasks = tasks,
                        onAddTask = { title -> viewModel.addNewTask(title) },
                        onUpdateTask = { task, completed -> viewModel.updateTaskStatus(task, completed) },
                        onDeleteTask = { task -> viewModel.deleteTask(task) },
                        onUpdateTaskTitle = { task, title -> viewModel.updateTaskTitle(task, title) }
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    tasks: List<Task>,
    onAddTask: (String) -> Unit,
    onUpdateTask: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onUpdateTaskTitle: (Task, String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Daftar Tugas (Room Compose)") }) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            TaskInput(onAddTask)
            Spacer(modifier = Modifier.height(8.dp))

            TaskList(
                tasks = tasks,
                onUpdateTask = onUpdateTask,
                onDeleteTask = onDeleteTask,
                onUpdateTaskTitle = onUpdateTaskTitle
            )
        }
    }
}

@Composable
fun TaskInput(onAddTask: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tugas Baru") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onAddTask(text)
                    text = ""
                }
            },
            enabled = text.isNotBlank()
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Tambah Tugas")
        }
    }
}
@Composable
fun TaskList(
    tasks: List<Task>,
onUpdateTask: (Task, Boolean) -> Unit,
    onUpdateTaskTitle: (Task, String) -> Unit,
onDeleteTask: (Task) -> Unit
) {
// LazyColumn menggantikan RecyclerView
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                task = task,
                onCheckedChange = { isChecked -> onUpdateTask(task, isChecked) },
                onUpdateTitle = { newTitle -> onUpdateTaskTitle(task, newTitle)},
                onDelete = { onDeleteTask(task) }
            )
            Divider()
        }
    }
}
@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onUpdateTitle: (String) -> Unit, // <-- Parameter baru
    onDelete: () -> Unit
) {
    // State untuk mengontrol visibilitas dialog
    var showEditDialog by remember { mutableStateOf(false) }
    // State untuk menyimpan teks di dalam text-field dialog
    var newTitle by remember { mutableStateOf(task.title) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!task.isCompleted) }
            // Sesuaikan padding agar tombol ikon tidak terlalu sempit
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckedChange,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(8.dp))

        // --- TOMBOL EDIT BARU ---
        IconButton(onClick = {
            // Set judul saat ini ke text-field dan tampilkan dialog
            newTitle = task.title
            showEditDialog = true
        }) {
            Icon(Icons.Filled.Edit, contentDescription = "Ubah Tugas")
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Hapus Tugas")
        }
    }

    // --- DIALOG UNTUK EDIT TUGAS ---
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Ubah Judul Tugas") },
            text = {
                // Text field untuk memasukkan judul baru
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Judul Baru") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Hanya update jika judul tidak kosong
                        if (newTitle.isNotBlank()) {
                            onUpdateTitle(newTitle)
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}