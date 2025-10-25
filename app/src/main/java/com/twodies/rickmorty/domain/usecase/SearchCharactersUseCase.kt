package com.twodies.rickmorty.domain.usecase

import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.CharacterFilter
import com.twodies.rickmorty.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(filter: CharacterFilter): Flow<List<Character>> {
        return repository.searchCharacters(filter)
    }
}
