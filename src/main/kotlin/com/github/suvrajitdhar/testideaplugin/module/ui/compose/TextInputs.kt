package com.github.suvrajitdhar.testideaplugin.module.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp


@Composable
fun TextInputs() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        var moduleName by remember { mutableStateOf(TextFieldValue("")) }
        var moduleNamespace by remember { mutableStateOf(TextFieldValue("")) }

        TextField(
            value = moduleName,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            textStyle = TextStyle(fontFamily = FontFamily.SansSerif),
            label = { Text("Enter Module Name:") },
            placeholder = { Text("The Module Name") },
            onValueChange = { newValue ->
                moduleName = newValue
                moduleNamespace = newValue
            }
        )

        OutlinedTextField(
            value = moduleNamespace,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            label = { Text(text = "Enter Module Namespace:") },
            placeholder = { Text(text = "If left blank the module name will be the namespace.") },
            textStyle = TextStyle(fontFamily = FontFamily.SansSerif),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = {
                moduleNamespace = it
            }
        )
    }
}