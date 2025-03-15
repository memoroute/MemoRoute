package com.example.memoroutev2.model

/**
 * 目的地数据模型
 */
data class Destination(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val imageResource: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val popularity: Float,
    var isFavorite: Boolean = false
) 