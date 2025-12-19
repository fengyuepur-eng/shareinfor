package com.example.myapplication.ui.home

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Link
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.MutedText
import java.util.Date
import kotlin.math.abs

@Composable
fun LinkItem(
    link: Link,
    onClick: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(link.title ?: "No Title", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = link.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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
                    val source = getSourceFromUrl(link.url)
                    Icon(painterResource(id = getSourceIcon(source)), contentDescription = "Source", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(source, style = MaterialTheme.typography.bodySmall, color = MutedText)
                    Text(" · ${getTimeAgo(link.timestamp)}", style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
                IconButton(onClick = onFavoriteToggle) {
                    Icon(painterResource(id = if (link.isFavorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off), contentDescription = "Bookmark")
                }
            }
        }
    }
}

private fun getSourceFromUrl(url: String): String {
    return try {
        val host = java.net.URL(url).host
        host.removePrefix("www.")
    } catch (e: Exception) {
        "Unknown source"
    }
}

private fun getSourceIcon(source: String): Int {
    // In a real app, you would have a more sophisticated way of getting icons
    return when {
        source.contains("medium.com") -> android.R.drawable.ic_dialog_info
        source.contains("instagram.com") -> android.R.drawable.ic_media_play
        else -> android.R.drawable.ic_dialog_map
    }
}

private fun getTimeAgo(date: Date): String {
    val now = System.currentTimeMillis()
    val diff = now - date.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return when {
        days > 0 -> "$days days ago"
        hours > 0 -> "$hours hours ago"
        minutes > 0 -> "$minutes minutes ago"
        else -> "$seconds seconds ago"
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
