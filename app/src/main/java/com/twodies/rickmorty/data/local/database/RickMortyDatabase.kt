package com.twodies.rickmorty.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.twodies.rickmorty.data.local.dao.CharacterDao
import com.twodies.rickmorty.data.local.entity.CharacterEntity

@Database(
    entities = [CharacterEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RickMortyDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao

    companion object {
        const val DATABASE_NAME = "rick_morty_db"
    }
}
