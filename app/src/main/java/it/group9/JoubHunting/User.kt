package it.group9.JoubHunting

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "MST_user")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_ID") val userId: Long = 0, // 自動連番

    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "sukil") val skill: String?, // "sukil" -> skill に補正、NULL可
    @ColumnInfo(name = "inte_sei") val intention: Int?, // "inte_sei" -> intention (志向性)
    @ColumnInfo(name = "expe") val experience: String? // "expe" -> experience (経験)
)
