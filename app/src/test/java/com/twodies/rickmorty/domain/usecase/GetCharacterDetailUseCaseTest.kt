package com.twodies.rickmorty.domain.usecase

import app.cash.turbine.test
import com.twodies.rickmorty.domain.model.Character
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

class GetCharacterDetailUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: GetCharacterDetailUseCase

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
        useCase = GetCharacterDetailUseCase(repository)
    }

    @Test
    fun `invoke returns character from repository`() = runTest {
        coEvery {
            repository.getCharacter(1)
        } returns flowOf(Resource.Success(mockCharacter))

        useCase(1).test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals(mockCharacter, result.data)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when character not found`() = runTest {
        val errorMessage = "Character not found"
        coEvery {
            repository.getCharacter(1)
        } returns flowOf(Resource.Error(errorMessage))

        useCase(1).test {
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertEquals(errorMessage, result.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits loading state`() = runTest {
        coEvery {
            repository.getCharacter(1)
        } returns flowOf(Resource.Loading())

        useCase(1).test {
            val result = awaitItem()
            assertTrue(result is Resource.Loading)
            awaitComplete()
        }
    }
}
