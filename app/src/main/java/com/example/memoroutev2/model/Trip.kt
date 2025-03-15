package com.example.memoroutev2.model

import java.util.Date
import java.util.UUID

/**
 * 旅行数据模型
 */
data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val location: String,
    val description: String,
    val startDate: Date,
    val endDate: Date? = null,
    val imageUrl: String? = null,
    val imageResource: Int = 0,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isFavorite: Boolean = false,
    val createdAt: Date = Date(),
    val locationPoints: List<TripLocation> = emptyList(),
    val paths: List<TripPath> = emptyList(),
    val isPublic: Boolean = true,
    val showLocation: Boolean = true,
    val pointsCount: Int = 0,
    val pathsCount: Int = 0
) 