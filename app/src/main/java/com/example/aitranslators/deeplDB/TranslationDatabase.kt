package com.example.aitranslators.deeplDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TranslationEntity::class], version = 2/*, exportSchema = true*/)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao
    companion object {
        fun getDatabase(context: Context): TranslationDatabase {
            return Room.databaseBuilder(
                    context.applicationContext,
                    TranslationDatabase::class.java,
                    "translation_database"
                ).allowMainThreadQueries().build()  //.fallbackToDestructiveMigration()  --> does´t help
        }
    }
}
