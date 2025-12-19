package com.example.myapplication.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import java.util.UUID

class LinkRepository {
    private val _links = MutableStateFlow<List<Link>>(emptyList())
    val links = _links.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(
        listOf(
            Category(UUID.randomUUID().toString(), "設計靈感", null, 0),
            Category(UUID.randomUUID().toString(), "工作", null, 0),
            Category(UUID.randomUUID().toString(), "待閱讀", null, 0),
            Category(UUID.randomUUID().toString(), "科技新聞", null, 0)
        )
    )
    val categories = _categories.asStateFlow()

    init {
        // Sample Data
        val catDesign = _categories.value.first { it.name == "設計靈感" }
        val catWork = _categories.value.first { it.name == "工作" }

        _links.value = listOf(
            Link(
                id = UUID.randomUUID().toString(),
                url = "https://www.medium.com/design-trends-2024",
                title = "10 Best UI Design Trends for 2024",
                description = "A comprehensive guide to the latest shifts in...",
                imageUrl = "https://i.ibb.co/1n4Y01R/screen.png", // Placeholder image
                categoryId = catDesign.id,
                isFavorite = false,
                isRead = false,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2) // 2 hours ago
            ),
            Link(
                id = UUID.randomUUID().toString(),
                url = "https://www.instagram.com/p/cat-video",
                title = "Funny Cat Video Compilation 2024 😼",
                description = "Shared by Alex",
                imageUrl = "https://i.ibb.co/L5w20R0/screen2.png", // Placeholder image
                categoryId = null,
                isFavorite = false,
                isRead = false,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24) // Yesterday
            ),
            Link(
                id = UUID.randomUUID().toString(),
                url = "https://reactnative.dev/docs",
                title = "React Native Documentation",
                description = "Core components and APIs...",
                imageUrl = "https://i.ibb.co/M9q0mP5/screen3.png", // Placeholder image
                categoryId = catWork.id,
                isFavorite = true,
                isRead = true,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 5) // 5 days ago
            )
        )
    }

    fun addLink(link: Link) {
        _links.value = _links.value + link
        updateCategoryLinkCount()
    }

    fun updateLink(updatedLink: Link) {
        _links.value = _links.value.map { if (it.id == updatedLink.id) updatedLink else it }
    }

    fun deleteLink(linkId: String) {
        _links.value = _links.value.filter { it.id != linkId }
        updateCategoryLinkCount()
    }

    fun addCategory(category: Category) {
        _categories.value = _categories.value + category
    }

    fun updateCategory(updatedCategory: Category) {
        _categories.value = _categories.value.map { if (it.id == updatedCategory.id) updatedCategory else it }
    }

    fun deleteCategory(categoryId: String) {
        _categories.value = _categories.value.filter { it.id != categoryId }
        // Also remove categoryId from links that belong to this category
        _links.value = _links.value.map {
            if (it.categoryId == categoryId) it.copy(categoryId = null) else it
        }
        updateCategoryLinkCount()
    }

    private fun updateCategoryLinkCount() {
        _categories.value = _categories.value.map { category ->
            val count = _links.value.count { it.categoryId == category.id }
            category.copy(linkCount = count)
        }
    }
}