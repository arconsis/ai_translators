package com.example.aitranslators.apis.microsoftApi

data class TranslationResult(
    val detectedLanguage: DetectedLanguage?,
    val translations: List<Translation>
)

data class Translation(
    val text: String,
    val to: String,
)

data class DetectedLanguage(
    val language: String,
    val score: Double
)
