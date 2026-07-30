package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.FeedbackItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM feedback_items ORDER BY id DESC")
    fun getAllFeedbacks(): Flow<List<FeedbackItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackItem)

    @Query("DELETE FROM feedback_items WHERE id = :id")
    suspend fun deleteFeedback(id: Long)

    @Query("SELECT * FROM feedback_items ORDER BY id DESC")
    suspend fun getAllFeedbacksSync(): List<FeedbackItem>
}
