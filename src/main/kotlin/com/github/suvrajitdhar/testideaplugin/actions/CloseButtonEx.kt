package com.github.suvrajitdhar.testideaplugin.actions

import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.components.JBComponent
import javax.swing.*

class CloseButtonEx {
    val contentPane = JPanel()

    init {

        createUI("title")
    }


    private fun createUI(title: String) {


        val closeBtn = JLabel("Library name")
        val libraryName = JBTextField("lib1")
        val packageLabel = JLabel("Package name(com.example)")
        val packagename = JBTextField("com.test")


        createLayout(closeBtn,libraryName)
        createLayout(packageLabel,packagename)
    }


    private fun createLayout(vararg arg: JComponent){

        val gl = GroupLayout(contentPane)
        contentPane.layout = gl

        gl.autoCreateContainerGaps = true

        gl.setHorizontalGroup(

            gl.createSequentialGroup()
                .addComponent(arg[0])
        )

        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addComponent(arg[0])
        )

    }
}


