package com.github.suvrajitdhar.testideaplugin.module.builder

import com.github.suvrajitdhar.testideaplugin.module.DemoModuleType
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel

class DemoModuleBuilder: ModuleBuilder() {

    override fun getModuleType(): ModuleType<*> = DemoModuleType.getInstance()

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
    }

    override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?): ModuleWizardStep =
        DemoModuleWizardStep()
}