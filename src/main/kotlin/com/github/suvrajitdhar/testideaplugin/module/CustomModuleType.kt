package com.github.suvrajitdhar.testideaplugin.module

import com.github.suvrajitdhar.testideaplugin.module.builder.DemoModuleBuilder
import com.intellij.icons.AllIcons.Icons
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import javax.swing.Icon



class CustomModuleType : ModuleType<DemoModuleBuilder>(ID) {
    override fun createModuleBuilder(): DemoModuleBuilder {
        return DemoModuleBuilder()
    }

    override fun getName(): String {
        return "SDK Module Type"
    }

    override fun getDescription(): String {
        return "Example custom module type"
    }

    override fun getNodeIcon( b: Boolean): Icon {
        return  Icons.Ide.NextStep
    }

    override fun createWizardSteps(
        wizardContext: WizardContext,
        moduleBuilder: DemoModuleBuilder,
        modulesProvider: ModulesProvider
    ): Array<ModuleWizardStep> {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider)
    }

    companion object {
        private const val ID = "CUSTOM_MODULE_TYPE"


        fun getInstance(): CustomModuleType {
            return ModuleTypeManager.getInstance().findByID(ID) as CustomModuleType
        }
    }
}