package com.example.classes.compilers

import com.example.classes.ConfigLoader
import com.example.classes.appendPath
import com.example.classes.appendPathUnix
import com.example.classes.overwriteFile
import com.example.interfaces.ICompiler
import java.nio.file.Files
import java.nio.file.Paths

class GCCCompiler(private val dockerWorkspace: String) : ICompiler {
    init {
        Files.createDirectories(Paths.get(dockerWorkspace))
    }

    override fun compile(code: String): String {
        val codePath = dockerWorkspace.appendPathUnix(ConfigLoader.config.runner.gcc.codeFilename)
        val executablePath = dockerWorkspace.appendPathUnix(ConfigLoader.config.runner.gcc.executableFilename)
        val workspacePath = "${System.getProperty("user.dir").appendPath(dockerWorkspace)}:/$dockerWorkspace"
        val codeFile = code.overwriteFile(codePath)

        val gccCompileCommand = listOf(
            "docker", "run", "--rm", "-v", workspacePath, "gcc",
            "gcc", "/$codePath", "-o", "/$executablePath"
        )

        val compileProcess = ProcessBuilder(gccCompileCommand)
        compileProcess.redirectError(ProcessBuilder.Redirect.INHERIT)
        compileProcess.start().waitFor()

        codeFile.delete()
        return executablePath
    }
}