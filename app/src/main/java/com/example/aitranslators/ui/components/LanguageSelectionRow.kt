package com.example.aitranslators.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aitranslators.R
import com.example.aitranslators.data.TranslationData
import com.example.aitranslators.languages
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LanguageSelectionRow(
    uiData: TranslationData,
    setSourceLang: (language: String) -> Unit,
    setTargetLang: (language: String) -> Unit,
    setDetectingLanguage: (b: Boolean) -> Unit,
    checkSourceLanguageSelected: (b: Boolean) -> Unit,
    checkTargetLanguageSelected: (b: Boolean) -> Unit
) {

    languages = languages.toMutableList().apply {
        if (isNotEmpty()) {
            this[0] = stringResource(R.string.detect_Language)
        }
    }
    var filteredLanguages by remember { mutableStateOf(languages) }
    var sourceLanguageMenuVisible by remember { mutableStateOf(false) }
    var targetLanguageMenuVisible by remember { mutableStateOf(false) }
    var sameLanSelected by remember { mutableStateOf(false) }
    var searchSourceLanguageText by remember { mutableStateOf("") }
    var searchTargetLanguageText by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val snackBarHostState = remember { SnackbarHostState() }



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    sourceLanguageMenuVisible = !sourceLanguageMenuVisible
                    filteredLanguages = languages
                }
        ) {
            Text(
                text = if (uiData.detectingLanguage) stringResource(R.string.detectingLanguage) else uiData.sourceLanguage,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)
            )
            DropdownMenu(
                expanded = sourceLanguageMenuVisible,
                onDismissRequest = { sourceLanguageMenuVisible = false },
                modifier = Modifier
                    .background(Color.White)
                    .border(1.dp, MaterialTheme.colors.primary)
                    .fillMaxSize(0.5f)
            ) {
                OutlinedTextField(
                    value = searchSourceLanguageText,
                    onValueChange = { searchText ->
                        searchSourceLanguageText = searchText
                        filteredLanguages = languages.filter {
                            it.lowercase(Locale.getDefault())
                                .contains(
                                    searchText.trim().lowercase(Locale.getDefault())
                                )
                        }
                    },
                    label = { Text(stringResource(R.string.search_Language)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textStyle = TextStyle(color = Color.Black),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )
                if (filteredLanguages.isNotEmpty()) {
                    filteredLanguages.forEach { language ->
                        DropdownMenuItem(
                            onClick = {
                                sameLanSelected = if (language != uiData.targetLanguage) {
                                    setSourceLang(language)
                                    checkSourceLanguageSelected(true)
                                    searchSourceLanguageText = ""
                                    false
                                } else {
                                    true
                                }
                                if (language == languages[0]) {
                                    setDetectingLanguage(true)
                                } else {
                                    setDetectingLanguage(false)
                                }
                                sourceLanguageMenuVisible = false
                            }
                        ) {
                            Text(text = language)
                        }
                    }
                }
            }
        }
            IconButton(
                onClick = {
                    setSourceLang(uiData.targetLanguage)
                    setTargetLang(uiData.sourceLanguage)
                },
                enabled = uiData.sourceLanguageSelected && !uiData.detectingLanguage && uiData.targetLanguageSelected,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_compare_arrows_24),
                    contentDescription = null,
                    tint = if (uiData.sourceLanguageSelected && !uiData.detectingLanguage && uiData.targetLanguageSelected) MaterialTheme.colors.primary else Color.Gray
                )
            }


        Box(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    targetLanguageMenuVisible = !targetLanguageMenuVisible
                    filteredLanguages = languages.drop(1)
                }
        ) {
            Text(
                text = uiData.targetLanguage,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)
            )
            DropdownMenu(
                expanded = targetLanguageMenuVisible,
                onDismissRequest = { targetLanguageMenuVisible = false },
                modifier = Modifier
                    .background(Color.White)
                    .border(1.dp, MaterialTheme.colors.primary)
                    .fillMaxSize(0.5f)
            ) {
                OutlinedTextField(
                    value = searchTargetLanguageText,
                    onValueChange = { searchText ->
                        searchTargetLanguageText = searchText
                        filteredLanguages = languages.drop(1).filter {
                            it.lowercase(Locale.getDefault())
                                .contains(
                                    searchText.trim().lowercase(Locale.getDefault())
                                )
                        }
                    },
                    label = { Text(stringResource(R.string.search_Language)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    textStyle = TextStyle(color = Color.Black),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )
                if (filteredLanguages.isNotEmpty()) {
                    filteredLanguages.forEach { language ->
                        DropdownMenuItem(
                            onClick = {
                                sameLanSelected = if (language != uiData.sourceLanguage) {
                                    setTargetLang(language)
                                    checkTargetLanguageSelected(true)
                                    searchTargetLanguageText = ""
                                    false
                                } else {
                                    true
                                }
                                targetLanguageMenuVisible = false
                            }
                        ) {
                            Text(text = language)
                        }
                    }
                }
            }
        }
    }
    if (sameLanSelected) {
        Box(
            //    modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            // Snack bar Component
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.fillMaxWidth()
            )
            LaunchedEffect(key1 = true) {
                snackBarHostState.showSnackbar(
                    message = "Source and Target cant be the same!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}