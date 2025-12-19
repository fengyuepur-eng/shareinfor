package com.example.myapplication.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Category
import com.example.myapplication.data.LinkRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class CategoryViewModel(private val linkRepository: LinkRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> = linkRepository.categories
        .map { categories -> categories.sortedBy { it.name } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCategory(name: String) {
        if (name.isNotBlank()) {
            val newCategory = Category(
                id = UUID.randomUUID().toString(),
                name = name,
                iconResId = null // TODO: Allow user to select icon
            )
            linkRepository.addCategory(newCategory)
        }
    }

    fun updateCategory(category: Category) {
        linkRepository.updateCategory(category)
    }

    fun deleteCategory(categoryId: String) {
        linkRepository.deleteCategory(categoryId)
    }
}
