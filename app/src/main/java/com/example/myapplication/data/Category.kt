package com.example.myapplication.data

data class Category(
    val id: String,
    val name: String,
    val iconResId: Int?, // Resource ID for an icon, e.g., R.drawable.ic_work
    val linkCount: Int = 0 // Number of links in this category
)