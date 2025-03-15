package com.example.memoroutev2.model

import java.io.Serializable
import java.util.UUID

/**
 * 旅行位置点数据模型
 */
data class TripLocation(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val type: LocationType = LocationType.POINT,
    val order: Int = 0
) : Serializable {
    enum class LocationType {
        POINT,  // 单个位置点
        PATH_POINT  // 路径中的点
    }
}

/**
 * 旅行路径数据模型
 */
data class TripPath(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val points: List<TripLocation> = emptyList(),
    val color: Int = 0xFF3F51B5.toInt(),  // 默认蓝色
    val width: Float = 3f,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable 