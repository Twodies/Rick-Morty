package com.twodies.rickmorty.domain.usecase

import app.cash.turbine.test
import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.CharacterFilter
import com.twodies.rickmorty.domain.model.Location
import com.twodies.rickmorty.domain.repository.CharacterRepository
import com.twodies.rickmorty.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetCharactersUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: GetCharactersUseCase

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
        repository = mockk()
        useCase = GetCharactersUseCase(repository)
    }

    @Test
    fun `invoke returns success from repository`() = runTest {
        val characters = listOf(mockCharacter)
        coEvery {
            repository.getCharacters(1, any())
        } returns flowOf(Resource.Success(characters))

        useCase(1, CharacterFilter()).test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals(characters, result.data)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error from repository`() = runTest {
        val errorMessage = "Network error"
        coEvery {
            repository.getCharacters(1, any())
        } returns flowOf(Resource.Error(errorMessage))

        useCase(1, CharacterFilter()).test {
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, result.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits loading state`() = runTest {
        coEvery {
            repository.getCharacters(1, any())
        } returns flowOf(Resource.Loading())

        useCase(1, CharacterFilter()).test {
            val result = awaitItem()
            assertTrue(result is Resource.Loading)
            awaitComplete()
        }
    }

    @Test
    fun `invoke passes filter to repository`() = runTest {
        val filter = CharacterFilter(name = "Rick")
        coEvery {
            repository.getCharacters(1, filter)
        } returns flowOf(Resource.Success(listOf(mockCharacter)))

        useCase(1, filter).test {
            awaitItem()
            awaitComplete()
        }
    }
}
