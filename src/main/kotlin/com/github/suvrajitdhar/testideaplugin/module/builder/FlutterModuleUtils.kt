/*
 * Copyright 2017 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package com.github.suvrajitdhar.testideaplugin.module.builder

import com.intellij.openapi.module.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager


object FlutterModuleUtils {
    const val DEPRECATED_FLUTTER_MODULE_TYPE_ID = "WEB_MODULE"

    /**
     * This provides the [ModuleType] ID for Flutter modules to be assigned by the [io.flutter.module.FlutterModuleBuilder] and
     * elsewhere in the Flutter plugin.
     *
     *
     * For Flutter module detection however, [ModuleType]s should not be used to determine Flutterness.
     */
    val moduleTypeIDForFlutter: String
        get() = "JAVA_MODULE"
    val flutterModuleType: ModuleType<*>
        get() = ModuleTypeManager.getInstance().findByID(moduleTypeIDForFlutter)



    fun getModules(project: Project): Array<Module> {
        // A disposed project has no modules.
        return if (project.isDisposed) Module.EMPTY_ARRAY else ModuleManager.getInstance(project).getModules()
    }


    /**
     * Set the passed module to the module type used by Flutter, defined by [.getModuleTypeIDForFlutter].
     */
    fun setFlutterModuleType(module: Module) {
        module.setModuleType(moduleTypeIDForFlutter)
    }

    fun setFlutterModuleAndReload(module: Module, project: Project) {

        ProjectManager.getInstance().reloadProject(project)
    }

}