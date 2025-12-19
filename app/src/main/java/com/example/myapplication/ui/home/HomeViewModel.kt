package com.example.myapplication.ui.home

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Link
import com.example.myapplication.data.LinkRepository
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(private val linkRepository: LinkRepository) : ViewModel() {
    val links: StateFlow<List<Link>> = linkRepository.links

    fun toggleFavorite(linkId: String) {
        val current = linkRepository.links.value.firstOrNull { it.id == linkId } ?: return
        linkRepository.updateLink(current.copy(isFavorite = !current.isFavorite))
    }
}
