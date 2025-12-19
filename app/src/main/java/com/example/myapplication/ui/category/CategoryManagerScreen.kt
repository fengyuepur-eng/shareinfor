package com.example.myapplication.ui.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.data.Category
import com.example.myapplication.data.LinkRepository
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.MutedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel
) {
    val categories by categoryViewModel.categories.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("分類管理") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "新增分類")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("現有分類 (${categories.size})", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(categories) { category ->
                    CategoryItem(
                        category = category,
                        onEditClick = { /* TODO: Implement Edit */ },
                        onDeleteClick = { categoryViewModel.deleteCategory(category.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("新增分類") },
            text = {
                TextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("分類名稱") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    categoryViewModel.addCategory(newCategoryName)
                    newCategoryName = ""
                    showDialog = false
                }) {
                    Text("新增")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun CategoryItem(category: Category, onEditClick: (Category) -> Unit, onDeleteClick: (Category) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // TODO: Replace with actual category icon
                Icon(Icons.Filled.Add, contentDescription = "Category Icon", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(category.name, style = MaterialTheme.typography.titleMedium)
                    Text("${category.linkCount} 個連結", style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
            }

            Row {
                IconButton(onClick = { onEditClick(category) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDeleteClick(category) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF1A241F)
@Composable
fun CategoryManagerScreenPreview() {
    MyApplicationTheme {
        CategoryManagerScreen(rememberNavController(), categoryViewModel = viewModel(factory = ViewModelFactory(linkRepository = LinkRepository(), linkInfoFetcher = com.example.myapplication.data.LinkInfoFetcher())))
    }
}
