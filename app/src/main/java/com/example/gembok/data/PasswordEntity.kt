package com.example.gembok.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val username: String,
    val password: String,
    val siteOrApp: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
