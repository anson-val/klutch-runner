package com.example.classes.compilers

import java.nio.file.Files
import java.nio.file.Paths

import com.example.classes.appendPath
import com.example.classes.appendPathUnix
import com.example.classes.overwriteFile
import com.example.interfaces.ICompiler

const val GCC_CODE_FILENAME = "_code.c"
const val GCC_EXECUTABLE_FILENAME = "_code"

class GCCCompiler(private val dockerWorkspace: String): ICompiler {
    init {
        Files.createDirectories(Paths.get(dockerWorkspace))
    }

    override fun compile(code: String): String {
        val codePath = dockerWorkspace.appendPathUnix(GCC_CODE_FILENAME)
        val executablePath = dockerWorkspace.appendPathUnix(GCC_EXECUTABLE_FILENAME)
        val workspacePath = "${System.getProperty("user.dir").appendPath(dockerWorkspace)}:/$dockerWorkspace"
        val codeFile = code.overwriteFile(codePath)

        val gccCompileCommand = listOf("docker", "run", "--rm", "-v", workspacePath, "gcc",
            "gcc", "/$codePath", "-o","/$executablePath")

        val compileProcess = ProcessBuilder(gccCompileCommand)
        compileProcess.redirectError(ProcessBuilder.Redirect.INHERIT)
        compileProcess.start().waitFor()

        codeFile.delete()
        return executablePath
    }
}