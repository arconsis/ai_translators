package com.example.aitranslators

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ViewModelNavigationGraph(
    translationScreenViewModel: TranslationScreenViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.FirstRoute.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    )
    {
        composable(Route.FirstRoute.route) { TranslationScreen(navController, translationScreenViewModel) }
        composable(Route.SecondRoute.route) { SavedTranslationsScreen(navController, translationScreenViewModel) }

    }
}