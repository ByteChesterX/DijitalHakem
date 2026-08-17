package com.example.aihakem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aihakem.repository.RefereeRepository
import com.example.aihakem.ui.RefereeScreen
import com.example.aihakem.ui.RefereeViewModel
import com.example.aihakem.ui.RefereeViewModelFactory

class MainActivity : ComponentActivity() {

    // API anahtarı BuildConfig üzerinden güvenli şekilde çekilir
    private val geminiApiKey = BuildConfig.GEMINI_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = RefereeRepository(geminiApiKey)
        val factory = RefereeViewModelFactory(repository)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: RefereeViewModel = viewModel(factory = factory)
                    RefereeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
