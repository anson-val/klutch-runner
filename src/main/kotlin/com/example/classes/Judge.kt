package com.example.classes

import java.io.File

import com.example.interfaces.ICompiler
import com.example.interfaces.IExecutor

class Judge(private val compiler: ICompiler, private val executor: IExecutor) {
    enum class Result { Accepted, Incorrect, TimeLimitExceeded, CompileError, RuntimeError }

    fun judge(submission: SubmissionData): Result {
        val executableFilePath: String?

        try {
            executableFilePath = compiler.compile(submission.code)
        } catch (e:Exception) {
            return Result.CompileError
        }

        if (!File(executableFilePath).exists()) return Result.CompileError

        var isCorrect = true

        for (testCase in submission.testCases) {
            val output = executor.execute(executableFilePath, testCase.input).trim()

            val expectedOutput = testCase.expectedOutput.trim()
            if (output != expectedOutput) {
                isCorrect = false
                break
            }
        }
        executableFilePath.deleteFileAtPath()

        return if (isCorrect) Result.Accepted else Result.Incorrect
    }
}