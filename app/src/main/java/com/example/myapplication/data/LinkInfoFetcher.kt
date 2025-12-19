package com.example.myapplication.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class LinkInfoFetcher {
    suspend fun fetchLinkMetadata(url: String): LinkMetadata? {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(url).get()
                val title = document.select("meta[property=og:title]").attr("content")
                    .ifEmpty { document.title() }
                val description = document.select("meta[property=og:description]").attr("content")
                    .ifEmpty { document.select("meta[name=description]").attr("content") }
                val imageUrl = document.select("meta[property=og:image]").attr("content")
                    .ifEmpty { document.select("link[rel=icon]").attr("href") }

                LinkMetadata(
                    title = title.takeIf { it.isNotBlank() },
                    description = description.takeIf { it.isNotBlank() },
                    imageUrl = imageUrl.takeIf { it.isNotBlank() }
                )
            } catch (e: Exception) {
                Log.e("LinkInfoFetcher", "Error fetching link metadata for $url: ${e.message}")
                null
            }
        }
    }
}

data class LinkMetadata(
    val title: String?,
    val description: String?,
    val imageUrl: String?
)
