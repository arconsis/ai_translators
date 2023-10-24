package com.example.aitranslators.services

import android.content.ContentValues.TAG
import android.util.Log
import com.example.aitranslators.languageMap
import com.example.aitranslators.languageMapReverse
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GoogleTranslator : TranslatorService {
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun translateText(query: TranslationQuery): Result<TranslationResult> {
        val startTime = System.currentTimeMillis()
        return suspendCancellableCoroutine { continuation ->
            GlobalScope.launch {

                if (!TranslateLanguage.getAllLanguages()
                        .contains(query.sourceLanguage) && !TranslateLanguage.getAllLanguages()
                        .contains(query.targetLanguage)
                ) {
                    continuation.resume(Result.failure(Throwable("")))
                } else {
                    val detectedLanguage: String = if (query.sourceLanguage.isNullOrBlank() && canDetectLanguage()) {
                        languageMap[detectSourceLanguage(query.text).detectedLanguage] ?: ""
                    } else {
                        query.sourceLanguage ?: ""
                    }

                    val options = TranslatorOptions.Builder()
                        .setSourceLanguage(detectedLanguage)
                        .setTargetLanguage(query.targetLanguage)
                        .build()

                    val googleTranslator = Translation.getClient(options)

                    val conditions = DownloadConditions.Builder().build()

                    googleTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener {
                            googleTranslator.translate(query.text)
                                .addOnSuccessListener { translatedText ->
                                    val endTime = System.currentTimeMillis()
                                    val responseTime = endTime - startTime
                                    val translationResult = TranslationResult(
                                        text = translatedText,
                                        responseTime = responseTime
                                    )
                                    continuation.resume(Result.success(translationResult))
                                }
                                .addOnFailureListener { exception ->
                                    continuation.resume(Result.failure(exception))
                                }
                        }
                        .addOnFailureListener { exception ->
                            val error = Throwable(
                                message = "-> Model couldn't be downloaded or other internal error. Please try again and wait for the required model to download.",
                                cause = exception
                            )

                            continuation.resume(Result.failure(error))

                        }

                    // Cancel the operation if the coroutine is cancelled
                    continuation.invokeOnCancellation {
                        googleTranslator.close()
                    }
                }
            }
        }
    }

    override fun canDetectLanguage(): Boolean {
        return true
    }


    override suspend fun detectSourceLanguage(text: String): DetectedLanguage {
        return suspendCancellableCoroutine { continuation ->
            val languageIdentifier = LanguageIdentification.getClient()
            languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener { languageCode ->
                    if (languageCode == "und") {
                        Log.i(TAG, "Can't identify language.")
                        continuation.resume(DetectedLanguage("")) // Language couldn't be identified
                    } else {
                        Log.i(TAG, "Language: $languageCode")
                        continuation.resume(DetectedLanguage(languageMapReverse[languageCode] ?: "Couldn't detect"))
                    }
                }
                .addOnFailureListener {
                    // Model couldn't be loaded or other internal error.
                    continuation.resumeWithException(Throwable(""))
                }
            // Cancel the operation if the coroutine is cancelled
            continuation.invokeOnCancellation {
                languageIdentifier.close()
            }
        }
    }
}