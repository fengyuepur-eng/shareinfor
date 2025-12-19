package com.example.myapplication.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Category
import com.example.myapplication.data.Link
import com.example.myapplication.data.LinkInfoFetcher
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
    val categories: List<Category> = emptyList(),
    val debugLog: String = "",
    val isEdit: Boolean = false
)

class AddEditLinkViewModel(
    private val linkRepository: LinkRepository,
    private val linkInfoFetcher: LinkInfoFetcher,
    private val sharedUrl: String?,
    private val editingLinkId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditLinkUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Initialize categories
        viewModelScope.launch {
            linkRepository.categories.collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }

        // If editing, preload existing link data
        editingLinkId?.let { id ->
            linkRepository.links.value.firstOrNull { it.id == id }?.let { link ->
                _uiState.update {
                    it.copy(
                        url = link.url,
                        title = link.title.orEmpty(),
                        description = link.description.orEmpty(),
                        selectedCategoryId = link.categoryId,
                        isFavorite = link.isFavorite,
                        isEdit = true
                    )
                }
            }
        } ?: run {
            // Handle shared URL and fetch metadata for new link
            sharedUrl?.let { url ->
                if (url.isNotBlank()) {
                    _uiState.update { it.copy(url = url) }

                    // Auto-fetch metadata for shared URL
                    viewModelScope.launch {
                        try {
                            val metadata = linkInfoFetcher.fetchLinkMetadata(url)
                            _uiState.update {
                                it.copy(
                                    title = metadata?.title ?: "",
                                    description = metadata?.description ?: ""
                                )
                            }
                        } catch (e: Exception) {
                            _uiState.update {
                                it.copy(
                                    debugLog = "Error fetching metadata: ${e.message}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onUrlChange(newUrl: String) {
        _uiState.update { it.copy(url = newUrl) }
        
        // Auto-fetch link metadata when URL is provided
        if (newUrl.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val metadata = linkInfoFetcher.fetchLinkMetadata(newUrl)
                    if (metadata != null) {
                        _uiState.update {
                            it.copy(
                                title = metadata.title ?: it.title,
                                description = metadata.description ?: it.description
                            )
                        }
                    }
                } catch (e: Exception) {
                    // If fetching fails, at least we have the URL
                    _uiState.update {
                        it.copy(
                            debugLog = "Error fetching metadata: ${e.message}"
                        )
                    }
                }
            }
        }
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
            val existing = editingLinkId?.let { id ->
                linkRepository.links.value.firstOrNull { it.id == id }
            }

            if (existing != null) {
                val updated = existing.copy(
                    url = state.url,
                    title = state.title,
                    description = state.description,
                    categoryId = state.selectedCategoryId,
                    isFavorite = state.isFavorite
                )
                linkRepository.updateLink(updated)
            } else {
                val newLink = Link(
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
                linkRepository.addLink(newLink)
            }
        }
    }
}
