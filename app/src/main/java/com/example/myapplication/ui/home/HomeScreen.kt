package com.example.myapplication.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.data.LinkRepository
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(factory = ViewModelFactory(linkRepository = LinkRepository()))
) {
    val links by homeViewModel.links.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Links") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO: Navigate to Add Link*/ }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Link")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search your links...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") }
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = true, onClick = { /*TODO*/ }, label = { Text("All") })
                FilterChip(selected = false, onClick = { /*TODO*/ }, label = { Text("Favorites") })
                FilterChip(selected = false, onClick = { /*TODO*/ }, label = { Text("Unread") })
            }

            // Link List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(links) { link ->
                    LinkItem(link = link)
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeScreen()
    }
}
