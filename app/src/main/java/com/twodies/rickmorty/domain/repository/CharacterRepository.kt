package com.twodies.rickmorty.domain.repository

import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.CharacterFilter
import com.twodies.rickmorty.util.Resource
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getCharacters(
        page: Int,
        filter: CharacterFilter = CharacterFilter()
    ): Flow<Resource<List<Character>>>

    suspend fun getCharacter(id: Int): Flow<Resource<Character>>

    fun searchCharacters(filter: CharacterFilter): Flow<List<Character>>
}
