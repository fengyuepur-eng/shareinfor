package com.example.myapplication.data

import java.util.Date

data class Link(
    val id: String,
    val url: String,
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val categoryId: String?,
    val isFavorite: Boolean,
    val isRead: Boolean,
    val timestamp: Date
)
