package com.twodies.rickmorty.presentation.characters

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.twodies.rickmorty.presentation.components.CharacterCard
import com.twodies.rickmorty.presentation.components.EmptyState
import com.twodies.rickmorty.presentation.components.ErrorMessage
import com.twodies.rickmorty.presentation.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    onCharacterClick: (Int) -> Unit,
    onFilterClick: () -> Unit,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyGridState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        searchQuery = ""
        viewModel.onEvent(CharactersEvent.Search(""))
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == state.characters.size - 1 && !state.isLastPage
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !state.isLoading) {
            viewModel.onEvent(CharactersEvent.LoadNextPage)
        }
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.onEvent(CharactersEvent.Search(it))
                    },
                    onClose = {
                        isSearchActive = false
                        searchQuery = ""
                        viewModel.onEvent(CharactersEvent.Search(""))
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Rick and Morty") },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        Box {
                            IconButton(onClick = onFilterClick) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filter")
                            }
                            if (state.filter.hasActiveFilters()) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 8.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(state.isRefreshing),
            onRefresh = { viewModel.onEvent(CharactersEvent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.characters.isEmpty() -> {
                    LoadingIndicator()
                }
                state.error != null && state.characters.isEmpty() -> {
                    ErrorMessage(
                        message = state.error ?: "Unknown error",
                        onRetry = { viewModel.onEvent(CharactersEvent.Refresh) }
                    )
                }
                state.characters.isEmpty() -> {
                    EmptyState(message = "No characters found")
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.characters,
                            key = { it.id }
                        ) { character ->
                            CharacterCard(
                                character = character,
                                onClick = { onCharacterClick(character.id) }
                            )
                        }

                        if (state.isLoading && state.characters.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search characters...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Close search"
                )
            }
        }
    )
}
