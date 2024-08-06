package com.example.tutle

import androidx.room.*

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms")
    fun getAllAlarms(): List<AlarmData>

    @Insert
    fun insertAlarm(alarm: AlarmData)

    @Update
    fun updateAlarm(alarm: AlarmData)

    @Delete
    fun deleteAlarm(alarm: AlarmData)

    @Query("DELETE FROM alarms")
    fun deleteAllAlarms()
}
