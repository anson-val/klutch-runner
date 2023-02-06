package com.example.classes

import java.io.File

import com.example.interfaces.ICompiler
import com.example.interfaces.IExecutor

class Judge(private val compiler: ICompiler, private val executor: IExecutor) {
    enum class Status { Accepted, WrongAnswer, TimeLimitExceeded, CompileError, RuntimeError }

    data class Verdict (val status: Status, val executionTimeSeconds: Double, val score: Double)

    fun judge(submission: SubmissionData): Verdict {
        val executableFilePath: String?

        try {
            executableFilePath = compiler.compile(submission.code)
        } catch (e:Exception) {
            println(e)
            return Verdict(Status.CompileError, -1.0, 0.0)
        }

        if (!File(executableFilePath).exists()) return Verdict(Status.CompileError, -1.0, 0.0)

        val result = executeAndDetect(executableFilePath, submission.testCases)

        executableFilePath.deleteFileAtPath()

        return result
    }

    private fun executeAndDetect(executableFilePath: String, testCases: List<TestCaseData>): Verdict {
        var isCorrect = true
        var totalScore = 0.0
        var totalExecutionTimeSeconds = 0.0

        for (testCase in testCases) {
            var executionResult: IExecutor.ExecutionResult?

            try {
                executionResult = executor.execute(executableFilePath, testCase.input, testCase.timeOutSeconds)
            } catch (e:Exception) {
                return Verdict(Status.RuntimeError, -1.0, 0.0)
            }

            when {
                executionResult == null -> return Verdict(Status.RuntimeError, -1.0, 0.0)
                executionResult.isTimeOut -> return Verdict(Status.TimeLimitExceeded, -1.0, 0.0)
                executionResult.isCorrupted -> return Verdict(Status.RuntimeError, -1.0, 0.0)
            }

            val output = executionResult.output?.trim() ?: return Verdict(Status.RuntimeError, -1.0, 0.0)
            val expectedOutput = testCase.expectedOutput.trim()
            totalExecutionTimeSeconds += executionResult.executionTimeSeconds

            if (output == expectedOutput) {
                totalScore += testCase.weight
            } else {
                isCorrect = false
            }
        }

        return if (isCorrect) Verdict(Status.Accepted, totalExecutionTimeSeconds, totalScore)
            else Verdict(Status.WrongAnswer, totalExecutionTimeSeconds, totalScore)
    }
}