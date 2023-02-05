package com.example.classes

import com.example.interfaces.ICompiler
import com.example.interfaces.IExecutor

class Judge(private val compiler: ICompiler, private val executor: IExecutor) {
    enum class Result { Accepted, Incorrect }

    fun judge(submission: SubmissionData): Result {
        val executableFilePath = compiler.compile(submission.code)
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