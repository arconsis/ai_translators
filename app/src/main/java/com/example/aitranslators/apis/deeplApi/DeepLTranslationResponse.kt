package com.example.aitranslators.apis.deeplApi

data class DeepLTranslationResponse(
    val translations: List<DeepLTranslation>
)

data class DeepLTranslation(

    val detected_source_language: String?,
    val text: String

)