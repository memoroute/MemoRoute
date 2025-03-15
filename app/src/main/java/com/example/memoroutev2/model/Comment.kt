package com.example.memoroutev2.model

import java.util.Date
import java.util.UUID

/**
 * 评论数据模型
 */
data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String? = null,
    val content: String,
    val createdAt: Date = Date(),
    val likes: Int = 0,
    val isLiked: Boolean = false
) 