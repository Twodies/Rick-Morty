package com.twodies.rickmorty.domain.usecase

import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.repository.CharacterRepository
import com.twodies.rickmorty.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(id: Int): Flow<Resource<Character>> {
        return repository.getCharacter(id)
    }
}
