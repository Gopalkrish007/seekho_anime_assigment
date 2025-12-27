package com.example.seekhoanime.ui.detail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seekhoanime.core.AppResult
import com.example.seekhoanime.core.ErrorMapper
import com.example.seekhoanime.data.repository.AnimeRepository
import com.example.seekhoanime.domain.model.AnimeCharacter
import com.example.seekhoanime.domain.model.AnimeDetail
import com.example.seekhoanime.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class AnimeDetailUiState(
    val loading: Boolean = false,
    val detail: AnimeDetail? = null,
    val cast: List<AnimeCharacter> = emptyList(),
    val error: String? = null,
    val isOffline: Boolean = false
)

class AnimeDetailViewModel(
    private val repo: AnimeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnimeDetailUiState(loading = true))
    val state: StateFlow<AnimeDetailUiState> = _state

    fun bind(animeId: Int, isOnlineFlow: kotlinx.coroutines.flow.Flow<Boolean>) {
        // Observe Detail from DB
        viewModelScope.launch {
            repo.observeDetail(animeId).collect { d ->
                _state.value = _state.value.copy(loading = false, detail = d)
                if (d == null) refresh(animeId)
            }
        }

        // Observe Cast from DB
        viewModelScope.launch {
            repo.observeCast(animeId).collect { c ->
                _state.value = _state.value.copy(cast = c)
                if (c.isEmpty()) refreshCast(animeId)
            }
        }

        // Auto refresh when online returns
        viewModelScope.launch {
            isOnlineFlow.collect { online ->
                _state.value = _state.value.copy(isOffline = !online)
                if (online) {
                    refresh(animeId)
                    refreshCast(animeId)
                }
            }
        }
    }

    fun refresh(animeId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)

            when (val r = repo.refreshDetail(animeId)) {
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

    fun refreshCast(animeId: Int) {
        viewModelScope.launch {
            when (val r = repo.refreshCast(animeId)) {
                is AppResult.Success -> Unit
                is AppResult.Error -> {
                    // keep detail visible; show a mild error (optional)
                    _state.value = _state.value.copy(error = ErrorMapper.userMessage(r.error))
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

