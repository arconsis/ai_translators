package com.example.aitranslators.services

import android.util.Log
import com.example.aitranslators.apis.microsoftApi.MicrosoftApiClient
import com.example.aitranslators.apis.microsoftApi.TranslateRequestItem
import com.example.aitranslators.languageMapReverse

class MicrosoftTranslator : TranslatorService {
    override suspend fun translateText(query: TranslationQuery): Result<TranslationResult> {
        val startTime = System.currentTimeMillis()
        val translateRequestItem = TranslateRequestItem(query.text)
        val response = MicrosoftApiClient.microsoftApiService.translateText(
            fromLanguage = query.sourceLanguage ?: "",
            toLanguage = query.targetLanguage,
            request = listOf(translateRequestItem)
        )
        return if (response.isSuccessful) {
            val endTime = System.currentTimeMillis()
            val responseTime = endTime - startTime
            val translationResult = response.body()?.firstOrNull()
            val translatedText = translationResult?.translations?.firstOrNull()
            if (translatedText != null) {
                Result.success(
                    TranslationResult(
                        text = translatedText.text,
                        responseTime = responseTime
                    )
                )
            } else {
                Result.failure(Throwable("No Response by Microsoft"))
            }

        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("API_CALL", "API Error: ${response.code()} - $errorBody")
            Result.failure(Throwable(errorBody))
        }
    }

    override fun canDetectLanguage(): Boolean {
        return true
    }

    override suspend fun detectSourceLanguage(text: String): DetectedLanguage {
        val translateRequestItem = TranslateRequestItem(text)
        val response = MicrosoftApiClient.microsoftApiService.translateText(
            fromLanguage = "",
            toLanguage = "en",
            request = listOf(translateRequestItem)
        )
        return if (response.isSuccessful) {
            val translationResult = response.body()?.firstOrNull()
            val translatedText = translationResult?.translations?.firstOrNull()
            if (translatedText != null) {
                DetectedLanguage(detectedLanguage = languageMapReverse[translationResult.detectedLanguage?.language] ?: "Couldn't detect")
            } else {
                DetectedLanguage("No Response by Microsoft")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("API_CALL", "API Error: ${response.code()} - $errorBody")
            DetectedLanguage(errorBody ?: "Error")
        }
    }
}