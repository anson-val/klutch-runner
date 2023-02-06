package com.example.classes.executors

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import com.example.classes.overwriteFile
import com.example.classes.appendPath
import com.example.classes.appendPathUnix
import com.example.interfaces.IExecutor

const val JVM_INPUT_FILENAME = "input.txt"
const val JVM_OUTPUT_FILENAME = "output.txt"
const val DOCKER_CONTAINER_NAME = "jvm-docker"

class JVMExecutor(private val dockerWorkspace: String): IExecutor {
    init {
        Files.createDirectories(Paths.get(dockerWorkspace))
    }

    override fun execute(executableFileName: String, input: String, timeOutLimitInSeconds: Double): IExecutor.ExecutionResult {
        val inputFilePath = dockerWorkspace.appendPathUnix(JVM_INPUT_FILENAME)
        val outputFilePath = dockerWorkspace.appendPathUnix(JVM_OUTPUT_FILENAME)
        val workspacePath = "${System.getProperty("user.dir").appendPath(dockerWorkspace)}:/$dockerWorkspace"

        val inputFile = input.overwriteFile(inputFilePath)
        val jvmExecuteCommand = listOf("docker", "run", "--rm", "--name", DOCKER_CONTAINER_NAME, "-v", workspacePath, "zenika/kotlin",
            "sh", "-c", "java -jar $executableFileName < /$inputFilePath > /$outputFilePath")

        val executeProcess = ProcessBuilder(jvmExecuteCommand)
        executeProcess.redirectError(ProcessBuilder.Redirect.INHERIT)

        val process = executeProcess.start()
        val startTimeMillis = System.currentTimeMillis().toDouble()
        val isTimeOut = !process.waitFor((timeOutLimitInSeconds * 1000).toLong(), TimeUnit.MILLISECONDS)

        if (isTimeOut) {
            ProcessBuilder("docker", "kill", DOCKER_CONTAINER_NAME).start().waitFor()
        }

        process.destroy()
        process.waitFor()

        val executionTimeSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000

        val isCorrupted = process.exitValue() != 0

        val outputFile = File(outputFilePath)
        var output: String? = null

        if (outputFile.exists()) {
            output = outputFile.readText()
        }

        inputFile.delete()
        outputFile.delete()

        return IExecutor.ExecutionResult(isTimeOut, isCorrupted, output, executionTimeSeconds)
    }
}