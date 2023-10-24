package com.example.aitranslators


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.aitranslators.checkInternet.ConnectivityStatus
import com.example.aitranslators.ui.components.BottomBar
import com.example.aitranslators.ui.components.LanguageSelectionRow
import com.example.aitranslators.ui.components.TopBar
import com.example.aitranslators.ui.components.TranslatorView
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun TranslationScreen(
    navController: NavHostController,
    viewModel: TranslationScreenViewModel = hiltViewModel()
) {


    val context = LocalContext.current
    val savedTranslationsInDB by viewModel.savedTranslationsInDB.collectAsStateWithLifecycle()
    val translationData by viewModel.translationData.collectAsStateWithLifecycle()

    if (!translationData.viewModelInitialized) {
        LaunchedEffect(viewModel) {
            viewModel.initializeUiStrings(
                sourceLan = context.getString(R.string.source_Language),
                targetLan = context.getString(R.string.target_Language),
                errorText = context.getString(R.string.errorText)
            )
        }
    }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val textState: MutableState<String> = remember { mutableStateOf("") }

    val pagerState = rememberPagerState(pageCount = {
        translationData.translators.size
    })
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                title = R.string.app_name,
                contentDescription = R.string.saved,
                icon = Icons.Default.Star,
                clickHandler = {
                    navController.navigate(Route.SecondRoute.route)
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                ConnectivityStatus()
                LanguageSelectionRow(
                    uiData = translationData,
                    setSourceLang = {
                        viewModel.setSourceLang(it)
                    },
                    setTargetLang = {
                        viewModel.setTargetLang(it)
                    },

                    setDetectingLanguage = {
                        viewModel.setDetectingLanguage(it)
                    },
                    checkSourceLanguageSelected = {
                        viewModel.isSourceLanguageSelected(it)
                    },
                    checkTargetLanguageSelected = {
                        viewModel.isTargetLanguageSelected(it)
                    }
                )

                OutlinedTextField(
                    value = translationData.textToTranslate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .border(1.dp, MaterialTheme.colors.primary),
                    onValueChange = { newValue ->
                        textState.value = newValue
                        viewModel.updateTextToTranslate(textState.value)
                    },
                    isError = translationData.noResult,
                    //    label = { Text("Type something to translate") },
                    placeholder = { Text(stringResource(R.string.typeSomething)) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.checkInput(translationData.textToTranslate)
                            viewModel.translate(pagerState.currentPage)
                            keyboardController?.hide()
                        }
                    )
                )

                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .border(1.dp, MaterialTheme.colors.primary, RoundedCornerShape(16.dp))
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TranslatorView(
                            translator = translationData.translators[it],
                            buttonEnabled = translationData.buttonEnabled[it],
                            detectingLanguage = translationData.detectingLanguage,
                            searchInputEmpty = translationData.searchInputEmpty,
                            isStarClicked = translationData.isStarClickedList[it],
                            error = translationData.translators[it].error,
                            translateText = {
                                viewModel.updateNoResultState(false)
                                viewModel.checkInput(translationData.textToTranslate)
                                viewModel.checkReadyToTranslate()
                                viewModel.translate(it)
                            },
                            saveOrRemoveInDb = {
                                val currentTranslation =
                                    translationData.translators[it].translatedText ?: ""

                                val translationEntityToDelete =
                                    savedTranslationsInDB.translations.find { del ->
                                        del.translation == currentTranslation
                                    }
                                if (translationData.isStarClickedList[it] && translationEntityToDelete != null) {
                                    viewModel.removeTranslationFromDatabase(
                                        translationEntityToDelete
                                    )
                                    viewModel.updateStarIconList(currentPage = it, b = false)
                                } else {
                                    viewModel.addTranslationToDatabase(
                                        textToTranslate = translationData.textToTranslate,
                                        translation = translationData.translators[it].translatedText
                                            ?: ""
                                    )
                                    viewModel.updateStarIconList(currentPage = it, b = true)
                                }
                            }
                        )
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                // Snack bar Component
                val snackBarMessage = if (translationData.searchInputEmpty) {
                    stringResource(R.string.searchTermEmpty)
                } else if (!translationData.readyToTranslate) {
                    stringResource(R.string.notReadyToTranslate)
                } else {
                    null
                }

                snackBarMessage?.let {
                    SnackbarHost(
                        hostState = snackBarHostState,
                        modifier = Modifier.fillMaxWidth()
                    )
                    LaunchedEffect(key1 = snackBarMessage) {
                        snackBarHostState.showSnackbar(
                            message = snackBarMessage,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomBar(
                currentPage = pagerState.currentPage,
                pageCount = pagerState.pageCount,
                canScrollForward = pagerState.canScrollForward,
                canScrollBackward = pagerState.canScrollBackward,
                onGoBack = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                onGoForward = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            )

        }
    )

}


