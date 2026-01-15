package it.group9.JoubHunting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM MST_user WHERE user_ID = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM MST_user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)
}
