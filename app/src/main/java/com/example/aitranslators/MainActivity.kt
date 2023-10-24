package com.example.aitranslators

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aitranslators.data.Translator
import com.example.aitranslators.data.TranslatorsInfo
import com.example.aitranslators.services.TranslatorService
import com.example.aitranslators.ui.theme.AITranslatorsTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val translators = loadTranslatorsFromJson(applicationContext)
        setContent {
            AITranslatorsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ViewModelAiTranslator(translators)
                }
            }
        }
    }
    @Composable
    fun ViewModelAiTranslator(translators: List<Translator>) {

        val translatorsState = remember { mutableStateOf(translators) }

        val translationScreenViewModel = hiltViewModel<TranslationScreenViewModel>()

        translationScreenViewModel.initializeTranslators(translatorsState.value)

        ViewModelNavigationGraph(translationScreenViewModel = translationScreenViewModel)
    }

    @Suppress("DEPRECATION")
    private fun loadTranslatorsFromJson(context: Context): List<Translator> {
        val inputStream = context.resources.openRawResource(R.raw.translators_info)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<TranslatorsInfo>>() {}.type
        val translators = Gson().fromJson<List<TranslatorsInfo>>(jsonString, listType)
        val translationServices = translators.map {
            val serviceClass = Class.forName(it.className)
            val serviceInstance = serviceClass.newInstance() as TranslatorService
            Translator(serviceInstance, it.name)
        }
        return translationServices
    }
}
