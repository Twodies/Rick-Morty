package com.twodies.rickmorty.domain.model

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class CharacterFilterTest {

    @Test
    fun `hasActiveFilters returns false for empty filter`() {
        val filter = CharacterFilter()
        assertFalse(filter.hasActiveFilters())
    }

    @Test
    fun `hasActiveFilters returns true when status is set`() {
        val filter = CharacterFilter(status = Status.ALIVE)
        assertTrue(filter.hasActiveFilters())
    }

    @Test
    fun `hasActiveFilters returns true when gender is set`() {
        val filter = CharacterFilter(gender = Gender.MALE)
        assertTrue(filter.hasActiveFilters())
    }

    @Test
    fun `hasActiveFilters returns true when species is set`() {
        val filter = CharacterFilter(species = "Human")
        assertTrue(filter.hasActiveFilters())
    }

    @Test
    fun `hasActiveFilters returns true when type is set`() {
        val filter = CharacterFilter(type = "Genetic experiment")
        assertTrue(filter.hasActiveFilters())
    }

    @Test
    fun `hasActiveFilters returns true when multiple filters are set`() {
        val filter = CharacterFilter(
            status = Status.ALIVE,
            gender = Gender.MALE,
            species = "Human"
        )
        assertTrue(filter.hasActiveFilters())
    }

    @Test
    fun `hasActiveFilters ignores name field`() {
        val filter = CharacterFilter(name = "Rick")
        assertFalse(filter.hasActiveFilters())
    }
}
