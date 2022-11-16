package com.github.suvrajitdhar.testideaplugin.actions

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.intellij.icons.AllIcons
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.fileTemplates.actions.CreateFromTemplateActionBase
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import com.intellij.psi.PsiDirectory
import org.apache.commons.text.CaseUtils
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*

open class DemoAction : AnAction() {
    private lateinit var defaultProperties: Properties

    override fun actionPerformed(e: AnActionEvent) {

        val dataContext = e.dataContext
        val view = LangDataKeys.IDE_VIEW.getData(dataContext) ?: return
        val project: Project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
        val dir = view.orChooseDirectory ?: return

//        Messages.showChooseDialog(
//            project,
//            "Choose a project type !!",
//            "Hello",
//            AllIcons.Icons.Ide.NextStepInverted,
//            arrayOf("Standard", "Embedded"),
//            "Standard"
//        )

        val (moduleName, isHiltIncluded) = Messages.showInputDialogWithCheckBox(
            "Please enter the Module name",
            "Input Module Name",
            "Should include Hilt as the Dependency Injection Framework?",
            true,
            true,
            AllIcons.Ide.Gift,
            null,
            object : InputValidator {
                override fun checkInput(inputString: String?) = !inputString.isNullOrEmpty()
                override fun canClose(inputString: String?) = true
            }
        )

        Messages.showInputDialog(
            project,
            "Please enter the package name",
            "Input Package Name",
            AllIcons.Ide.Gift,
            null,
            object : InputValidator {
                override fun checkInput(inputString: String?) = !inputString.isNullOrEmpty()
                override fun canClose(inputString: String?) = true
            }
        )?.let { pkg ->
            val namespace = Messages.showInputDialog(
                project,
                "Please enter the Module namespace. If left blank it will take the module name instead.",
                "Input Module Namespace",
                AllIcons.Ide.Gift,
                moduleName,
                null
            ) ?: moduleName

            val name = CaseUtils.toCamelCase(moduleName, true, '-', '_')
           // val name = moduleName
            defaultProperties = Properties().apply {
                setProperty(FileTemplate.ATTRIBUTE_PACKAGE_NAME, pkg)
                setProperty("MyLib", name)
                setProperty("myLib", moduleName)
                setProperty("namespace", namespace)
            }

            WriteCommandAction.runWriteCommandAction(project) {
                createModule(project, moduleName, dir, pkg)
            }
        }
    }

    fun createModule(
        project: Project,
        moduleName: @NlsSafe String = "testlib",
        dir: PsiDirectory,
        pkg: @NlsSafe String
    ) {

        val absolutePath = "${project.basePath}/$moduleName"
            defaultProperties = Properties().apply {
                setProperty(FileTemplate.ATTRIBUTE_PACKAGE_NAME, pkg)
                setProperty("MyLib", moduleName)
                setProperty("myLib", moduleName)
                setProperty("namespace", moduleName)

        }

        val props = Properties(defaultProperties).apply {
            setProperty(FileTemplate.ATTRIBUTE_FILE_PATH, absolutePath)
        }
        val moduleDir = dir.createSubdirectory(moduleName)

        val template = FileTemplateManager.getInstance(project).getTemplate("MyLibModule.gradle")
        val psiElement = FileTemplateUtil.createFromTemplate(
            /* template = */ template,
            /* fileName = */ "build.gradle",
            /* props = */ props,
            /* directory = */ moduleDir
        )

        moduleDir.createSubdirectory("libs")
        moduleDir.createSubdirectory("src").apply {
            createSubdirectory("androidTest").apply {
//                    createPkgStructure(pkg, createSubdirectory("java"))
                createPkgStructure(pkg, createSubdirectory("kotlin"))
            }
            createSubdirectory("test").apply {
//                    createPkgStructure(pkg, createSubdirectory("java"))
                createPkgStructure(pkg, createSubdirectory("kotlin"))
            }
            createSubdirectory("main").apply {
//                    createPkgStructure(pkg, createSubdirectory("java"))
                val childDir = createPkgStructure(pkg, createSubdirectory("kotlin"))
                createResDirStructure(this, moduleName, pkg)
                createAndroidManifestFromTemplate(project, moduleName, this, pkg)
                createActivityCodeFromTemplate(project, moduleName, childDir, pkg)
            }
        }

        val psiFile = psiElement.containingFile
//            val pointer = SmartPointerManager.getInstance(project).createSmartPsiElementPointer<PsiFile>(psiFile)
        val liveTemplateDefaultValues = emptyMap<String, String>()

        psiFile.virtualFile?.let { virtualFile ->
            if (template.isLiveTemplateEnabled) {
                CreateFromTemplateActionBase.startLiveTemplate(psiFile, liveTemplateDefaultValues)
            } else {
                FileEditorManager.getInstance(project).openFile(virtualFile, true)
            }
        }

        dir.files.filter {
            it.name in arrayOf("settings.gradle", "settings.gradle.kt")
        }.forEach {
            if (it.isWritable) {
                val fileName = "${project.basePath}/${it.name}"
                val contentToWrite = "\r\ninclude ':${moduleName}'"
                Files.write(
                    Paths.get(fileName),
                    contentToWrite.toByteArray(Charset.defaultCharset()),
                    StandardOpenOption.APPEND
                )
            }
        }



        ProjectSyncInvoker.DefaultProjectSyncInvoker().syncProject(project)
        project.save()


    }

    private fun createActivityCodeFromTemplate(
        project: Project,
        moduleName: @NlsSafe String,
        dir: PsiDirectory,
        pkg: @NlsSafe String
    ) {
        val template = FileTemplateManager.getInstance(project).getTemplate("MyLibActivity.kt")
        val name = defaultProperties.getProperty("MyLib")
        FileTemplateUtil.createFromTemplate(
            /* template = */ template,
            /* fileName = */ "${name}Activity",
            /* props = */ defaultProperties,
            /* directory = */ dir
        )
    }

    private fun createActivityLayoutFromTemplate(
        project: Project,
        moduleName: @NlsSafe String,
        dir: PsiDirectory,
        pkg: @NlsSafe String
    ) {
        val template = FileTemplateManager.getInstance(project).getTemplate("activity_mylib.xml")
        FileTemplateUtil.createFromTemplate(
            /* template = */ template,
            /* fileName = */ "activity_$moduleName",
            /* props = */ defaultProperties,
            /* directory = */ dir
        )
    }

    private fun createAndroidManifestFromTemplate(
        project: Project,
        moduleName: String,
        dir: PsiDirectory,
        pkg: String
    ) {
        val template = FileTemplateManager.getInstance(project).getTemplate("AndroidManifest.xml")
        FileTemplateUtil.createFromTemplate(
            /* template = */ template,
            /* fileName = */ "AndroidManifest",
            /* props = */ defaultProperties,
            /* directory = */ dir
        )
    }

    private fun createResDirStructure(psiDirectory: PsiDirectory, moduleName: @NlsSafe String, pkg: @NlsSafe String) {
        psiDirectory.createSubdirectory("res").apply {
            createSubdirectory("drawable")
            createSubdirectory("layout").apply {
                createActivityLayoutFromTemplate(project, moduleName, this, pkg)
            }
            createSubdirectory("values").apply {
//                TODO("Create strings, colors etc. files from Template")
            }
        }
    }

    private fun createPkgStructure(pkg: @NlsSafe String, dir: PsiDirectory): PsiDirectory {
        var subDir = dir
        pkg.split('.').forEach {
            subDir = subDir.createSubdirectory(it)
        }
        return subDir
    }
}

