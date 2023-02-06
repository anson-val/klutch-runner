package com.example

import com.example.classes.Judge
import com.example.classes.SqlSubmissionSource
import com.example.classes.compilers.KotlinCompiler
import com.example.classes.executors.JVMExecutor
import com.example.interfaces.ISubmissionSource

fun main() {
    val submissionSource: ISubmissionSource = SqlSubmissionSource

    while(true) {
        var submission = submissionSource.getNextSubmissionData()
        while (submission != null) {
            val judge = Judge(KotlinCompiler(), JVMExecutor())
            val result = judge.judge(submission)
            submissionSource.setResult(submission.id, result)
            submission = submissionSource.getNextSubmissionData()
        }

        Thread.sleep(5000)
    }
}