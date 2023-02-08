package com.example.classes.compilers

import com.example.classes.ConfigLoader
import com.example.classes.appendPathUnix
import com.example.classes.overwriteFile
import com.example.interfaces.ICompiler
import java.nio.file.Files
import java.nio.file.Paths

class PythonPass(private val dockerWorkspace: String) : ICompiler {
    init {
        Files.createDirectories(Paths.get(dockerWorkspace))
    }

    override fun compile(code: String): String {
        val codeFilePath = dockerWorkspace.appendPathUnix(ConfigLoader.config.runner.python.codeFilename)
        val codeFile = code.overwriteFile(codeFilePath)
        return codeFilePath
    }
}