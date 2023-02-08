package com.example.classes.compilers

import com.example.classes.ConfigLoader
import com.example.classes.appendPath
import com.example.classes.appendPathUnix
import com.example.classes.overwriteFile
import com.example.interfaces.ICompiler
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class JavaCompiler(private val dockerWorkspace: String) : ICompiler {
    init {
        Files.createDirectories(Paths.get(dockerWorkspace))
    }

    override fun compile(code: String): String {
        val codePath = dockerWorkspace.appendPathUnix(ConfigLoader.config.runner.java.codeFilename)
        val classPath = dockerWorkspace.appendPath(ConfigLoader.config.runner.java.classFilename)
        val manifestFilePath = dockerWorkspace.appendPath(ConfigLoader.config.runner.java.manifestFilename)
        val workspacePath = "${System.getProperty("user.dir").appendPath(dockerWorkspace)}:/$dockerWorkspace"
        val codeFile = code.overwriteFile(codePath)
        val manifestFile = "Main-Class: Main\n\n\n".overwriteFile(manifestFilePath)

        val javaCompileCommand = listOf(
            "docker", "run", "--rm", "-v", workspacePath, "eclipse-temurin:latest", "sh", "-c",
            "cd /$dockerWorkspace; javac ${ConfigLoader.config.runner.java.codeFilename}; jar -cvfm ${ConfigLoader.config.runner.java.executableFilename} ${ConfigLoader.config.runner.java.manifestFilename} ${ConfigLoader.config.runner.java.classFilename}"
        )

        val compileProcess = ProcessBuilder(javaCompileCommand)
        compileProcess.redirectError(ProcessBuilder.Redirect.INHERIT)
        compileProcess.start().waitFor()

        codeFile.delete()
        manifestFile.delete()
        File(classPath).delete()
        return dockerWorkspace.appendPathUnix(ConfigLoader.config.runner.java.executableFilename)
    }
}