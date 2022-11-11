package com.github.suvrajitdhar.testideaplugin.module.builder

import com.github.suvrajitdhar.testideaplugin.module.DemoModuleType
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel

class DemoModuleBuilder: ModuleBuilder() {

    override fun getModuleType(): ModuleType<*> = DemoModuleType.getInstance()

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
    }

    override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?): ModuleWizardStep =
        DemoModuleWizardStep()

    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean {
        logger<DemoModuleBuilder>().info(">>>>>>>>> SDK Type in use is $sdkType")
        return super.isSuitableSdkType(sdkType)
    }

    override fun setProjectType(module: Module?) {
        super.setProjectType(module)
    }
}