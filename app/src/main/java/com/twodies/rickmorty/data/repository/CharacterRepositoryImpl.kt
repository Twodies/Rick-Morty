package com.twodies.rickmorty.data.repository

import android.util.Log
import com.twodies.rickmorty.data.local.dao.CharacterDao
import com.twodies.rickmorty.data.local.entity.toDomain
import com.twodies.rickmorty.data.local.entity.toEntity
import com.twodies.rickmorty.data.remote.api.RickMortyApi
import com.twodies.rickmorty.data.remote.dto.toDomain
import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.CharacterFilter
import com.twodies.rickmorty.domain.repository.CharacterRepository
import com.twodies.rickmorty.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: RickMortyApi,
    private val dao: CharacterDao
) : CharacterRepository {

    override fun getCharacters(
        page: Int,
        filter: CharacterFilter
    ): Flow<Resource<List<Character>>> = flow {
        emit(Resource.Loading())
        val cachedCharacters = if (filter.isEmptyFilter()) {
            dao.getAllCharacters().map { entities ->
                entities.map { it.toDomain() }
            }
        } else {
            dao.searchCharacters(
                name = filter.name.takeIf { it.isNotEmpty() },
                status = filter.status?.value,
                species = filter.species.takeIf { it.isNotEmpty() },
                type = filter.type.takeIf { it.isNotEmpty() },
                gender = filter.gender?.value
            ).map { entities ->
                entities.map { it.toDomain() }
            }
        }

        val cachedData = cachedCharacters.firstOrNull() ?: emptyList()
        if (cachedData.isNotEmpty()) {
            emit(Resource.Loading(cachedData))
        }

        try {

            val response = api.getCharacters(
                page = page,
                name = filter.name.takeIf { it.isNotEmpty() },
                status = filter.status?.value,
                species = filter.species.takeIf { it.isNotEmpty() },
                type = filter.type.takeIf { it.isNotEmpty() },
                gender = filter.gender?.value
            )

            val characters = response.results.map { it.toDomain() }
            dao.insertCharacters(characters.map { it.toEntity() })

            emit(Resource.Success(characters))

        } catch (e: HttpException) {

            if (e.code() == 404) {
                emit(Resource.Success(emptyList()))
            } else {
                emit(Resource.Error(
                    message = "Server error: ${e.localizedMessage}",
                    data = null
                ))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emit(Resource.Error(
                message = "Network error. Showing cached data.",
                data = null
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(
                message = "An unexpected error occurred: ${e.localizedMessage}",
                data = null
            ))
        } }

    override suspend fun getCharacter(id: Int): Flow<Resource<Character>> = flow {
        emit(Resource.Loading())

        val cachedCharacter = dao.getCharacterById(id)
        if (cachedCharacter != null) {
            emit(Resource.Loading(cachedCharacter.toDomain()))
        }

        try {
            val character = api.getCharacter(id).toDomain()
            dao.insertCharacter(character.toEntity())
            emit(Resource.Success(character))
        } catch (e: HttpException) {
            emit(Resource.Error(
                message = "Server error: ${e.localizedMessage}",
                data = cachedCharacter?.toDomain()
            ))
        } catch (e: IOException) {
            emit(Resource.Error(
                message = "Network error. Showing cached data.",
                data = cachedCharacter?.toDomain()
            ))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An unexpected error occurred: ${e.localizedMessage}",
                data = cachedCharacter?.toDomain()
            ))
        }
    }

    override fun searchCharacters(filter: CharacterFilter): Flow<List<Character>> {
        return dao.searchCharacters(
            name = filter.name.takeIf { it.isNotEmpty() },
            status = filter.status?.value,
            species = filter.species.takeIf { it.isNotEmpty() },
            type = filter.type.takeIf { it.isNotEmpty() },
            gender = filter.gender?.value
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun CharacterFilter.isEmptyFilter(): Boolean {
        return name.isEmpty() && status == null && species.isEmpty() &&
               type.isEmpty() && gender == null
    }
}
