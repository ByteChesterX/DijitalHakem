package com.example.aihakem.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aihakem.data.Statement
import com.example.aihakem.repository.RefereeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    object Idle : UiState
    object Listening : UiState
    object Loading : UiState
    data class Success(val resultText: String) : UiState
    data class Error(val message: String) : UiState
}

class RefereeViewModel(
    private val repository: RefereeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var statementAText: String = ""
    private var statementBText: String = ""

    // Ses kaydı tamamlandığında gelen metinleri biriktirir
    fun appendTranscript(speaker: Int, text: String) {
        if (text.isBlank()) return
        
        if (speaker == 1) {
            statementAText = if (statementAText.isEmpty()) text else "$statementAText $text"
        } else {
            statementBText = if (statementBText.isEmpty()) text else "$statementBText $text"
        }
    }

    // İki tarafın konuşması tamamlandığında analizi başlatır
    fun analyzeDispute(topic: String = "", personA: String = "1. Kişi", personB: String = "2. Kişi") {
        if (statementAText.isBlank() || statementBText.isBlank()) {
            _uiState.value = UiState.Error("Lütfen her iki tarafın da ifadesini kaydedin.")
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val statementA = Statement(personA, statementAText)
                val statementB = Statement(personB, statementBText)

                val result = repository.evaluateDispute(topic, statementA, statementB)
                _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Analiz yapılırken bir hata oluştu: ${e.localizedMessage}")
            }
        }
    }

    fun setListeningState(isListening: Boolean) {
        if (isListening) {
            _uiState.value = UiState.Listening
        } else if (_uiState.value is UiState.Listening) {
            _uiState.value = UiState.Idle
        }
    }

    fun reset() {
        statementAText = ""
        statementBText = ""
        _uiState.value = UiState.Idle
    }
}

// ViewModel Factory
class RefereeViewModelFactory(private val repository: RefereeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RefereeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RefereeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
