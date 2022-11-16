package com.github.suvrajitdhar.testideaplugin.actions

import com.intellij.execution.util.ExecUtil.execAndGetOutput
import com.intellij.openapi.progress.ProcessCanceledException
import java.lang.Runnable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.SystemIndependent
import com.intellij.openapi.vfs.LocalFileSystem
import com.github.suvrajitdhar.testideaplugin.actions.FlutterUtils
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.github.suvrajitdhar.testideaplugin.actions.FlutterUtils.FlutterPubspecInfo
import com.google.common.base.Charsets
import com.intellij.execution.ExecutionException
import com.intellij.ide.impl.ProjectUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.module.Module
import java.io.IOException
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleSourceOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderEntry
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.PlatformUtils
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import org.yaml.snakeyaml.resolver.Resolver
import java.io.InputStreamReader
import java.lang.Exception
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern

object FlutterUtils {
    private val VALID_ID = Pattern.compile("[_a-zA-Z$][_a-zA-Z0-9$]*")

    // Note the possessive quantifiers -- greedy quantifiers are too slow on long expressions (#1421).
    private val VALID_PACKAGE = Pattern.compile("^([a-z]++([_]?[a-z0-9]+)*)++$")

    /**
     * This method exists for compatibility with older IntelliJ API versions.
     *
     *
     * `Application.invokeAndWait(Runnable)` doesn't exist pre 2016.3.
     */
    @Throws(ProcessCanceledException::class)
    fun invokeAndWait(runnable: Runnable) {
        ApplicationManager.getApplication().invokeAndWait(
            runnable,
            ModalityState.defaultModalityState()
        )
    }

    val isAndroidStudio: Boolean
        get() = StringUtil.equals(PlatformUtils.getPlatformPrefix(), "AndroidStudio")

    /**
     * Write a warning message to the IntelliJ log.
     *
     *
     * This is separate from LOG.warn() to allow us to decorate the behavior.
     */
    fun warn(logger: Logger, t: Throwable) {
        logger.warn(t)
    }

    /**
     * Write a warning message to the IntelliJ log.
     *
     *
     * This is separate from LOG.warn() to allow us to decorate the behavior.
     */
    fun warn(logger: Logger, message: String?) {
        logger.warn(message)
    }

    /**
     * Write a warning message to the IntelliJ log.
     *
     *
     * This is separate from LOG.warn() to allow us to decorate the behavior.
     */
    fun warn(logger: Logger, message: String?, t: Throwable) {
        logger.warn(message, t)
    }

    private val baselineVersion: Int
        private get() {
            val appInfo = ApplicationInfo.getInstance()
            return appInfo?.build?.baselineVersion ?: -1
        }

    fun disableGradleProjectMigrationNotification(project: Project) {
        val showMigrateToGradlePopup = "show.migrate.to.gradle.popup"
        val properties = PropertiesComponent.getInstance(project)
        if (properties.getValue(showMigrateToGradlePopup) == null) {
            properties.setValue(showMigrateToGradlePopup, "false")
        }
    }

    fun exists(file: VirtualFile?): Boolean {
        return file != null && file.exists()
    }

    /**
     * Test if the given element is contained in a module with a pub root that declares a flutter dependency.
     */
    val isIntegrationTestingMode: Boolean
        get() = System.getProperty("idea.required.plugins.id", "") == "io.flutter.tests.gui.flutter-gui-tests"

    fun getRealVirtualFile(psiFile: PsiFile?): VirtualFile? {
        return psiFile?.originalFile?.virtualFile
    }

    fun getProjectRoot(project: Project): VirtualFile? {
        assert(!project.isDefault)
        val path = project.basePath!!
        val file = LocalFileSystem.getInstance().findFileByPath(path)
        return Objects.requireNonNull(file)
    }

    /**
     * Checks whether a given string is a valid Dart identifier.
     *
     *
     * See: https://dart.dev/guides/language/spec
     *
     * @param id the string to check
     * @return true if a valid identifer, false otherwise.
     */
    fun isValidDartIdentifier(id: String): Boolean {
        return VALID_ID.matcher(id).matches()
    }

