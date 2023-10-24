package com.example.aitranslators.services

interface TranslatorService {
    suspend fun translateText(query: TranslationQuery) : Result<TranslationResult>

    fun canDetectLanguage () : Boolean

    suspend fun detectSourceLanguage(text: String): DetectedLanguage
}