package com.example.classes.compilers

import com.example.classes.overwriteFile
import com.example.interfaces.ICompiler

const val KOTLIN_CODE_PATH = "_code.kt"
const val KOTLIN_EXECUTABLE_PATH = "_code.jar"

class KotlinCompiler: ICompiler {
    override fun compile(code: String): String {
        val codeFile = code.overwriteFile(KOTLIN_CODE_PATH)
        val kotlinCompileCommand = listOf("cmd.exe", "/C", "kotlinc", KOTLIN_CODE_PATH, "-include-runtime", "-d", KOTLIN_EXECUTABLE_PATH)

        val compileProcess = ProcessBuilder(kotlinCompileCommand)
        compileProcess.start().waitFor()

        codeFile.delete()
        return KOTLIN_EXECUTABLE_PATH
    }
}