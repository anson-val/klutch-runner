package com.example.interfaces

interface IExecutor {
    data class Result(
        val isTimeOut: Boolean,
        val isCorrupted: Boolean,
        val output: String
    )
    fun execute(executableFileName: String, input: String, timeOutLimitInSeconds: Double): Result
}