package com.twodies.rickmorty.domain.usecase

import android.util.Log
import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.CharacterFilter
import com.twodies.rickmorty.domain.repository.CharacterRepository
import com.twodies.rickmorty.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(
        page: Int,
        filter: CharacterFilter = CharacterFilter()
    ): Flow<Resource<List<Character>>> {
        return repository.getCharacters(page, filter)
    }
}
