package ru.fefu.activitiesfefu.data

import androidx.room.*

@Dao
interface ActivityUserDao {
    @Insert
    suspend fun insert(user: ActivityUserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): ActivityUserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<ActivityUserEntity>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): ActivityUserEntity?
    @Update
    suspend fun update(user: ActivityUserEntity)

    // Методы для проверки уникальности логина и никнейма
    @Query("SELECT COUNT(*) FROM users WHERE email = :email AND id != :excludeUserId")
    suspend fun isEmailExists(email: String, excludeUserId: Int): Int
    
    @Query("SELECT COUNT(*) FROM users WHERE name = :nickname AND id != :excludeUserId")
    suspend fun isNicknameExists(nickname: String, excludeUserId: Int): Int
    
    // Методы для проверки при регистрации (без исключения)
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun isEmailExistsForRegistration(email: String): Int
    
    @Query("SELECT COUNT(*) FROM users WHERE name = :nickname")
    suspend fun isNicknameExistsForRegistration(nickname: String): Int
}