package com.github.suvrajitdhar.testideaplugin.module.builder

import com.android.tools.idea.npw.ideahost.IdeaWizardDelegate
import com.github.suvrajitdhar.testideaplugin.module.CustomModuleType
import com.google.common.base.Preconditions
import com.intellij.ide.util.newProjectWizard.WizardDelegate
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.AbstractWizard
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableRootModel

class DemoModuleBuilder : ModuleBuilder() {


    override fun setupRootModel(model: ModifiableRootModel) {

    }
    override fun getModuleType(): CustomModuleType {
        return CustomModuleType.getInstance()
    }

    override fun getCustomOptionsStep(ctx: WizardContext, parentDisposable: Disposable): ModuleWizardStep? {


        return DemoModuleWizardStep(ctx.project )
    }

    override fun createModule(moduleModel: ModifiableModuleModel): Module {
        return super.createModule(moduleModel)

    }

}