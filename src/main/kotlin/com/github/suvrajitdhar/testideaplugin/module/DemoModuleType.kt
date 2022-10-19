package com.github.suvrajitdhar.testideaplugin.module

import com.github.suvrajitdhar.testideaplugin.module.builder.DemoModuleBuilder
import com.intellij.icons.AllIcons.Icons
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import javax.swing.Icon


private const val ID = "DEMO_MODULE_TYPE"

class DemoModuleType: ModuleType<DemoModuleBuilder>(ID) {

    companion object {
        fun getInstance(): ModuleType<*> = (ModuleTypeManager.getInstance().findByID(ID) as DemoModuleType)
    }
    
    override fun createModuleBuilder(): DemoModuleBuilder = DemoModuleBuilder()

    override fun getName(): String = "Demo Module Type"

    override fun getDescription(): String = "Example custom module type"

    override fun getNodeIcon(isOpened: Boolean): Icon = Icons.Ide.NextStep

    override fun createWizardSteps(
        wizardContext: WizardContext,
        moduleBuilder: DemoModuleBuilder,
        modulesProvider: ModulesProvider
    ): Array<ModuleWizardStep> = super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider)

}