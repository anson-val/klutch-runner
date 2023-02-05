package com.example.classes

import java.io.File

import com.example.interfaces.ICompiler
import com.example.interfaces.IExecutor

class Judge(private val compiler: ICompiler, private val executor: IExecutor) {
    enum class Status { Accepted, Incorrect, TimeLimitExceeded, CompileError, RuntimeError }

    data class Result (val status: Status, val executionTimeSeconds: Double, val score: Double)

    fun judge(submission: SubmissionData): Result {
        val executableFilePath: String?

        try {
            executableFilePath = compiler.compile(submission.code)
        } catch (e:Exception) {
            return Result(Status.CompileError, -1.0, 0.0)
        }

        if (!File(executableFilePath).exists()) return Result(Status.CompileError, -1.0, 0.0)

        val result = executeAndDetect(executableFilePath, submission.testCases)

        executableFilePath.deleteFileAtPath()

        return result
    }

    private fun executeAndDetect(executableFilePath: String, testCases: List<TestCaseData>): Result {
        var isCorrect = true
        var totalScore = 0.0
        var totalExecutionTimeSeconds = 0.0

        for (testCase in testCases) {
            var executionResult: IExecutor.ExecutionResult?

            try {
                executionResult = executor.execute(executableFilePath, testCase.input, testCase.timeOutSeconds)
            } catch (e:Exception) {
                return Result(Status.RuntimeError, -1.0, 0.0)
            }

            when {
                executionResult == null -> return Result(Status.RuntimeError, -1.0, 0.0)
                executionResult.isTimeOut -> return Result(Status.TimeLimitExceeded, -1.0, 0.0)
                executionResult.isCorrupted -> return Result(Status.RuntimeError, -1.0, 0.0)
            }

            val output = executionResult.output.trim()
            val expectedOutput = testCase.expectedOutput.trim()
            totalExecutionTimeSeconds += executionResult.executionTimeSeconds

            if (output == expectedOutput) {
                totalScore += testCase.weight
            } else {
                isCorrect = false
            }
        }

        return if (isCorrect) Result(Status.Accepted, totalExecutionTimeSeconds, totalScore)
            else Result(Status.Incorrect, totalExecutionTimeSeconds, totalScore)
    }
}