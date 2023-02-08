package com.example.classes.compilers

import com.example.classes.ConfigLoader
import com.example.classes.appendPath
import com.example.classes.appendPathUnix
import com.example.classes.overwriteFile
import com.example.interfaces.ICompiler
import java.nio.file.Files
import java.nio.file.Paths

class KotlinCompiler(private val dockerWorkspace: String) : ICompiler {
    init {
        Files.createDirectories(Paths.get(dockerWorkspace))
    }

    override fun compile(code: String): String {
        val codePath = dockerWorkspace.appendPathUnix(ConfigLoader.config.runner.kotlin.codeFilename)
        val executablePath = dockerWorkspace.appendPathUnix(ConfigLoader.config.runner.kotlin.executableFilename)
        val workspacePath = "${System.getProperty("user.dir").appendPath(dockerWorkspace)}:/$dockerWorkspace"
        val codeFile = code.overwriteFile(codePath)
        val kotlinCompileCommand = listOf(
            "docker", "run", "--rm", "-v", workspacePath, "danysk/kotlin:latest",
            "sh", "-c", "kotlinc /$codePath -include-runtime -d /$executablePath"
        )

        val compileProcess = ProcessBuilder(kotlinCompileCommand)
        compileProcess.redirectError(ProcessBuilder.Redirect.INHERIT)
        compileProcess.start().waitFor()

        codeFile.delete()
        return executablePath
    }
}