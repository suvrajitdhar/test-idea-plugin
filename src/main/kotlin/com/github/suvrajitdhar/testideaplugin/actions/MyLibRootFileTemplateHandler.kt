package com.github.suvrajitdhar.testideaplugin.actions

import com.intellij.ide.fileTemplates.DefaultCreateFromTemplateHandler
import com.intellij.ide.fileTemplates.FileTemplate
import org.jetbrains.kotlin.psi.psiUtil.quoteIfNeeded

class MyLibRootFileTemplateHandler(): DefaultCreateFromTemplateHandler() {

    override fun prepareProperties(props: MutableMap<String, Any>) {
        val packageName = props[FileTemplate.ATTRIBUTE_PACKAGE_NAME] as? String
        if (!packageName.isNullOrEmpty()) {
            props[FileTemplate.ATTRIBUTE_PACKAGE_NAME] = packageName.split('.').joinToString(".", transform = String::quoteIfNeeded)
        }

        val name = props[FileTemplate.ATTRIBUTE_NAME] as? String
        if (name != null) {
            props[FileTemplate.ATTRIBUTE_NAME] = name.quoteIfNeeded()
        }
    }


}