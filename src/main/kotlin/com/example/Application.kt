package com.example

import com.example.classes.Judge
import com.example.classes.SqlSubmissionSource
import com.example.classes.compilers.GCCCompiler
import com.example.classes.compilers.JavaCompiler
import com.example.classes.compilers.KotlinCompiler
import com.example.classes.executors.GCCExecutor
import com.example.classes.executors.JVMExecutor
import com.example.interfaces.ISubmissionSource

const val DOCKER_WORKSPACE = "klutch-docker-runner"

fun main() {
    val submissionSource: ISubmissionSource = SqlSubmissionSource

    while(true) {
        var submission = submissionSource.getNextSubmissionData()
        while (submission != null) {
            val judge = getJudge(submission.language)
            val verdict = judge.judge(submission)
            submissionSource.setResult(submission.id, verdict)
            submission = submissionSource.getNextSubmissionData()
        }

        Thread.sleep(5000)
    }
}

fun getJudge(language: String): Judge =
    when(language) {
        "kotlin" -> Judge(KotlinCompiler(DOCKER_WORKSPACE), JVMExecutor(DOCKER_WORKSPACE))
        "java" -> Judge(JavaCompiler(DOCKER_WORKSPACE), JVMExecutor(DOCKER_WORKSPACE))
        "c" -> Judge(GCCCompiler(DOCKER_WORKSPACE), GCCExecutor(DOCKER_WORKSPACE))
        else -> throw NotImplementedError()
    }