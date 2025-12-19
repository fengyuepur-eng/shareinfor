package com.example.myapplication.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.AppDestinations
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.data.Link
import com.example.myapplication.data.LinkRepository
import com.example.myapplication.data.LinkInfoFetcher
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val links by homeViewModel.links.collectAsState()
    val categories by homeViewModel.categories.collectAsState(initial = emptyList())
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    val filteredLinks = (if (query.isBlank()) {
        links.filter { link ->
            selectedCategoryId == null || link.categoryId == selectedCategoryId
        }
    } else {
        links.filter { link ->
            val haystack = listOfNotNull(link.title, link.description, link.url)
                .joinToString(" ")
                .lowercase()
            val matchesQuery = haystack.contains(query.lowercase())
            val matchesCategory = selectedCategoryId == null || link.categoryId == selectedCategoryId
            matchesQuery && matchesCategory
        }
    }).sortedWith(
        compareByDescending<Link> { it.isFavorite }
            .thenByDescending { it.timestamp.time }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Links") },
                actions = {
                    IconButton(onClick = { navController.navigate(AppDestinations.CATEGORY_MANAGER_ROUTE) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Manage categories")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_edit_link") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Link")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Search Bar
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search your links...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") }
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null },
                        label = { Text("All") }
                    )
                }
                items(categories, key = { it.id }) { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.name) }
                    )
                }
            }

            // Link List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredLinks, key = { it.id }) { link ->
                    LinkItem(
                        link = link,
                        onClick = {
                            navController.navigate("${AppDestinations.LINK_DETAIL_ROUTE}/${link.id}")
                        },
                        onFavoriteToggle = {
                            homeViewModel.toggleFavorite(link.id)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeScreen(
            navController = rememberNavController(),
            homeViewModel = viewModel(factory = ViewModelFactory(linkRepository = LinkRepository(), linkInfoFetcher = com.example.myapplication.data.LinkInfoFetcher()))
        )
    }
}
