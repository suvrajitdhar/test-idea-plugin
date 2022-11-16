package com.github.suvrajitdhar.testideaplugin.module.builder

import com.github.suvrajitdhar.testideaplugin.actions.FlutterUtils
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import javax.swing.JComponent

class FlutterModuleWizardStep( context: WizardContext?) : ModuleWizardStep(), Disposable {
    private val myPeer: FlutterGeneratorPeer

    init {
        //TODO(pq): find a way to listen to wizard cancelation and propagate to peer.
        myPeer = context?.let { FlutterGeneratorPeer(it) }!!

    }

    override fun getComponent(): JComponent {
        return myPeer.component
    }

    override fun updateDataModel() {

    }


    override fun dispose() {}// It's possible that the refresh will fail in which case we just want to trap and ignore.


}