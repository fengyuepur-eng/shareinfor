package com.example.myapplication.ui.home

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LinkRepository
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(private val linkRepository: LinkRepository) : ViewModel() {
    val links = linkRepository.links.asStateFlow()
}
