package com.twodies.rickmorty.domain.usecase

import app.cash.turbine.test
import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.CharacterFilter
import com.twodies.rickmorty.domain.model.Location
import com.twodies.rickmorty.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SearchCharactersUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: SearchCharactersUseCase

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
        useCase = SearchCharactersUseCase(repository)
    }

    @Test
    fun `invoke returns filtered characters`() = runTest {
        val filter = CharacterFilter(name = "Rick")
        val characters = listOf(mockCharacter)
        coEvery {
            repository.searchCharacters(filter)
        } returns flowOf(characters)

        useCase(filter).test {
            val result = awaitItem()
            assertEquals(characters, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty list when no matches`() = runTest {
        val filter = CharacterFilter(name = "NonExistent")
        coEvery {
            repository.searchCharacters(filter)
        } returns flowOf(emptyList())

        useCase(filter).test {
            val result = awaitItem()
            assertEquals(emptyList(), result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke passes correct filter to repository`() = runTest {
        val filter = CharacterFilter(name = "Morty", species = "Human")
        coEvery {
            repository.searchCharacters(filter)
        } returns flowOf(listOf(mockCharacter))

        useCase(filter).test {
            awaitItem()
            awaitComplete()
        }
    }
}
