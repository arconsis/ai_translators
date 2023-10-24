package com.example.aitranslators.services

data class TranslationQuery(
    val text: String = "",
    val sourceLanguage: String? = "",
    val targetLanguage: String = ""
)
