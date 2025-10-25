package com.twodies.rickmorty.presentation.characters

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.CharacterFilter
import com.twodies.rickmorty.domain.model.Gender
import com.twodies.rickmorty.domain.model.Location
import com.twodies.rickmorty.domain.model.Status
import com.twodies.rickmorty.domain.usecase.GetCharactersUseCase
import com.twodies.rickmorty.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getCharactersUseCase: GetCharactersUseCase
    private lateinit var viewModel: CharactersViewModel

    private val mockCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = Location("Earth", ""),
        location = Location("Earth", ""),
        image = "https://example.com/rick.jpg",
        episode = listOf("S01E01"),
        url = "",
        created = ""
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCharactersUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads characters successfully`() = runTest {
        val characters = listOf(mockCharacter)
        coEvery {
            getCharactersUseCase(1, any())
        } returns flowOf(Resource.Success(characters))

        viewModel = CharactersViewModel(getCharactersUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(characters, state.characters)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun `search event updates characters`() = runTest {
        val initialCharacters = listOf(mockCharacter)
        val searchResults = listOf(mockCharacter.copy(name = "Morty Smith"))

        coEvery {
            getCharactersUseCase(1, match { it.name.isEmpty() })
        } returns flowOf(Resource.Success(initialCharacters))

        coEvery {
            getCharactersUseCase(1, match { it.name == "Morty" })
        } returns flowOf(Resource.Success(searchResults))

        viewModel = CharactersViewModel(getCharactersUseCase)
        advanceUntilIdle()

        viewModel.onEvent(CharactersEvent.Search("Morty"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Morty", state.searchQuery)
    }

    @Test
    fun `apply filter event updates state`() = runTest {
        val filter = CharacterFilter(status = Status.ALIVE, gender = Gender.MALE)
        val filteredCharacters = listOf(mockCharacter)

        coEvery {
            getCharactersUseCase(1, match { it.status == null })
        } returns flowOf(Resource.Success(listOf(mockCharacter)))

        coEvery {
            getCharactersUseCase(1, match { it.status == Status.ALIVE })
        } returns flowOf(Resource.Success(filteredCharacters))

        viewModel = CharactersViewModel(getCharactersUseCase)
        advanceUntilIdle()

        viewModel.onEvent(CharactersEvent.ApplyFilter(filter))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(filter, state.filter)
    }

    @Test
    fun `clear filter resets state`() = runTest {
        val filter = CharacterFilter(status = Status.ALIVE)

        coEvery {
            getCharactersUseCase(1, any())
        } returns flowOf(Resource.Success(listOf(mockCharacter)))

        viewModel = CharactersViewModel(getCharactersUseCase)
        advanceUntilIdle()

        viewModel.onEvent(CharactersEvent.ApplyFilter(filter))
        advanceUntilIdle()

        viewModel.onEvent(CharactersEvent.ClearFilter)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(CharacterFilter(), state.filter)
        assertEquals("", state.searchQuery)
    }

    @Test
    fun `error state is handled correctly`() = runTest {
        val errorMessage = "Network error"
        coEvery {
            getCharactersUseCase(1, any())
        } returns flowOf(Resource.Error(errorMessage))

        viewModel = CharactersViewModel(getCharactersUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(errorMessage, state.error)
    }
}
