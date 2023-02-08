package com.example

import com.example.classes.ConfigLoader
import com.example.classes.Judge
import com.example.classes.SqlSubmissionSource
import com.example.classes.compilers.*
import com.example.classes.executors.*
import com.example.interfaces.ISubmissionSource

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
        "kotlin" -> Judge(KotlinCompiler(ConfigLoader.config.runner.dockerWorkspace), JVMExecutor(ConfigLoader.config.runner.dockerWorkspace))
        "java" -> Judge(JavaCompiler(ConfigLoader.config.runner.dockerWorkspace), JVMExecutor(ConfigLoader.config.runner.dockerWorkspace))
        "c" -> Judge(GCCCompiler(ConfigLoader.config.runner.dockerWorkspace), GCCExecutor(ConfigLoader.config.runner.dockerWorkspace))
        "python" -> Judge(PythonPass(ConfigLoader.config.runner.dockerWorkspace), PythonExecutor(ConfigLoader.config.runner.dockerWorkspace))
        else -> throw NotImplementedError()
    }