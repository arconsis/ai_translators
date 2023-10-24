package com.example.aitranslators.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun TopBar(title: Int, contentDescription: Int?, icon: ImageVector, clickHandler: () -> Unit) {

    TopAppBar(
        title = { Text(stringResource(id = title)) },
        backgroundColor = MaterialTheme.colors.primary,
        navigationIcon = {
            IconButton(onClick = {
                clickHandler()
            }) {
                Icon(
                    icon,
                    contentDescription = contentDescription?.let { stringResource(it) }
                )
            }
        }
    )
}