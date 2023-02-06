package com.example.interfaces

interface IExecutor {
    data class ExecutionResult(
        val isTimeOut: Boolean,
        val isCorrupted: Boolean,
        val output: String?,
        val executionTimeSeconds: Double
    )
    fun execute(executableFileName: String, input: String, timeOutLimitInSeconds: Double): ExecutionResult
}