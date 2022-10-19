package com.github.suvrajitdhar.testideaplugin.module.builder

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import com.github.suvrajitdhar.testideaplugin.module.ui.compose.Buttons
import com.github.suvrajitdhar.testideaplugin.module.ui.compose.TextInputs
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import javax.swing.JComponent

class DemoModuleWizardStep: ModuleWizardStep() {

    override fun getComponent(): JComponent =
        ComposePanel().apply {
            setBounds(0, 0, 800, 600)
            setContent {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Row {
                            Column(
                                modifier = Modifier.fillMaxHeight().weight(1f)
                            ) {
                                TextInputs()
                                Buttons()
                            }
                            Box(
                                modifier = Modifier.fillMaxHeight().weight(1f)
                            )
                    }
                }
            }
        }

    override fun updateDataModel() {
        TODO("Not yet implemented")
    }
}