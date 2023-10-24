package com.example.aitranslators.apis.chatGPTApi


import com.example.aitranslators.BuildConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGPTApiService {
    @Headers("Content-Type: application/json", "Authorization: Bearer ${BuildConfig.API_KEY_ChatGPT}")
    @POST("v1/chat/completions ")
    suspend fun getCompletions(@Body completionResponse: ChatGptRequestData) : Response<ChatGPTTranslationResponse>
}