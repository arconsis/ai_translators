package com.example.aitranslators.deeplDB

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TranslationRepository @Inject constructor  (
    private val translationDao: TranslationDao
) {
      fun insertTranslation(translation: TranslationEntity) {
        translationDao.insert(translation)
    }

    val allTranslations: Flow<List<TranslationEntity>> = translationDao.getAllTranslations()

      fun removeTranslation(translation: TranslationEntity) {
        translationDao.delete(translation)
    }
}