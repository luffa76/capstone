package com.example.tutle

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NicknameDao {
    @Query("SELECT * FROM Nickname LIMIT 1")
    fun getNickname(): Nickname?

    @Insert
    fun insertNickname(nickname: Nickname)

    @Query("DELETE FROM Nickname")
    fun deleteAllNicknames()
}