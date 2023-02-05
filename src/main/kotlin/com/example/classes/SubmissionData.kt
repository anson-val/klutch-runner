package com.example.classes

data class SubmissionData(
    val id: Int,
    val language: String,
    val code: String,
    val testCases: List<TestCaseData>
)
