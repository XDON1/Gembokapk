package com.example.gembok.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords ORDER BY createdAt DESC")
    fun getAllPasswords(): Flow<List<PasswordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordEntity)

    @Delete
    suspend fun deletePassword(password: PasswordEntity)

    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM passwords")
    suspend fun deleteAll()
}
