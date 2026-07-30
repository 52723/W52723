package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback_items")
data class FeedbackItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val feedbackType: String = "功能建议", // 功能建议, 体感体验, 菜谱灵感, 界面美化, 错误反馈
    val ratingStars: Int = 5,
    val content: String,
    val contactInfo: String = "", // 选填联系方式
    val dateString: String,
    val isProcessed: Boolean = false
)
