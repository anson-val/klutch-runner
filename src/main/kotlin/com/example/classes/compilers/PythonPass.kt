package com.example.classes.compilers

import java.nio.file.Files
import java.nio.file.Paths

import com.example.classes.overwriteFile
import com.example.classes.appendPathUnix
import com.example.interfaces.ICompiler

const val PYTHON_CODE_FILENAME = "_code.py"

class PythonPass(private val dockerWorkspace: String): ICompiler {
    init {
        Files.createDirectories(Paths.get(dockerWorkspace))
    }

    override fun compile(code: String): String {
        val codeFilePath = dockerWorkspace.appendPathUnix(PYTHON_CODE_FILENAME)
        val codeFile = code.overwriteFile(codeFilePath)
        return codeFilePath
    }
}