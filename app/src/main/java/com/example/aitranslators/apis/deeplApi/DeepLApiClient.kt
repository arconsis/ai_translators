package com.example.aitranslators.apis.deeplApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DeepLApiClient {
    private const val baseUrl = "https://api-free.deepl.com/v2/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val deepLService: DeepLApiService = retrofit.create(DeepLApiService::class.java)
}