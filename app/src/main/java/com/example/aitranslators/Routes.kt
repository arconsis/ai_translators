package com.example.aitranslators

sealed class Route(val route: String) {
    object FirstRoute : Route("first")
    object SecondRoute : Route("second")
}