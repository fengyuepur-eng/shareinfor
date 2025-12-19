package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedUrl = intent?.getStringExtra(Intent.EXTRA_TEXT)
        setContent {
            MyApplicationTheme {
                AppNavigation(sharedUrl = sharedUrl)
            }
        }
    }
}