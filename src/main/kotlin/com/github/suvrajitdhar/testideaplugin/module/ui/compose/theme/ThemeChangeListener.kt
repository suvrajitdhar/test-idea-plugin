package com.github.suvrajitdhar.testideaplugin.module.ui.compose.theme

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener

internal class ThemeChangeListener(val updateColors: () -> Unit) : LafManagerListener {
    override fun lookAndFeelChanged(source: LafManager) {
        updateColors()
    }
}
