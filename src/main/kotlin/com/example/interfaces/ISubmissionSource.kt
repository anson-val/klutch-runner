package com.example.interfaces

import com.example.classes.Judge
import com.example.classes.SubmissionData

interface ISubmissionSource {
    fun getNextSubmissionData(): SubmissionData?
    fun setResult(id: Int, result: Judge.Result)
}