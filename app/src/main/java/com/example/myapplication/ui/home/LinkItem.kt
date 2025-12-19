package com.example.myapplication.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.Link
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.MutedText
import java.util.Date

@Composable
fun LinkItem(link: Link) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    model = link.imageUrl,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                ),
                contentDescription = link.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(link.title ?: "No Title", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "A comprehensive guide to the latest shifts in...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(painterResource(id = android.R.drawable.ic_menu_more), contentDescription = "More")
                    }
                }
                 Spacer(modifier = Modifier.height(8.dp))
                 Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                 ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // In a real app, you would resolve the source from the URL
                        Icon(painterResource(id = android.R.drawable.ic_dialog_info), contentDescription = "Source", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Medium", style = MaterialTheme.typography.bodySmall, color = MutedText)
                        Text(" • 2h ago", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    }
                    IconButton(onClick = { /*TODO: Bookmark*/ }) {
                        Icon(painterResource(id = android.R.drawable.btn_star), contentDescription = "Bookmark")
                    }
                 }
            }
        }
    }
}

@Preview
@Composable
fun LinkItemPreview() {
    MyApplicationTheme {
        LinkItem(
            link = Link(
                id = "1",
                url = "https://www.medium.com/design-trends-2024",
                title = "10 Best UI Design Trends for 2024",
                description = "A comprehensive guide to the latest shifts in...",
                imageUrl = "https://i.ibb.co/1n4Y01R/screen.png",
                categoryId = "1",
                isFavorite = false,
                isRead = false,
                timestamp = Date()
            )
        )
    }
}
