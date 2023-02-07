package com.example.classes

import kotlinx.serialization.Serializable

@Serializable
data class SubmissionData(
    val id: Int,
    val language: String,
    val code: String,
    val testCases: List<TestCaseData>
)
