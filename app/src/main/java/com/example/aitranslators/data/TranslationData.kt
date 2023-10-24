package com.example.aitranslators.data

import com.example.aitranslators.services.TranslatorService

data class Translator(
    val service: TranslatorService,
    val name: String,
    val translatedText: String? = null,
    val detectedLanguage: String? = null,
    val responseTime: Long = 0,
    val responseTimeLanguageDetection: Long = 0,
    val error: Boolean = false
)

data class TranslationData(
    val translators: List<Translator> = listOf(),
    val buttonEnabled: List<Boolean> = listOf(),
    val isStarClickedList: List<Boolean> = listOf(),
    val viewModelInitialized: Boolean = false,
    val searchInputEmpty: Boolean = false,
    val noResult: Boolean = false,
    val sourceLanguage: String = "",
    val targetLanguage: String = "",
    val textToTranslate: String = "",
    val sourceLanguageSelected: Boolean = false,
    val targetLanguageSelected: Boolean = false,
    val readyToTranslate: Boolean = true,
    val detectingLanguage: Boolean = false,
    val detectedLanguage: String = "",
    val errorText: String = "",
    val detectedLanguageCode: String = "",
)
