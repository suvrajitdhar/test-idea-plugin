package com.github.suvrajitdhar.testideaplugin.ui

import com.github.suvrajitdhar.testideaplugin.actions.DemoAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.impl.file.PsiDirectoryFactory
import java.awt.Container
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class MyFrame(val project: Project?) {
    // Components of the Form
    private val c: Container
    val name: JLabel
    val moduleName: JTextField
    val mno: JLabel
    val packageName: JTextField
    val gender: JLabel
    val namespace: JTextField
    val create: JButton = JButton("Create")


    val contentPane = JPanel()

    // constructor, to initialize the components
    // with default values.
    init {

        c = contentPane
        c.layout = null

        name = JLabel("Module Name")
        name.font = Font("Arial", Font.PLAIN, 20)
        name.setSize(100, 40)
        name.setLocation(100, 100)
        c.add(name)
        moduleName = JTextField("testlib")
        moduleName.font = Font("Arial", Font.PLAIN, 15)
        moduleName.setSize(150, 40)
        moduleName.setLocation(200, 100)
        c.add(moduleName)
        mno = JLabel("package")
        mno.font = Font("Arial", Font.PLAIN, 20)
        mno.setSize(100, 40)
        mno.setLocation(100, 150)
        c.add(mno)
        packageName = JTextField("com.test")
        packageName.font = Font("Arial", Font.PLAIN, 15)
        packageName.setSize(150, 40)
        packageName.setLocation(200, 150)
        c.add(packageName)
        gender = JLabel("Namespace")
        gender.font = Font("Arial", Font.PLAIN, 20)
        gender.setSize(100, 40)
        gender.setLocation(100, 200)
        c.add(gender)
        namespace = JTextField()
        namespace.font = Font("Arial", Font.PLAIN, 15)
        namespace.setSize(150, 40)
        namespace.setLocation(200, 200)
        c.add(namespace)

        create.setSize(200, 40)
        create.setLocation(150, 250)
        c.add(create)




        create.addActionListener {
//            WriteCommandAction.runWriteCommandAction(project) {
//                val dir = PsiDirectoryFactory.getInstance(project).createDirectory(project!!.baseDir)
//
//                DemoAction().createModule(project, moduleName.text, dir, packageName.text)
//            }

        }
    }




}