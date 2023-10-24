package com.example.aitranslators.deeplDB

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DaoModule {
    @Provides
    @Singleton
    fun provideTranslationDatabase(@ApplicationContext context: Context): TranslationDatabase {
        return TranslationDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTranslationDao(database: TranslationDatabase): TranslationDao {
        return database.translationDao()
    }
}