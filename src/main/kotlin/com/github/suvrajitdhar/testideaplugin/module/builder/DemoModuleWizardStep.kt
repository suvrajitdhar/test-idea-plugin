package com.github.suvrajitdhar.testideaplugin.module.builder


import com.github.suvrajitdhar.testideaplugin.actions.DemoAction
import com.github.suvrajitdhar.testideaplugin.ui.MyFrame
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.impl.file.PsiDirectoryFactory
import javax.swing.JComponent


class DemoModuleWizardStep(val project: Project?) : ModuleWizardStep(){


    var f = MyFrame(project)


    override fun getComponent(): JComponent = f.contentPane

    override fun updateDataModel() {

    }

    override fun onWizardFinished() {
        super.onWizardFinished()
        WriteCommandAction.runWriteCommandAction(project) {
            val dir = PsiDirectoryFactory.getInstance(project).createDirectory(project!!.baseDir)

            DemoAction().createModule(project, f.moduleName.text, dir, f.packageName.text)
        }


    }

}