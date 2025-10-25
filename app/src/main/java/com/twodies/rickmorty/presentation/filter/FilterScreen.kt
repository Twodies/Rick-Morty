package com.twodies.rickmorty.presentation.filter

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.twodies.rickmorty.domain.model.CharacterFilter
import com.twodies.rickmorty.domain.model.Gender
import com.twodies.rickmorty.domain.model.Status
import com.twodies.rickmorty.presentation.characters.CharactersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    onBackClick: () -> Unit,
    onApplyFilter: () -> Unit,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedStatus by remember { mutableStateOf<Status?>(null) }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var speciesInput by remember { mutableStateOf("") }
    var typeInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        selectedStatus = state.filter.status
        selectedGender = state.filter.gender
        speciesInput = state.filter.species
        typeInput = state.filter.type
        Log.d("FilterScreen", "Initialized with current filter: status=$selectedStatus, gender=$selectedGender, species='$speciesInput', type='$typeInput'")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filters") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            FilterSection(title = "Status") {
                ChipGroup(
                    options = listOf(null) + Status.values().toList(),
                    selectedOption = selectedStatus,
                    onOptionSelected = { selectedStatus = it },
                    labelProvider = { it?.name?.lowercase()?.replaceFirstChar { c -> c.uppercase() } ?: "All" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(title = "Gender") {
                ChipGroup(
                    options = listOf(null) + Gender.values().toList(),
                    selectedOption = selectedGender,
                    onOptionSelected = { selectedGender = it },
                    labelProvider = { it?.name?.lowercase()?.replaceFirstChar { c -> c.uppercase() } ?: "All" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(title = "Species") {
                OutlinedTextField(
                    value = speciesInput,
                    onValueChange = { speciesInput = it },
                    placeholder = { Text("e.g. Human, Alien") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(title = "Type") {
                OutlinedTextField(
                    value = typeInput,
                    onValueChange = { typeInput = it },
                    placeholder = { Text("e.g. Genetic experiment") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        Log.d("FilterScreen", "Clear button clicked")
                        selectedStatus = null
                        selectedGender = null
                        speciesInput = ""
                        typeInput = ""
                        viewModel.onEvent(
                            com.twodies.rickmorty.presentation.characters.CharactersEvent.ClearFilter
                        )
                        onBackClick()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }

                Button(
                    onClick = {
                        val filter = CharacterFilter(
                            status = selectedStatus,
                            gender = selectedGender,
                            species = speciesInput,
                            type = typeInput
                        )
                        Log.d("FilterScreen", "Apply button clicked with filter: $filter")
                        Log.d("FilterScreen", "Status: $selectedStatus, Gender: $selectedGender, Species: '$speciesInput', Type: '$typeInput'")
                        viewModel.onEvent(
                            com.twodies.rickmorty.presentation.characters.CharactersEvent.ApplyFilter(filter)
                        )
                        onApplyFilter()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun <T> ChipGroup(
    options: List<T?>,
    selectedOption: T?,
    onOptionSelected: (T?) -> Unit,
    labelProvider: (T?) -> String
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selectedOption == option,
                onClick = {
                    Log.d("FilterScreen", "Chip clicked: ${labelProvider(option)}")
                    onOptionSelected(option)
                },
                label = { Text(labelProvider(option)) },
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}
