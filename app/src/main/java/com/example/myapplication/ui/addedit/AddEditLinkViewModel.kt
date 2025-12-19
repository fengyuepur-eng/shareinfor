package com.example.myapplication.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Category
import com.example.myapplication.data.Link
import com.example.myapplication.data.LinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

data class AddEditLinkUiState(
    val url: String = "",
    val title: String = "",
    val description: String = "",
    val selectedCategoryId: String? = null,
    val isFavorite: Boolean = false,
    val categories: List<Category> = emptyList()
)

class AddEditLinkViewModel(
    private val linkRepository: LinkRepository,
    private val sharedUrl: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditLinkUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (sharedUrl != null) {
            _uiState.update { it.copy(url = sharedUrl) }
            onUrlChange(sharedUrl)
        }

        viewModelScope.launch {
            linkRepository.categories.collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun onUrlChange(newUrl: String) {
        _uiState.update { it.copy(url = newUrl) }
        // TODO: Add logic to auto-fetch link metadata
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onFavoriteChanged(isFavorite: Boolean) {
        _uiState.update { it.copy(isFavorite = isFavorite) }
    }

    fun saveLink() {
        viewModelScope.launch {
            val state = _uiState.value
            val link = Link(
                id = UUID.randomUUID().toString(),
                url = state.url,
                title = state.title,
                description = state.description,
                imageUrl = null, // Will be fetched later
                categoryId = state.selectedCategoryId,
                isFavorite = state.isFavorite,
                isRead = false,
                timestamp = Date()
            )
            linkRepository.addLink(link)
        }
    }
}