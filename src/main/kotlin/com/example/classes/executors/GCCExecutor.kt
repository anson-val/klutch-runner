package com.example.classes.executors

import com.example.classes.*
import com.example.interfaces.IExecutor
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class GCCExecutor(private val dockerWorkspace: String) : IExecutor {
    init {
        Files.createDirectories(Paths.get(dockerWorkspace))
    }

    override fun execute(
        executableFileName: String,
        input: String,
        timeOutLimitInSeconds: Double
    ): IExecutor.ExecutionResult {
        val dockerContainerName = "klutch-gcc-executor-${RandomStringGenerator.generate(24)}"
        val inputFilePath = dockerWorkspace.appendPathUnix(ConfigLoader.config.runner.gcc.inputFilename)
        val outputFilePath = dockerWorkspace.appendPathUnix(ConfigLoader.config.runner.gcc.outputFilename)
        val workspacePath = "${System.getProperty("user.dir").appendPath(dockerWorkspace)}:/$dockerWorkspace"

        val inputFile = input.overwriteFile(inputFilePath)
        val gccExecuteCommand = listOf(
            "docker", "run", "--rm", "--name", dockerContainerName, "-v", workspacePath, "gcc",
            "sh", "-c", "$executableFileName < /$inputFilePath > /$outputFilePath"
        )

        val executeProcess = ProcessBuilder(gccExecuteCommand)
        executeProcess.redirectError(ProcessBuilder.Redirect.INHERIT)

        val process = executeProcess.start()
        val startTimeMillis = System.currentTimeMillis().toDouble()
        val isTimeOut = !process.waitFor((timeOutLimitInSeconds * 1000).toLong(), TimeUnit.MILLISECONDS)

        if (isTimeOut) {
            ProcessBuilder("docker", "kill", dockerContainerName).start().waitFor()
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