package com.github.suvrajitdhar.testideaplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.ide.impl.NewProjectUtil
import com.intellij.ide.projectWizard.NewProjectWizard

class NewCustomModuleAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val wizard = NewProjectWizard(e.project, ModulesProvider.EMPTY_MODULES_PROVIDER, null)
        NewProjectUtil.createNewProject(wizard)
    }
}