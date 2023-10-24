package com.example.aitranslators.services

import com.example.aitranslators.apis.chatGPTApi.ChatGPTApiClient
import com.example.aitranslators.apis.chatGPTApi.ChatGptRequestData
import com.example.aitranslators.apis.chatGPTApi.Message

class ChatGPTTranslator : TranslatorService {

    override suspend fun translateText(query: TranslationQuery): Result<TranslationResult> {
        val startTime = System.currentTimeMillis()

        try {
            val messages = mutableListOf(
                Message(role = "system", content = "You are a helpful translator."),
                Message(
                    role = "user",
                    content = "translate following text from ${query.sourceLanguage} to ${query.targetLanguage}:\n${query.text}"
                )
            )
            val requestData = ChatGptRequestData(
                model = "gpt-3.5-turbo", messages = messages
            )
            val response = ChatGPTApiClient.apiService.getCompletions(requestData)
            if (response.isSuccessful) {
                val endTime = System.currentTimeMillis()
                val responseTime = endTime - startTime
                val translationResponse = response.body()
                return if ((translationResponse != null) && translationResponse.choices.isNotEmpty()) {
                    val translationResult = TranslationResult(
                        text = translationResponse.choices[0].message.content,
                        responseTime = responseTime
                    )
                    Result.success(translationResult)
                } else {
                    val errorText = Throwable(response.errorBody()?.string())
                    Result.failure(errorText)
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorText = Throwable("API Error: ${response.code()} - $errorBody")
                return Result.failure(errorText)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun canDetectLanguage(): Boolean {
        return true
    }

    override suspend fun detectSourceLanguage(text: String): DetectedLanguage {
        try {
            val messages = mutableListOf(
                Message(role = "system", content = "You are a helpful language identifier."),
                Message(
                    role = "user",
                    content = "Detect language of following text:\n${text}"
                )
            )
            val requestData = ChatGptRequestData(
                model = "gpt-3.5-turbo", messages = messages
            )
            val response = ChatGPTApiClient.apiService.getCompletions(requestData)
            if (response.isSuccessful) {
                val translationResponse = response.body()
                return if ((translationResponse != null) && translationResponse.choices.isNotEmpty()) {
                    return DetectedLanguage(translationResponse.choices[0].message.content)
                } else {
                    val errorText = response.errorBody()?.string()
                    DetectedLanguage(errorText ?: "Error")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorText = "API Error: ${response.code()} - $errorBody"
                return DetectedLanguage(errorText)
            }
        } catch (e: Exception) {
            return DetectedLanguage(e.toString())
        }
    }
}

