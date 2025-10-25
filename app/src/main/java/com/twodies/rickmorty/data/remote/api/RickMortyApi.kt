package com.twodies.rickmorty.data.remote.api

import com.twodies.rickmorty.data.remote.dto.CharacterDto
import com.twodies.rickmorty.data.remote.dto.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickMortyApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
    ): CharacterResponse

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): CharacterDto

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}
