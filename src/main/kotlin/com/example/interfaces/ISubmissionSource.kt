package com.example.interfaces

import com.example.classes.Judger
import com.example.classes.SubmissionData

interface ISubmissionSource {
    fun getNextSubmissionData(): SubmissionData?
    fun setResult(id: Int, result: Judger.Result)
}