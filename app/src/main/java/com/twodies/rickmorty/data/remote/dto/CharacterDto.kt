package com.twodies.rickmorty.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.twodies.rickmorty.domain.model.Character
import com.twodies.rickmorty.domain.model.Location

data class CharacterDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("species") val species: String,
    @SerializedName("type") val type: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("origin") val origin: LocationDto,
    @SerializedName("location") val location: LocationDto,
    @SerializedName("image") val image: String,
    @SerializedName("episode") val episode: List<String>,
    @SerializedName("url") val url: String,
    @SerializedName("created") val created: String
)

fun CharacterDto.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = Location(origin.name, origin.url),
        location = Location(location.name, location.url),
        image = image,
        episode = episode,
        url = url,
        created = created
    )
}