    /**
     * Checks whether a given string is a valid Dart package name.
     *
     *
     *
     * @param name the string to check
     * @return true if a valid package name, false otherwise.
     * @see [https://dart.dev/tools/pub/pubspec.name](dart.dev/tools/pub/pubspec.name)
     */
    fun isValidPackageName(name: String): Boolean {
        return VALID_PACKAGE.matcher(name).matches()
    }

    /**
     * Checks whether a given filename is an Xcode metadata file, suitable for opening externally.
     *
     * @param name the name to check
     * @return true if an xcode project filename
     */
    fun isXcodeFileName(name: String): Boolean {
        return isXcodeProjectFileName(name) || isXcodeWorkspaceFileName(name)
    }

    /**
     * Checks whether a given file name is an Xcode project filename.
     *
     * @param name the name to check
     * @return true if an xcode project filename
     */
    fun isXcodeProjectFileName(name: String): Boolean {
        return name.endsWith(".xcodeproj")
    }

    /**
     * Checks whether a given name is an Xcode workspace filename.
     *
     * @param name the name to check
     * @return true if an xcode workspace filename
     */
    fun isXcodeWorkspaceFileName(name: String): Boolean {
        return name.endsWith(".xcworkspace")
    }

    /**
     * Checks whether the given commandline executes cleanly.
     *
     * @param cmd the command
     * @return true if the command runs cleanly
     */
    fun runsCleanly(cmd: GeneralCommandLine): Boolean {
        return try {
            execAndGetOutput(cmd).exitCode == 0
        } catch (e: ExecutionException) {
            false
        }
    }

    val pluginId: PluginId
        get() {
            val pluginId = PluginId.findId("io.flutter", "")!!
            return pluginId
        }

    /**
     * Given some plugin id, this method returns the [IdeaPluginDescriptor], or null if the plugin is not installed.
     */
    fun getPluginDescriptor(pluginId: String): IdeaPluginDescriptor? {
        for (descriptor in PluginManagerCore.getPlugins()) {
            if (descriptor.pluginId.idString == pluginId) {
                return descriptor
            }
        }
        return null
    }

    /**
     * Returns a structured object with information about the Flutter properties of the given
     * pubspec file.
     */
    fun getFlutterPubspecInfo(pubspec: VirtualFile): FlutterPubspecInfo {
        // It uses Flutter if it contains 'dependencies: flutter'.
        // It's a plugin if it contains 'flutter: plugin'.
        val info = FlutterPubspecInfo(pubspec.modificationStamp)
        try {
            val yamlMap = readPubspecFileToMap(pubspec)
            if (yamlMap != null) {
                // Special case the 'flutter' package itself - this allows us to run their unit tests from IntelliJ.
                val packageName = yamlMap["name"]
                if ("flutter" == packageName) {
                    info.flutter = true
                }

                // Check the dependencies.
                val dependencies = yamlMap["dependencies"]
                if (dependencies is Map<*, *>) {
                    // We use `|=` for assigning to 'flutter' below as it might have been assigned to true above.
                    info.flutter = info.flutter or dependencies.containsKey("flutter")
                }

                // Check for a Flutter plugin.
                val flutterEntry = yamlMap["flutter"]
                if (flutterEntry is Map<*, *>) {
                    info.isFlutterPlugin = flutterEntry.containsKey("plugin")
                }
            }
        } catch (e: IOException) {
            // ignore
        }
        return info
    }

    /**
     * Returns true if passed pubspec declares a flutter dependency.
     */
    fun declaresFlutter(pubspec: VirtualFile): Boolean {
        return getFlutterPubspecInfo(pubspec).declaresFlutter()
    }

    /**
     * Returns true if the passed pubspec indicates that it is a Flutter plugin.
     */
    fun isFlutterPlugin(pubspec: VirtualFile): Boolean {
        return getFlutterPubspecInfo(pubspec).isFlutterPlugin
    }

