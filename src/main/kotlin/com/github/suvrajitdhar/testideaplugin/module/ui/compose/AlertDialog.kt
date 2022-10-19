package com.github.suvrajitdhar.testideaplugin.module.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlertDialog(title: String, body: String, onDismiss: () -> Unit) {
    MaterialTheme {
        Column {
            AlertDialog(
                onDismissRequest = onDismiss
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                ,
                title = { Text(text = title) },
                text = { Text(body) },
                confirmButton = {
                    Button(onClick = onDismiss) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}