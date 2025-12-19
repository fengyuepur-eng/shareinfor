package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            val sharedUrl = intent?.getStringExtra(Intent.EXTRA_TEXT)
            Log.d("MainActivity", "Shared URL: $sharedUrl")
            
            setContent {
                MyApplicationTheme {
                    AppNavigation(sharedUrl = sharedUrl)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
        }
    }
}