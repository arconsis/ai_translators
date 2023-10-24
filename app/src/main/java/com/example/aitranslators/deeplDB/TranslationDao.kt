package com.example.aitranslators.deeplDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow



@Dao
interface TranslationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(translation: TranslationEntity)

    @Query("SELECT * FROM translations")
     fun getAllTranslations(): Flow<List<TranslationEntity>>

    @Delete()
     fun delete(translation: TranslationEntity)
}