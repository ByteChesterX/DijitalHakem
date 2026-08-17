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
    object Loading : UiState
    data class Success(val resultText: String) : UiState
    data class Error(val message: String) : UiState
}

class RefereeViewModel(
    private val repository: RefereeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun analyzeDispute(topic: String, personA: String, textA: String, personB: String, textB: String) {
        if (textA.isBlank() || textB.isBlank()) {
            _uiState.value = UiState.Error("Lütfen her iki arkadaşın da ifadelerini doldurun.")
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val statementA = Statement(personA.ifBlank { "1. Arkadaş" }, textA)
                val statementB = Statement(personB.ifBlank { "2. Arkadaş" }, textB)

                val result = repository.evaluateDispute(topic, statementA, statementB)
                _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Analiz yapılırken bir hata oluştu: ${e.localizedMessage}")
            }
        }
    }

    fun reset() {
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