    /**
     * Return the project located at the `path` or containing it.
     *
     * @param path The path to a project or one of its files
     * @return The Project located at the path
     */
    fun findProject(path: String): Project? {
        for (project in ProjectManager.getInstance().openProjects) {
            if (ProjectUtil.isSameProject(Paths.get(path), project)) {
                return project
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun readPubspecFileToMap(pubspec: VirtualFile): Map<String, Any>? {
        val contents = String(pubspec.contentsToByteArray(true /* cache contents */))
        return loadPubspecInfo(contents)
    }

    private fun loadPubspecInfo(yamlContents: String): Map<String, Any>? {
        val yaml = Yaml(SafeConstructor(), Representer(), DumperOptions(), object : Resolver() {
            override fun addImplicitResolvers() {
                addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO")
                addImplicitResolver(Tag.NULL, NULL, "~nN\u0000")
                addImplicitResolver(Tag.NULL, EMPTY, null)
                addImplicitResolver(Tag("tag:yaml.org,2002:value"), VALUE, "=")
                addImplicitResolver(Tag.MERGE, MERGE, "<")
            }
        })
        return try {
            yaml.load(yamlContents)
        } catch (e: Exception) {
            null
        }
    }

    fun isAndroidxProject(project: Project): Boolean {
        val basePath = project.basePath!!
        val projectDir = LocalFileSystem.getInstance().findFileByPath(basePath)!!
        var androidDir = getFlutterManagedAndroidDir(projectDir)
        if (androidDir == null) {
            androidDir = getAndroidProjectDir(projectDir)
            if (androidDir == null) {
                return false
            }
        }
        val propFile = androidDir.findChild("gradle.properties") ?: return false
        val properties = Properties()
        try {
            properties.load(InputStreamReader(propFile.inputStream, Charsets.UTF_8))
        } catch (ex: IOException) {
            return false
        }
        val value = properties.getProperty("android.useAndroidX")
        return if (value != null) {
            java.lang.Boolean.parseBoolean(value)
        } else false
    }

    private fun getAndroidProjectDir(dir: VirtualFile?): VirtualFile? {
        return if (dir!!.findChild("app") == null) null else dir
    }

    private fun getFlutterManagedAndroidDir(dir: VirtualFile?): VirtualFile? {
        val meta = dir!!.findChild(".metadata")
        if (meta != null) {
            try {
                val properties = Properties()
                properties.load(InputStreamReader(meta.inputStream, Charsets.UTF_8))
                val value = properties.getProperty("project_type") ?: return null
                when (value) {
                    "app" -> return dir.findChild("android")
                    "module" -> return dir.findChild(".android")
                    "package" -> return null
                    "plugin" -> return dir.findFileByRelativePath("example/android")
                }
            } catch (e: IOException) {
                // fall thru
            }
        }
        var android: VirtualFile?
        android = dir.findChild(".android")
        if (android != null) {
            return android
        }
        android = dir.findChild("android")
        if (android != null) {
            return android
        }
        android = dir.findFileByRelativePath("example/android")
        return android
    }

    fun findModuleNamed(project: Project, name: String): Module? {
        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {
            if (module.name == name) {
                return module
            }
        }
        return null
    }

    fun locateModuleRoot(module: Module): VirtualFile? {
        val entry = findModuleSourceEntry(module) ?: return null
        val roots = entry.rootModel.contentRoots
        return if (roots.size == 0) null else roots[0]
    }

    private fun findModuleSourceEntry(module: Module): ModuleSourceOrderEntry? {
        val moduleRootManager = ModuleRootManager.getInstance(module)
        val orderEntries = moduleRootManager.orderEntries
        for (entry in orderEntries) {
            if (entry is ModuleSourceOrderEntry) {
                return entry
            }
        }
        return null
    }

    class FlutterPubspecInfo internal constructor(val modificationStamp: Long) {
        var flutter = false
        var isFlutterPlugin = false

        fun declaresFlutter(): Boolean {
            return flutter
        }
    }
}