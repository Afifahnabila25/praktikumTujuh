package com.example.praktikumtujuh

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // CREATE: Menyimpan data baru
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task)
// READ: Mengambil semua data dan mengembalikannya sebagai Flow (observasi real- time)
    @Query("SELECT * FROM task_table ORDER BY id DESC")
    fun getAllTasks(): Flow<List<Task>>
    // UPDATE: Memperbarui data yang ada
    @Update
    suspend fun updateTask(task: Task)
    // DELETE: Menghapus data
    @Delete
    suspend fun deleteTask(task: Task)
}