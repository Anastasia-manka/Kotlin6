package com.example.kt6_2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.domain.usecase.GetPrizesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NobelUiState {
    object Loading : NobelUiState()
    data class Success(val prizes: List<NobelPrize>) : NobelUiState()
    data class Error(val message: String) : NobelUiState()
}

class NobelViewModel(
    private val getPrizesUseCase: GetPrizesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NobelUiState>(NobelUiState.Loading)
    val uiState: StateFlow<NobelUiState> = _uiState.asStateFlow()

    private var allPrizes: List<NobelPrize> = emptyList()
    private var currentYear: String? = null
    private var currentCategory: String? = null
    private var isLoadingAll = false

    init {
        loadAllPrizes()
    }

    private fun loadAllPrizes() {
        if (isLoadingAll) return
        isLoadingAll = true

        viewModelScope.launch {
            try {
                _uiState.value = NobelUiState.Loading
                allPrizes = getPrizesUseCase(limit = 100, year = null, category = null)
                android.util.Log.d("NobelDebug", "=== ЗАГРУЗКА ЗАВЕРШЕНА ===")
                android.util.Log.d("NobelDebug", "Загружено лауреатов: ${allPrizes.size}")
                applyFilters()
            } catch (e: Exception) {
                android.util.Log.e("NobelDebug", "Ошибка загрузки: ${e.message}")
                _uiState.value = NobelUiState.Error("Ошибка загрузки: ${e.message}")
            } finally {
                isLoadingAll = false
            }
        }
    }

    private fun applyFilters() {
        android.util.Log.d("NobelDebug", "=== ПРИМЕНЯЕМ ФИЛЬТР ===")
        android.util.Log.d("NobelDebug", "currentYear = '$currentYear'")
        android.util.Log.d("NobelDebug", "currentCategory = '$currentCategory'")
        android.util.Log.d("NobelDebug", "allPrizes.size = ${allPrizes.size}")

        val filtered = allPrizes.filter { prize ->
            var matches = true
            if (!currentYear.isNullOrBlank()) {
                matches = matches && prize.awardYear == currentYear
            }
            if (!currentCategory.isNullOrBlank()) {
                matches = matches && prize.category.lowercase() == currentCategory!!.lowercase()
            }
            matches
        }

        android.util.Log.d("NobelDebug", "Найдено после фильтра: ${filtered.size}")

        if (filtered.isNotEmpty()) {
            _uiState.value = NobelUiState.Success(filtered)
        } else {
            _uiState.value = NobelUiState.Error("Нет данных по указанным критериям")
        }
    }

    fun filterByYear(year: String?) {
        android.util.Log.d("NobelDebug", "=== filterByYear вызван с year = '$year' ===")
        currentYear = year?.takeIf { it.isNotBlank() }
        applyFilters()
    }

    fun filterByCategory(category: String?) {
        android.util.Log.d("NobelDebug", "=== filterByCategory вызван с category = '$category' ===")
        currentCategory = category
        applyFilters()
    }

    fun clearFilters() {
        android.util.Log.d("NobelDebug", "=== clearFilters вызван ===")
        currentYear = null
        currentCategory = null
        applyFilters()
    }
}