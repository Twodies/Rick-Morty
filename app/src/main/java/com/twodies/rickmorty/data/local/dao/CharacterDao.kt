package com.twodies.rickmorty.data.local.dao

import androidx.room.*
import com.twodies.rickmorty.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Query("""
        SELECT * FROM characters
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:status IS NULL OR status = :status)
        AND (:species IS NULL OR species LIKE '%' || :species || '%')
        AND (:type IS NULL OR type LIKE '%' || :type || '%')
        AND (:gender IS NULL OR gender = :gender)
        ORDER BY id ASC
    """)
    fun searchCharacters(
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): Flow<List<CharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()

    @Query("DELETE FROM characters WHERE timestamp < :timestamp")
    suspend fun deleteOldCharacters(timestamp: Long)
}
