package com.example.aitranslators

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aitranslators.data.TranslationData
import com.example.aitranslators.data.Translator
import com.example.aitranslators.deeplDB.TranslationDB
import com.example.aitranslators.deeplDB.TranslationEntity
import com.example.aitranslators.deeplDB.TranslationRepository
import com.example.aitranslators.services.DetectedLanguage
import com.example.aitranslators.services.TranslationQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TranslationScreenViewModel @Inject constructor(
    private val translationRepository: TranslationRepository
) : ViewModel() {


    private val _translationData: MutableStateFlow<TranslationData> =
        MutableStateFlow(TranslationData())
    val translationData: StateFlow<TranslationData> = _translationData.asStateFlow()


    private val _savedTranslationsInDB: MutableStateFlow<TranslationDB> =
        MutableStateFlow(TranslationDB())
    val savedTranslationsInDB: StateFlow<TranslationDB> = _savedTranslationsInDB.asStateFlow()


    init {
        viewModelScope.launch {
            translationRepository.allTranslations.collect { allTranslations ->
                _savedTranslationsInDB.update { prev -> prev.copy(translations = allTranslations) }
            }
        }
    }


    fun initializeTranslators(translators: List<Translator>) {
        _translationData.update {
            it.copy(
                translators = translators,
                buttonEnabled = MutableList(translators.size) {true},
                isStarClickedList = MutableList(translators.size) {false}
            )
        }
    }


    fun initializeUiStrings (sourceLan: String, targetLan: String, errorText: String){
        _translationData.update {
            it.copy(
                sourceLanguage = sourceLan,
                targetLanguage = targetLan,
                errorText = errorText,
                viewModelInitialized = true
            )
        }
    }

    fun checkInput(s: String) {
        _translationData.update { it.copy(searchInputEmpty = s.isBlank()) }
    }

    fun isSourceLanguageSelected(b: Boolean) {
        _translationData.update { it.copy(sourceLanguageSelected = b) }
    }

    fun isTargetLanguageSelected(b: Boolean) {
        _translationData.update { it.copy(targetLanguageSelected = b) }
    }

    fun checkReadyToTranslate() {
        _translationData.update { it.copy(readyToTranslate = _translationData.value.sourceLanguageSelected && _translationData.value.targetLanguageSelected) }
    }

    fun setSourceLang(s: String) {
        _translationData.update { it.copy(sourceLanguage = s) }
    }

    fun setTargetLang(s: String) {
        _translationData.update { it.copy(targetLanguage = s) }
    }

    fun updateTextToTranslate(s: String) {
        _translationData.update { it.copy(textToTranslate = s) }
    }


    fun updateStarIconList (currentPage: Int,b: Boolean){
        _translationData.update { it.copy(isStarClickedList = it.isStarClickedList.toMutableList().apply { this[currentPage] = b }) }
    }

    fun setAllStarIconsFalse () {
        _translationData.update { it.copy(isStarClickedList = MutableList(it.isStarClickedList.size) { false })}

    }

    fun updateNoResultState(b: Boolean) {
        _translationData.update { (it.copy(noResult = b)) }
    }


    fun setDetectingLanguage(b: Boolean) {
        _translationData.update { it.copy(detectingLanguage = b) }
    }




    fun translate(currentPage: Int) = viewModelScope.launch {

        if (_translationData.value.sourceLanguageSelected && _translationData.value.targetLanguageSelected && _translationData.value.textToTranslate.isNotEmpty()) {
            _translationData.update { it.copy(buttonEnabled= it.buttonEnabled.toMutableList().apply { this[currentPage] = false })}


            val startDetectionTime = System.currentTimeMillis()
            val detectLanguage = if (_translationData.value.detectingLanguage) _translationData.value.translators[currentPage].service.detectSourceLanguage(_translationData.value.textToTranslate) else DetectedLanguage("")
            val detectionTime = System.currentTimeMillis() - startDetectionTime

            val translators = _translationData.value.translators.toMutableList()
            val query = TranslationQuery(
                text = _translationData.value.textToTranslate,
                sourceLanguage = if (_translationData.value.detectingLanguage) "" else languageMap[_translationData.value.sourceLanguage],
                targetLanguage = languageMap[_translationData.value.targetLanguage] ?:""
            )
            _translationData.value.translators[currentPage].service.translateText(query)
                .onSuccess { translation ->
                    val translator = translators[currentPage].copy(
                        translatedText = translation.text,
                        detectedLanguage = detectLanguage.detectedLanguage,
                        responseTime = translation.responseTime,
                        responseTimeLanguageDetection = detectionTime,
                        error = false
                    )
                    translators[currentPage] = translator
                    _translationData.update {
                        it.copy(
                            noResult = false,
                            translators = translators
                        )
                    }
                }
                .onFailure { error ->
                    val translator = translators[currentPage].copy(
                        translatedText = "${_translationData.value.errorText} ${error.message}",
                        error = true
                    )
                    translators[currentPage] = translator
                    _translationData.update {
                        it.copy(
                            noResult = true,
                            translators = translators
                        )
                    }
                }
        _translationData.update { it.copy(buttonEnabled= it.buttonEnabled.toMutableList().apply { this[currentPage] = true })}
    }
  }


    fun addTranslationToDatabase(textToTranslate: String, translation: String) {

        if (textToTranslate.isNotEmpty() && translation.isNotEmpty()) {
            val translationEntity = TranslationEntity(
                textToTranslate = textToTranslate, translation = translation
            )
            viewModelScope.launch {
                translationRepository.insertTranslation(translationEntity)
            }
        }
    }

    fun removeTranslationFromDatabase(translation: TranslationEntity) {
        viewModelScope.launch {
            translationRepository.removeTranslation(translation)
        }
    }


}