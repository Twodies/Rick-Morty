package com.twodies.rickmorty.presentation.characters

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.CharacterFilter
import com.twodies.rickmorty.domain.usecase.GetCharactersUseCase
import com.twodies.rickmorty.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CharactersState())
    val state: StateFlow<CharactersState> = _state.asStateFlow()

    private var currentPage = 1
    private var searchJob: Job? = null

    init {
        loadCharacters()
    }


    fun onEvent(event: CharactersEvent) {
        when (event) {
            is CharactersEvent.LoadNextPage -> {
                if (!_state.value.isLoading && !_state.value.isLastPage) {
                    currentPage++
                    loadCharacters(append = true)
                }
            }
            is CharactersEvent.Refresh -> {
                currentPage = 1
                _state.update { it.copy(characters = emptyList(), isRefreshing = true) }
                loadCharacters(forceRefresh = true)
            }
            is CharactersEvent.Search -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query,
                        characters = emptyList(),
                        isLoading = true
                    )
                }
                currentPage = 1
                loadCharacters()
            }
            is CharactersEvent.ApplyFilter -> {
                _state.update {
                    it.copy(
                        filter = event.filter,
                        characters = emptyList(),
                        isLoading = true
                    )
                }
                currentPage = 1
                loadCharacters()
            }
            is CharactersEvent.ClearFilter -> {
                _state.update {
                    it.copy(
                        filter = CharacterFilter(),
                        searchQuery = "",
                        characters = emptyList(),
                        isLoading = true
                    )
                }
                currentPage = 1
                loadCharacters()
            }
        }
    }

    private fun loadCharacters(
        forceRefresh: Boolean = false,
        append: Boolean = false
    ) {
        searchJob?.cancel()

        val filter = _state.value.filter.copy(
            name = _state.value.searchQuery
        )

        searchJob = getCharactersUseCase(currentPage, filter)
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { characters ->
                            val isLastPage = characters.isEmpty()

                            _state.update { currentState ->
                                currentState.copy(
                                    characters = if (append) {
                                        currentState.characters + characters
                                    } else {
                                        characters
                                    },
                                    isLoading = false,
                                    isRefreshing = false,
                                    error = null,
                                    isLastPage = isLastPage
                                )
                            }
                        }
                    }
                    is Resource.Error -> {

                        if (append && _state.value.characters.isNotEmpty()) {
                            _state.update {
                                it.copy(
                                    error = null,
                                    isLoading = false,
                                    isRefreshing = false,
                                    isLastPage = true
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    error = result.message ?: "Unknown error",
                                    isLoading = false,
                                    isRefreshing = false
                                )
                            }
                        }
                    }
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = !forceRefresh && !append,
                                isRefreshing = forceRefresh,
                                characters = result.data ?: it.characters
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}

data class CharactersState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val filter: CharacterFilter = CharacterFilter(),
    val isLastPage: Boolean = false
)

sealed class CharactersEvent {
    object LoadNextPage : CharactersEvent()
    object Refresh : CharactersEvent()
    data class Search(val query: String) : CharactersEvent()
    data class ApplyFilter(val filter: CharacterFilter) : CharactersEvent()
    object ClearFilter : CharactersEvent()
}
