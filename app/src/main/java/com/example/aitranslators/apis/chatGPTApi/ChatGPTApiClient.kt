package com.example.aitranslators.apis.chatGPTApi


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object ChatGPTApiClient {

    private const val BASE_URL = "https://api.openai.com/"

    private val httpClient = OkHttpClient.Builder()
    //    .callTimeout(30L,TimeUnit.SECONDS)
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val apiService : ChatGPTApiService = retrofit.create(ChatGPTApiService::class.java)

}
