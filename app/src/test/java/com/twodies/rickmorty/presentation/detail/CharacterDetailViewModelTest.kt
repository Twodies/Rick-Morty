package com.twodies.rickmorty.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.Location
import com.twodies.rickmorty.domain.usecase.GetCharacterDetailUseCase
import com.twodies.rickmorty.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getCharacterDetailUseCase: GetCharacterDetailUseCase
    private lateinit var savedStateHandle: SavedStateHandle

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
        episode = listOf("S01E01", "S01E02"),
        url = "",
        created = ""
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCharacterDetailUseCase = mockk()
        savedStateHandle = SavedStateHandle(mapOf("characterId" to 1))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads character detail successfully`() = runTest {
        coEvery {
            getCharacterDetailUseCase(1)
        } returns flowOf(Resource.Success(mockCharacter))

        val viewModel = CharacterDetailViewModel(getCharacterDetailUseCase, savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.character)
        assertEquals(mockCharacter.name, state.character?.name)
    }

    @Test
    fun `handles error loading character`() = runTest {
        val errorMessage = "Character not found"
        coEvery {
            getCharacterDetailUseCase(1)
        } returns flowOf(Resource.Error(errorMessage))

        val viewModel = CharacterDetailViewModel(getCharacterDetailUseCase, savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(errorMessage, state.error)
    }
}
