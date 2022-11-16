package com.github.suvrajitdhar.testideaplugin.module.builder

import com.intellij.icons.AllIcons
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.ui.ComboboxWithBrowseButton
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBLabel
import com.intellij.xml.util.XmlStringUtil
import org.apache.commons.lang.StringUtils
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.text.JTextComponent

class FlutterGeneratorPeer(context: WizardContext) {
    private val myContext: WizardContext
    private val myMainPanel: JPanel? = null
    private val myVersionContent: JBLabel? = null
    private val errorIcon: JLabel? = null
    private val errorText: JTextPane? = null
    private val errorPane: JScrollPane? = null
//    private val myHelpForm: SettingsHelpForm? = null

    init {
        myContext = context
        errorIcon?.setText("")
        errorIcon?.setIcon(AllIcons.Actions.Lightning)
        Messages.installHyperlinkSupport(errorText)

        // Hide pending real content.
        myVersionContent?.setVisible(false)

        // TODO(messick) Remove this field.
        init()
    }

    private fun init() {

        errorIcon?.setVisible(false)
        errorPane?.setVisible(false)
    }


    fun apply() {}
    val component: JComponent
        get() = myMainPanel!!

}