package com.example.aitranslators.apis.chatGPTApi

data class ChatGptRequestData(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)
