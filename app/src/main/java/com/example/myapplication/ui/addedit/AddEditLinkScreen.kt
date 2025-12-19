package com.example.myapplication.ui.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.data.LinkRepository
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.MutedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLinkScreen(
    navController: NavController,
    viewModel: AddEditLinkViewModel,
    url: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    if (url != null) {
        viewModel.onUrlChange(url)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新增連結") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // URL Field
            Text("連結網址", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.url,
                onValueChange = viewModel::onUrlChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
            Text("✓ 已自動擷取網站資訊", color = Color.Green, style = MaterialTheme.typography.labelSmall)

            Spacer(modifier = Modifier.height(24.dp))

            // Title Field
            Text("標題", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                 colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Description Field
            Text("描述", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Category Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("分類", style = MaterialTheme.typography.labelMedium)
                TextButton(onClick = { /* TODO: Navigate to Category Management */ }) {
                    Text("管理分類", color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.categories.forEach { category ->
                     FilterChip(
                         selected = uiState.selectedCategoryId == category.id,
                         onClick = { viewModel.onCategorySelected(category.id) },
                         label = { Text(category.name) }
                     )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Favorite Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("❤️ 加入最愛")
                    Text("顯示在最愛列表頂部", style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
                Switch(
                    checked = uiState.isFavorite,
                    onCheckedChange = viewModel::onFavoriteChanged
                )
            }

            Spacer(Modifier.weight(1f)) // Pushes content below to the bottom

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text("取消")
                }
                Button(
                    onClick = {
                        viewModel.saveLink()
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("儲存變更 ✓")
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A241F)
@Composable
fun AddEditLinkScreenPreview() {
    MyApplicationTheme {
        AddEditLinkScreen(navController = rememberNavController(), viewModel = viewModel(factory = ViewModelFactory(linkRepository = LinkRepository())))
    }
}
