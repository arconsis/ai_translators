package com.example.aitranslators.apis.chatGPTApi

data class ChatGPTTranslationResponse(
    val id: String,
    val obj: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    val finishReason: String
)


data class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)