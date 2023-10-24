package com.example.aitranslators.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aitranslators.R
import com.example.aitranslators.data.Translator

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TranslatorView(
    translator: Translator,
    buttonEnabled: Boolean,
    detectingLanguage: Boolean,
    searchInputEmpty: Boolean,
    error: Boolean,
    isStarClicked: Boolean,
    translateText: () -> Unit,
    saveOrRemoveInDb: () -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = translator.name,
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                Button(
                    enabled = buttonEnabled,
                    onClick = {
                        translateText()
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.translate),
                        color = Color.White
                    )
                }
            }
        }

        Divider()

        Spacer(modifier = Modifier.height(30.dp))

        if (!translator.translatedText.isNullOrEmpty()) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(modifier = Modifier.weight(4f)) {
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            SelectionContainer {
                                Text(
                                    text = "${translator.translatedText} ${if (!translator.detectedLanguage.isNullOrEmpty() && detectingLanguage) "\n${stringResource(R.string.detectedLanguage)} " + translator.detectedLanguage else ""}",
                                    modifier = Modifier.padding(10.dp),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                    if (!searchInputEmpty && !error) {
                        Box(modifier = Modifier.weight(1f)) {
                            IconButton(
                                onClick = {
                                    saveOrRemoveInDb()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (isStarClicked) Color.Magenta else Color.DarkGray
                                )
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(0.8f)) {
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${stringResource(R.string.responseTime)}\n${if (translator.responseTime > 0) "${stringResource(R.string.translation)} ${translator.responseTime}" else ""} ${if (detectingLanguage && translator.responseTimeLanguageDetection > 0) "\n${stringResource(R.string.languageDetection)} ${translator.responseTimeLanguageDetection}" else ""}",
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}