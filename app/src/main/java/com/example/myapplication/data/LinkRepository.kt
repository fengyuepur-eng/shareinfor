package com.example.myapplication.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Date
import java.util.UUID

/**
 * In-memory store backed by a local JSON file so data survives app restarts.
 * If no saved data exists, it seeds a small sample dataset.
 */
class LinkRepository(private val context: Context? = null) {
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dataFile: File? = context?.filesDir?.resolve("links_data.json")

    private val _links = MutableStateFlow<List<Link>>(emptyList())
    val links = _links.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    init {
        val loaded = loadFromDisk()
        if (!loaded) {
            seedSampleData()
            persistAsync()
        }
        updateCategoryLinkCount()
    }

    fun addLink(link: Link) {
        _links.value = _links.value + link
        updateCategoryLinkCount()
        persistAsync()
    }

    fun updateLink(updatedLink: Link) {
        _links.value = _links.value.map { if (it.id == updatedLink.id) updatedLink else it }
        updateCategoryLinkCount()
        persistAsync()
    }

    fun deleteLink(linkId: String) {
        _links.value = _links.value.filter { it.id != linkId }
        updateCategoryLinkCount()
        persistAsync()
    }

    fun addCategory(category: Category) {
        _categories.value = _categories.value + category
        persistAsync()
    }

    fun updateCategory(updatedCategory: Category) {
        _categories.value = _categories.value.map { if (it.id == updatedCategory.id) updatedCategory else it }
        persistAsync()
    }

    fun deleteCategory(categoryId: String) {
        _categories.value = _categories.value.filter { it.id != categoryId }
        // Also remove categoryId from links that belong to this category
        _links.value = _links.value.map {
            if (it.categoryId == categoryId) it.copy(categoryId = null) else it
        }
        updateCategoryLinkCount()
        persistAsync()
    }

    private fun updateCategoryLinkCount() {
        _categories.value = _categories.value.map { category ->
            val count = _links.value.count { it.categoryId == category.id }
            category.copy(linkCount = count)
        }
    }

    private fun seedSampleData() {
        val categories = listOf(
            Category(UUID.randomUUID().toString(), "設計靈感", null, 0),
            Category(UUID.randomUUID().toString(), "工作", null, 0),
            Category(UUID.randomUUID().toString(), "待閱讀", null, 0),
            Category(UUID.randomUUID().toString(), "科技趨勢", null, 0)
        )
        _categories.value = categories

        val catDesign = categories.find { it.name == "設計靈感" }
        val catWork = categories.find { it.name == "工作" }

        val sampleLinks = mutableListOf<Link>()

        catDesign?.let { category ->
            sampleLinks.add(
                Link(
                    id = UUID.randomUUID().toString(),
                    url = "https://www.medium.com/design-trends-2024",
                    title = "10 Best UI Design Trends for 2024",
                    description = "A comprehensive guide to the latest shifts in...",
                    imageUrl = "https://i.ibb.co/1n4Y01R/screen.png",
                    categoryId = category.id,
                    isFavorite = false,
                    isRead = false,
                    timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2)
                )
            )
        }

        sampleLinks.add(
            Link(
                id = UUID.randomUUID().toString(),
                url = "https://www.instagram.com/p/cat-video",
                title = "Funny Cat Video Compilation 2024 🐱",
                description = "Shared by Alex",
                imageUrl = "https://i.ibb.co/L5w20R0/screen2.png",
                categoryId = null,
                isFavorite = false,
                isRead = false,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
            )
        )

        catWork?.let { category ->
            sampleLinks.add(
                Link(
                    id = UUID.randomUUID().toString(),
                    url = "https://reactnative.dev/docs",
                    title = "React Native Documentation",
                    description = "Core components and APIs...",
                    imageUrl = "https://i.ibb.co/M9q0mP5/screen3.png",
                    categoryId = category.id,
                    isFavorite = true,
                    isRead = true,
                    timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 5)
                )
            )
        }

        _links.value = sampleLinks
    }

    private fun persistAsync() {
        val file = dataFile ?: return
        ioScope.launch {
            runCatching {
                val root = JSONObject()
                val linksArray = JSONArray()
                _links.value.forEach { link ->
                    linksArray.put(
                        JSONObject().apply {
                            put("id", link.id)
                            put("url", link.url)
                            put("title", link.title)
                            put("description", link.description)
                            put("imageUrl", link.imageUrl)
                            put("categoryId", link.categoryId)
                            put("isFavorite", link.isFavorite)
                            put("isRead", link.isRead)
                            put("timestamp", link.timestamp.time)
                        }
                    )
                }
                val categoriesArray = JSONArray()
                _categories.value.forEach { category ->
                    categoriesArray.put(
                        JSONObject().apply {
                            put("id", category.id)
                            put("name", category.name)
                            put("iconResId", category.iconResId)
                            put("linkCount", category.linkCount)
                        }
                    )
                }
                root.put("links", linksArray)
                root.put("categories", categoriesArray)
                file.writeText(root.toString())
            }
        }
    }

    private fun loadFromDisk(): Boolean {
        val file = dataFile ?: return false
        if (!file.exists()) return false
        return runCatching {
            val text = file.readText()
            val root = JSONObject(text)
            val linksArray = root.optJSONArray("links") ?: JSONArray()
            val categoriesArray = root.optJSONArray("categories") ?: JSONArray()

            val loadedCategories = mutableListOf<Category>()
            for (i in 0 until categoriesArray.length()) {
                val obj = categoriesArray.getJSONObject(i)
                loadedCategories.add(
                    Category(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        iconResId = obj.optInt("iconResId").takeIf { it != 0 },
                        linkCount = obj.optInt("linkCount", 0)
                    )
                )
            }

            val loadedLinks = mutableListOf<Link>()
            for (i in 0 until linksArray.length()) {
                val obj = linksArray.getJSONObject(i)
                loadedLinks.add(
                    Link(
                        id = obj.getString("id"),
                        url = obj.getString("url"),
                        title = obj.optString("title", null),
                        description = obj.optString("description", null),
                        imageUrl = obj.optString("imageUrl", null),
                        categoryId = obj.optString("categoryId", null),
                        isFavorite = obj.optBoolean("isFavorite", false),
                        isRead = obj.optBoolean("isRead", false),
                        timestamp = Date(obj.optLong("timestamp", System.currentTimeMillis()))
                    )
                )
            }

            _categories.value = loadedCategories
            _links.value = loadedLinks
            true
        }.getOrDefault(false)
    }
}
