package com.example.seekhoanime.ui.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seekhoanime.core.AppResult
import com.example.seekhoanime.core.ErrorMapper
import com.example.seekhoanime.data.repository.AnimeRepository
import com.example.seekhoanime.domain.model.AnimeSummary
import com.example.seekhoanime.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class AnimeListUiState(
    val loading: Boolean = false,
    val items: List<AnimeSummary> = emptyList(),
    val error: String? = null,
    val isOffline: Boolean = false
)

class AnimeListViewModel(
    private val repo: AnimeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnimeListUiState(loading = true))
    val state: StateFlow<AnimeListUiState> = _state

    fun bind(isOnlineFlow: kotlinx.coroutines.flow.Flow<Boolean>) {
        // Observe DB (always)
        viewModelScope.launch {
            repo.observeTopAnime().collect { list ->
                _state.value = _state.value.copy(
                    loading = false,
                    items = list
                )
                // first load: if DB empty, try refresh once
                if (list.isEmpty()) refresh()
            }
        }

        // Auto refresh when network comes back
        viewModelScope.launch {
            isOnlineFlow.collect { online ->
                _state.value = _state.value.copy(isOffline = !online)
                if (online) refresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)

            when (val r = repo.refreshTopAnime()) {
                is AppResult.Success -> {
                    _state.value = _state.value.copy(loading = false, error = null)
                }

                is AppResult.Error -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = ErrorMapper.userMessage(r.error)
                    )
                }
            }
        }
    }


    private fun friendlyError(e: Throwable): String {
        val msg = e.message ?: "Something went wrong"
        return if (msg.contains("429")) "Too many requests. Try again in a few seconds."
        else msg
    }
}

