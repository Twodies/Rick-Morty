package com.twodies.rickmorty.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.usecase.GetCharacterDetailUseCase
import com.twodies.rickmorty.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterDetailState())
    val state: StateFlow<CharacterDetailState> = _state.asStateFlow()

    init {
        savedStateHandle.get<Int>("characterId")?.let { characterId ->
            loadCharacterDetail(characterId)
        }
    }

    fun onEvent(event: CharacterDetailEvent) {
        when (event) {
            is CharacterDetailEvent.Refresh -> {
                _state.value.character?.let { character ->
                    loadCharacterDetail(character.id)
                }
            }
        }
    }

    private fun loadCharacterDetail(characterId: Int) {
        viewModelScope.launch {
            getCharacterDetailUseCase(characterId)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    character = result.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    error = result.message ?: "Unknown error",
                                    isLoading = false,
                                    character = result.data
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = true,
                                    character = result.data ?: it.character
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }
}

data class CharacterDetailState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class CharacterDetailEvent {
    object Refresh : CharacterDetailEvent()
}
