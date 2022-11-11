package com.github.suvrajitdhar.testideaplugin.actions

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
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiDirectory
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*

open class DemoAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {

        val dataContext = e.dataContext
        val view = LangDataKeys.IDE_VIEW.getData(dataContext) ?: return
        val project: Project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
        val dir = view.orChooseDirectory ?: return

        Messages.showEditableChooseDialog(
            "Choose a project type !!",
            "Hello",
            AllIcons.Icons.Ide.NextStepInverted,
            arrayOf("Standard", "Embedded"),
            "Standard",
            object : InputValidator {
                override fun checkInput(input: String?): Boolean {
                    logger<DemoAction>().info("Logging $input")
                    return true
                }

                override fun canClose(p0: String?): Boolean {
                    return true
                }

            }
        )?.let {
            Messages.showInputDialog(
                project,
                "Please enter the Module name",
                "Input Module Name",
                AllIcons.Ide.Gift
            )?.let { moduleName ->
                val namespace = Messages.showInputDialog(
                    project,
                    "Please enter the Module namespace. If left blank it will take the module name instead.",
                    "Input Module Namespace",
                    AllIcons.Ide.Gift
                ) ?: moduleName

                Messages.showInputDialog(
                    project,
                    "Please enter the package",
                    "Input Package",
                    AllIcons.Ide.Gift
                )?.let { pkg ->

                    val isIncludeHilt = Messages.showCheckboxOkCancelDialog(
                        "Please select if you want to use Hilt to inject and resolve dependencies.",
                        "Hilt Dependency",
                        "Include Hilt ?",
                        true,
                        0,
                        0,
                        AllIcons.Ide.Gift
                    )

                    WriteCommandAction.runWriteCommandAction(project) {
                        createModule(project, moduleName, dir, pkg)
                    }
                }
            }
        }
    }

    private fun createModule(project: Project, moduleName: @NlsSafe String, dir: PsiDirectory, pkg: @NlsSafe String) {
        try {
            val absolutePath = "${project.basePath}/$moduleName"

            val props = Properties().apply {
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
            val srcDir = moduleDir.createSubdirectory("src").apply {
                createSubdirectory("androidTest").apply {
                    createPkgStructure(pkg, createSubdirectory("java"))
                    createPkgStructure(pkg, createSubdirectory("kotlin"))
                }
                createSubdirectory("test").apply {
                    createPkgStructure(pkg, createSubdirectory("java"))
                    createPkgStructure(pkg, createSubdirectory("kotlin"))
                }
                createSubdirectory("main").apply {
                    createPkgStructure(pkg, createSubdirectory("java"))
                    createPkgStructure(pkg, createSubdirectory("kotlin"))
                    createResDirStructure(this)
                    TODO("Create AndroidManifest file from Template")
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

        } catch (t: Throwable) {
            println(t)
        }
    }

    private fun createResDirStructure(psiDirectory: PsiDirectory) {
        psiDirectory.createSubdirectory("res").apply {
            createSubdirectory("drawable")
            createSubdirectory("layout").apply {
                TODO("Create layout files from Template")
            }
            createSubdirectory("values").apply {
                TODO("Create strings, colors etc. files from Template")
            }
        }
    }

    private fun createPkgStructure(pkg: @NlsSafe String, dir: PsiDirectory) {
        var subDir = dir
        pkg.split('.').forEach {
            subDir = subDir.createSubdirectory(it)
        }
    }
}

