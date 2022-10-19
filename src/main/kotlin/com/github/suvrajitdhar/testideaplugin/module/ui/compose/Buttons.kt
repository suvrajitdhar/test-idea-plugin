package com.github.suvrajitdhar.testideaplugin.module.ui.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.material.Text
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Buttons() {

    val showBtnDialog = remember { mutableStateOf(false) }
    val showTxtDialog = remember { mutableStateOf(false) }

    if (showBtnDialog.value) {
        showDialog("Button Dialog", "This is the Alert Dialog from the Button with Icon!") {
            showBtnDialog.value = false
        }
    }
    if (showTxtDialog.value) {
        showDialog("Text Button Dialog", "This is the Alert Dialog from the Button with Text!") {
            showTxtDialog.value = false
        }
    }

    Row {

        Button(
            onClick = { showBtnDialog.value = true },
            modifier = Modifier.padding(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "FavoriteBorder",
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(text = "Icon Button")
        }
        TextButton(
            onClick = { showTxtDialog.value = true },
            modifier = Modifier.padding(8.dp),
        ) {
            Text(text = "Text Button")
        }
    }
}

@Composable
private fun showDialog(title: String, body: String, onDismiss : () -> Unit) = AlertDialog(title, body, onDismiss)