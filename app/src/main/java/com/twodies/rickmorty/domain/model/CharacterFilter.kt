package com.twodies.rickmorty.domain.model

data class CharacterFilter(
    val name: String = "",
    val status: Status? = null,
    val species: String = "",
    val type: String = "",
    val gender: Gender? = null
) {
    fun hasActiveFilters(): Boolean {
        return status != null || species.isNotEmpty() || type.isNotEmpty() || gender != null
    }
}

enum class Status(val value: String) {
    ALIVE("alive"),
    DEAD("dead"),
    UNKNOWN("unknown")
}

enum class Gender(val value: String) {
    FEMALE("female"),
    MALE("male"),
    GENDERLESS("genderless"),
    UNKNOWN("unknown")
}
