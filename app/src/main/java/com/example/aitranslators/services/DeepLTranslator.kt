package com.example.aitranslators.services

import android.util.Log
import com.example.aitranslators.apis.deeplApi.DeepLApiClient
import com.example.aitranslators.languageMapReverse


class DeepLTranslator : TranslatorService {
    override suspend fun translateText(query: TranslationQuery): Result<TranslationResult> {
        val startTime = System.currentTimeMillis()

        if (query.text.isNotEmpty()) {
            try {
                val response = DeepLApiClient.deepLService.translateText(
                    textToTranslate = query.text,
                    sourceLanguage = query.sourceLanguage ?: "",
                    targetLanguage = query.targetLanguage
                )

                if (response.isSuccessful) {
                    val endTime = System.currentTimeMillis()
                    val responseTime = endTime - startTime
                    val translationResult = response.body()?.translations?.firstOrNull()
                    if (translationResult != null) {
                        return Result.success(TranslationResult(text = translationResult.text, responseTime = responseTime))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_CALL", "API Error: ${response.code()} - $errorBody")
                    return Result.failure(Throwable(errorBody))
                }
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
        return Result.failure(Throwable("Error!"))
    }

    override fun canDetectLanguage(): Boolean {
        return true
    }

    override suspend fun detectSourceLanguage(text: String): DetectedLanguage {
            try {
                val response = DeepLApiClient.deepLService.translateText(
                    textToTranslate = text,
                    sourceLanguage =  "",
                    targetLanguage = "en"
                )
                if (response.isSuccessful) {
                    val translationResult = response.body()?.translations?.firstOrNull()

                    if (translationResult != null) {
                        return DetectedLanguage(detectedLanguage = languageMapReverse[translationResult.detected_source_language?.lowercase()] ?:"Couldn't detect")
                    }
                }
            } catch (e: Exception) {
                return DetectedLanguage(e.toString())
            }
        return DetectedLanguage("Error!")
    }
}