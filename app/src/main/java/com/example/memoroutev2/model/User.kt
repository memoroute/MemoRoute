package com.example.memoroutev2.model

import java.util.Date
import java.util.UUID

/**
 * 用户数据模型
 */
data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val nickname: String,
    val avatar: String? = null,
    val bio: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val registeredAt: Date = Date(),
    val tripCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0
) 