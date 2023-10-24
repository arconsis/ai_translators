package com.example.aitranslators.apis.microsoftApi

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MicrosoftApiClient {

    private const val BASE_URL = "https://api.cognitive.microsofttranslator.com/"


    private val httpClient = OkHttpClient.Builder()
        //    .callTimeout(30L,TimeUnit.SECONDS)
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val microsoftApiService: MicrosoftApiService = retrofit.create(MicrosoftApiService::class.java)
}