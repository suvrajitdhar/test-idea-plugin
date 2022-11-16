package com.github.suvrajitdhar.testideaplugin.module.builder

import com.github.suvrajitdhar.testideaplugin.actions.FlutterUtils
import java.util.Arrays.asList
import com.intellij.execution.OutputListener
import com.intellij.execution.process.ProcessListener
import com.intellij.facet.Facet
import com.intellij.facet.FacetManager
import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.projectView.impl.ProjectViewPane
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleWithNameAlreadyExists
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowId
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import javax.swing.ComboBoxEditor
import javax.swing.Icon
import javax.swing.JComponent
import org.apache.commons.lang.StringUtils

class FlutterModuleBuilder : ModuleBuilder() {
    protected var myStep: FlutterModuleWizardStep? = null
   // private var mySettingsFields: FlutterCreateAdditionalSettingsFields? = null
    protected var myProject: Project? = null
//    val name: String = "presentableName"

    // Non-null when creating a module.
    val project: Project?
        get() = myProject // Non-null when creating a module.
//    val presentableName: String = "flutter.module.name"
//    val description: String ="flutter.project.description"

    override fun setupRootModel(model: ModifiableRootModel) {
        doAddContentEntry(model)
        // Add a reference to Dart SDK project library, without committing.
        model.addInvalidLibrary("Dart SDK", "project")
    }

//    protected val flutterSdk: FlutterSdk?
//        protected get() = myStep!!.flutterSdk

    fun commitmodule(project: Project?, model: ModifiableModuleModel?):Module?{
        val basePath: String? = getModuleFileDirectory()
        val baseDir: VirtualFile? = basePath?.let { LocalFileSystem.getInstance().refreshAndFindFileByPath(it) }
        val flutter: Module = project?.let { super.commitModule(it, model) } ?: return null
        if (project != null) {
            showProjectInProjectWindow(project)
        }
        return flutter
    }

    private fun showProjectInProjectWindow( project: Project) {
        ApplicationManager.getApplication().invokeLater {
            DumbService.getInstance(project).runWhenSmart {
                ApplicationManager.getApplication().invokeLater {
                    val view: ProjectView = ProjectView.getInstance(project) ?: return@invokeLater
                    view.changeView(ProjectViewPane.ID)
                }
            }
        }
    }



    /**
     * @see [https://dart.dev/tools/pub/pubspec.name](dart.dev/tools/pub/pubspec.name)
     */
    @Throws(ConfigurationException::class)
    override fun validateModuleName(moduleName: String): Boolean {
        if (!FlutterUtils.isValidPackageName(moduleName)) {
            throw ConfigurationException(
                "Invalid module name: '$moduleName' - must be a valid Dart package name (lower_case_with_underscores)."
            )
        }

        if (!FlutterUtils.isValidDartIdentifier(moduleName)) {
            throw ConfigurationException("Invalid module name: '$moduleName' - must be a valid Dart identifier.")
        }

        return super.validateModuleName(moduleName)
    }


    
//    fun modifySettingsStep( settingsStep: SettingsStep?): ModuleWizardStep? {
//        val wizard: ModuleWizardStep? = settingsStep?.let { super.modifySettingsStep(it) }
//        return wizard
//    }



//    fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable?): ModuleWizardStep {
//        if (!context.isCreatingNewProject()) {
//            myProject = context.getProject()
//        }
//        myStep = FlutterModuleWizardStep(context)
//        Disposer.register(parentDisposable, myStep)
//        return myStep
//    }

    // The builder id is used to distinguish between different builders with the same module type, see
    // com.intellij.ide.projectWizard.ProjectTypeStep for an example.
//    val builderId: String= StringUtil.notNullize(super.getBuilderId()) + "_" + FlutterModuleBuilder::class.java.canonicalName

    override fun getModuleType(): ModuleType<*> {
        return FlutterModuleUtils.flutterModuleType
    }



    companion object {
        private val LOG: Logger = Logger.getInstance(FlutterModuleBuilder::class.java)
        fun addAndroidModule(
             project: Project?,
             model: ModifiableModuleModel?,
             baseDirPath: String?,
             flutterModuleName: String,
            isTopLevel: Boolean
        ) {
            val baseDir: VirtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(baseDirPath!!) ?: return
            val androidFile: VirtualFile = (if (isTopLevel) findAndroidModuleFile(
                baseDir,
                flutterModuleName
            ) else findEmbeddedModuleFile(baseDir, flutterModuleName))
                ?: return
            addAndroidModuleFromFile(project, model, androidFile)
        }

        fun addAndroidModuleFromFile(
             project: Project?,
             model: ModifiableModuleModel?,
             androidFile: VirtualFile
        ) {
            var model: ModifiableModuleModel? = model
            try {
                val toCommit: ModifiableModuleModel?
                if (model == null) {
                    toCommit = project?.let { ModuleManager.getInstance(it).getModifiableModel() }
                    model = toCommit
                } else {
                    toCommit = null
                }
                val newModule: Module = model?.loadModule(androidFile.getPath()) !!
                if (toCommit != null) {
                    ApplicationManager.getApplication().invokeLater {
                        // This check isn't normally needed but can prevent scary problems during testing.
                        // Even if .idea is removed modules may still be created from something in the cache files
                        // if the project had been opened previously.
                        if (ModuleManager.getInstance(project!!).findModuleByName(newModule.name) == null) {
                           // WriteAction.run(toCommit::commit)
                        }
                    }
                }
            } catch (e: ModuleWithNameAlreadyExists) {
                FlutterUtils.warn(LOG, e)
            } catch (e: IOException) {
                FlutterUtils.warn(LOG, e)
            }
        }

        
        private fun findAndroidModuleFile( baseDir: VirtualFile, flutterModuleName: String): VirtualFile? {
            baseDir.refresh(false, false)
            for (name in asList(flutterModuleName + "_android.iml", "android.iml")) {
                val candidate: VirtualFile? = baseDir.findChild(name)
                if (candidate != null && candidate.exists()) {
                    return candidate
                }
            }
            return null
        }

        
        private fun findEmbeddedModuleFile( baseDir: VirtualFile, flutterModuleName: String): VirtualFile? {
            baseDir.refresh(false, false)
            for (name in asList("android", ".android")) {
                val dir: VirtualFile? = baseDir.findChild(name)
                if (dir != null && dir.exists()) {
                    val candidate: VirtualFile? = dir.findChild(flutterModuleName + "_android.iml")
                    if (candidate != null && candidate.exists()) {
                        return candidate
                    }
                }
            }
            return null
        }


        

    }
}