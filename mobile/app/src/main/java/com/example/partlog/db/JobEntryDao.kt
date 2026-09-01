package com.example.partlog.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JobEntry)

    @Update
    suspend fun update(entry: JobEntry)

    @Query("SELECT * FROM job_entries ORDER BY createdAt DESC")
    fun getAllEntriesFlow(): Flow<List<JobEntry>>

    @Query("SELECT * FROM job_entries WHERE syncStatus = :status")
    suspend fun getEntriesBySyncStatus(status: String): List<JobEntry>

    @Query("SELECT * FROM job_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: String): JobEntry?

    @Query("UPDATE job_entries SET syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateSyncStatus(id: String, syncStatus: String)
}
