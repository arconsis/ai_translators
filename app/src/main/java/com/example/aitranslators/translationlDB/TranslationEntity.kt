package com.example.aitranslators.deeplDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val textToTranslate: String,
    val translation: String
)