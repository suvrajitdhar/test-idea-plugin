package com.github.suvrajitdhar.testideaplugin.recipes

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider
import com.github.suvrajitdhar.testideaplugin.recipes.customActivity.CustomActivityTemplate

class CustomWizardTemplateProvider: WizardTemplateProvider() {
    override fun getTemplates(): List<Template> {
        return listOf(CustomActivityTemplate)
    }
}