package com.example.classes.executors

import java.io.File

import com.example.classes.overwriteFile
import com.example.interfaces.IExecutor

const val JVM_INPUT_PATH = "input.txt"
const val JVM_OUTPUT_PATH = "output.txt"

class JVMExecutor: IExecutor {
    override fun execute(executableFileName: String, input: String): String {
        val inputFile = input.overwriteFile(JVM_INPUT_PATH)
        val outputFile = File(JVM_OUTPUT_PATH)
        val jvmExecuteCommand = listOf("java", "-jar", executableFileName)

        val executeProcess = ProcessBuilder(jvmExecuteCommand)

        executeProcess.redirectInput(inputFile)
        executeProcess.redirectOutput(outputFile)
        executeProcess.start().waitFor()

        val output = outputFile.readText()

        inputFile.delete()
        outputFile.delete()
        return output
    }
}