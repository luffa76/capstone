package com.example.tutle

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GraphDataDao {
    @Query("SELECT * FROM GraphData WHERE user_id = :userId")
    fun getGraphDataForUser(userId: String): List<GraphData>

    @Insert
    fun insertGraphData(graphData: GraphData)

    @Query("DELETE FROM graphdata WHERE user_id = :userId AND x_value = :x AND y_value = :y")
    fun deleteGraphData(userId: String, x: Float, y: Float)
}
