package com.github.suvrajitdhar.testideaplugin.codegen

import freemarker.template.Configuration
import freemarker.template.Version
import java.io.File


object FreeMakerTest {

    fun runTest(moduleName: String) {
        val cfg = Configuration(Version("2.3.31")).run {
            setClassForTemplateLoading(FreeMakerTest::class.java, "/views")
            defaultEncoding = "UTF-8"
            this
        }

        val template = cfg.getTemplate("test.ftlh")

        val templateData: MutableMap<String, Any> = HashMap()
        templateData["msg"] = "Today is a beautiful day"

        File("./$moduleName").mkdir()

//        StringWriter().use { out ->
//            template.process(templateData, out)
//            println(out.buffer.toString())
//            out.flush()
//        }

    }

}