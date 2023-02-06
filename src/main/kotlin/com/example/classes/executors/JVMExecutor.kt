package com.example.classes.executors

import java.io.File
import java.util.concurrent.TimeUnit

import com.example.classes.overwriteFile
import com.example.interfaces.IExecutor

const val JVM_INPUT_PATH = "input.txt"
const val JVM_OUTPUT_PATH = "output.txt"

class JVMExecutor: IExecutor {
    override fun execute(executableFileName: String, input: String, timeOutLimitInSeconds: Double): IExecutor.ExecutionResult {
        val inputFile = input.overwriteFile(JVM_INPUT_PATH)
        val outputFile = File(JVM_OUTPUT_PATH)
        val jvmExecuteCommand = listOf("java", "-jar", executableFileName)

        val executeProcess = ProcessBuilder(jvmExecuteCommand)

        executeProcess.redirectInput(inputFile)
        executeProcess.redirectOutput(outputFile)

        val process = executeProcess.start()
        val startTimeMillis = System.currentTimeMillis().toDouble()
        val isTimeOut = !process.waitFor((timeOutLimitInSeconds * 1000).toLong(), TimeUnit.MILLISECONDS)

        process.destroy()
        process.waitFor()

        val executionTimeSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000

        val isCorrupted = process.exitValue() != 0

        val output = outputFile.readText()
        inputFile.delete()
        outputFile.delete()

        return IExecutor.ExecutionResult(isTimeOut, isCorrupted, output, executionTimeSeconds)
    }
}