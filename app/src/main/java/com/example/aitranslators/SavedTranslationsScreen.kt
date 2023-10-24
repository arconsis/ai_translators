package com.example.aitranslators

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.aitranslators.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedTranslationsScreen(navController: NavController, viewModel: TranslationScreenViewModel) {

    val listState = rememberLazyListState()
    val savedTranslationsInDB by viewModel.savedTranslationsInDB.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopBar(
            title = R.string.saved,
            contentDescription = null,
            icon = Icons.Default.ArrowBack,
            clickHandler = {
                navController.popBackStack()
            })
    }, content = { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .weight(6f)
                    .padding(10.dp)
                    .border(1.dp, MaterialTheme.colors.primary, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(savedTranslationsInDB.translations.size) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(10.dp)
                                        .weight(4f)
                                ) {
                                    SelectionContainer {
                                        Text(
                                            text = savedTranslationsInDB.translations[it].textToTranslate + " -> " + savedTranslationsInDB.translations[it].translation,
                                            modifier = Modifier
                                                .padding(10.dp)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(10.dp)
                                        .weight(1f)
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (savedTranslationsInDB.translations[it] == savedTranslationsInDB.translations.last()) {
                                                viewModel.setAllStarIconsFalse()
                                            }
                                            viewModel.removeTranslationFromDatabase(
                                                savedTranslationsInDB.translations[it]
                                            )
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete"
                                        )
                                    }
                                }
                            }

                            Divider(
                                color = Color.Gray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
            }
        }
    })
}