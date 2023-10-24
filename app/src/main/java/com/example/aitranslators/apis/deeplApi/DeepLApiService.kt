package com.example.aitranslators.apis.deeplApi

import com.example.aitranslators.BuildConfig
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface DeepLApiService {
    @POST("translate")
    suspend fun translateText(
        @Query("auth_key") apiKey: String = BuildConfig.API_KEY_DeepL,
        @Query("text") textToTranslate: String,
        @Query("source_lang") sourceLanguage: String,
        @Query("target_lang") targetLanguage: String
    ): Response<DeepLTranslationResponse>
}