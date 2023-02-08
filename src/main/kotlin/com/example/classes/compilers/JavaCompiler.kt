package com.example.classes.compilers

import com.example.classes.appendPath
import com.example.classes.appendPathUnix
import com.example.classes.overwriteFile
import com.example.interfaces.ICompiler
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

const val JAVA_CODE_FILENAME = "Main.java"
const val JAVA_CLASS_FILENAME = "Main.class"
const val JAVA_EXECUTABLE_FILENAME = "_code.jar"
const val JAVA_MANIFEST_FILENAME = "MANIFEST.MF"

class JavaCompiler(private val dockerWorkspace: String) : ICompiler {
    init {
        Files.createDirectories(Paths.get(dockerWorkspace))
    }

    override fun compile(code: String): String {
        val codePath = dockerWorkspace.appendPathUnix(JAVA_CODE_FILENAME)
        val classPath = dockerWorkspace.appendPath(JAVA_CLASS_FILENAME)
        val manifestFilePath = dockerWorkspace.appendPath(JAVA_MANIFEST_FILENAME)
        val workspacePath = "${System.getProperty("user.dir").appendPath(dockerWorkspace)}:/$dockerWorkspace"
        val codeFile = code.overwriteFile(codePath)
        val manifestFile = "Main-Class: Main\n\n\n".overwriteFile(manifestFilePath)

        val javaCompileCommand = listOf(
            "docker", "run", "--rm", "-v", workspacePath, "eclipse-temurin:latest", "sh", "-c",
            "cd /$dockerWorkspace; javac $JAVA_CODE_FILENAME; jar -cvfm $JAVA_EXECUTABLE_FILENAME $JAVA_MANIFEST_FILENAME $JAVA_CLASS_FILENAME"
        )

        val compileProcess = ProcessBuilder(javaCompileCommand)
        compileProcess.redirectError(ProcessBuilder.Redirect.INHERIT)
        compileProcess.start().waitFor()

        codeFile.delete()
        manifestFile.delete()
        File(classPath).delete()
        return dockerWorkspace.appendPathUnix(JAVA_EXECUTABLE_FILENAME)
    }
}