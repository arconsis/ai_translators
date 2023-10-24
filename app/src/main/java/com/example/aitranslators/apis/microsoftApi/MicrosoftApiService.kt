package com.example.aitranslators.apis.microsoftApi

import com.example.aitranslators.BuildConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface MicrosoftApiService {
    @Headers(
        "Ocp-Apim-Subscription-Key: ${BuildConfig.API_KEY_Microsoft}",
        "Ocp-Apim-Subscription-Region: germanywestcentral",
        "Content-Type: application/json"
    )
    @POST("translate")
    suspend fun translateText(
        @Query("api-version") apiVersion: String = "3.0",
        @Query("from") fromLanguage: String,
        @Query("to") toLanguage: String,
        @Body request: List<TranslateRequestItem>
    ): Response<List<TranslationResult>>
}