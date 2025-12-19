package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.LinkRepository
import com.example.myapplication.ui.addedit.AddEditLinkViewModel
import com.example.myapplication.data.LinkInfoFetcher
import com.example.myapplication.ui.category.CategoryViewModel
import com.example.myapplication.ui.home.HomeViewModel

class ViewModelFactory(
    private val linkRepository: LinkRepository,
    private val linkInfoFetcher: LinkInfoFetcher,
    private val sharedUrl: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditLinkViewModel::class.java)) {
            return AddEditLinkViewModel(linkRepository, linkInfoFetcher, sharedUrl) as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(linkRepository) as T
        }
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(linkRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}