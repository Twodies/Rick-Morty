package com.twodies.rickmorty.di

import android.content.Context
import androidx.room.Room
import com.twodies.rickmorty.data.local.database.RickMortyDatabase
import com.twodies.rickmorty.data.remote.api.RickMortyApi
import com.twodies.rickmorty.data.repository.CharacterRepositoryImpl
import com.twodies.rickmorty.domain.repository.CharacterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRickMortyApi(okHttpClient: OkHttpClient): RickMortyApi {
        return Retrofit.Builder()
            .baseUrl(RickMortyApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickMortyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRickMortyDatabase(@ApplicationContext context: Context): RickMortyDatabase {
        return Room.databaseBuilder(
            context,
            RickMortyDatabase::class.java,
            RickMortyDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideCharacterDao(database: RickMortyDatabase) = database.characterDao()

    @Provides
    @Singleton
    fun provideCharacterRepository(
        api: RickMortyApi,
        database: RickMortyDatabase
    ): CharacterRepository {
        return CharacterRepositoryImpl(api, database.characterDao())
    }
}
