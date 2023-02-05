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


        val result = executeAndDetect(executableFilePath, submission.testCases)
        executableFilePath.deleteFileAtPath()

        return result
    }

    private fun executeAndDetect(executableFilePath: String, testCases: List<TestCaseData>): Result {
        var isCorrect = true

        for (testCase in testCases) {
            var result: IExecutor.Result?

            try {
                result = executor.execute(executableFilePath, testCase.input, testCase.timeOutSeconds)
            } catch (e:Exception) {
                return Result.RuntimeError
            }

            when {
                result == null -> return Result.RuntimeError
                result.isTimeOut -> return Result.TimeLimitExceeded
                result.isCorrupted -> return Result.RuntimeError
            }

            val output = result.output.trim()
            val expectedOutput = testCase.expectedOutput.trim()

            if (output != expectedOutput) {
                isCorrect = false
                break
            }
        }

        return if (isCorrect) Result.Accepted else Result.Incorrect
    }
}