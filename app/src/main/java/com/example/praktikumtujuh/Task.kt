package com.example.praktikumtujuh

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "task_table")
data class Task(
// id sebagai Primary Key dan dibuat otomatis
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
// Judul tugas
    val title: String,
// Status penyelesaian tugas
    val isCompleted: Boolean = false
)