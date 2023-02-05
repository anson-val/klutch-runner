package com.example.classes

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

import com.example.interfaces.ISubmissionSource
import com.example.model.Problems
import com.example.model.Submissions
import com.example.model.TestCases

object SqlSubmissionSource: ISubmissionSource {
    init {
        val config = HikariConfig("/hikari.properties")
        config.schema = "public"
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Problems, TestCases, Submissions)
        }
    }

    override fun getNextSubmissionData(): SubmissionData? {
        var submissionData: SubmissionData? = null

        transaction {
            val submission = Submissions.select {
                Submissions.result.eq("-")
            }.firstOrNull()

            if (submission != null) {
                val testCases = TestCases.select {
                    TestCases.problemId.eq(submission[Submissions.problemId])
                }.map {
                    TestCaseData(
                        it[TestCases.input],
                        it[TestCases.expectedOutput],
                        it[TestCases.weight],
                        it[TestCases.timeOutSeconds]
                    )
                }

                submissionData = SubmissionData(
                    submission[Submissions.id],
                    submission[Submissions.language],
                    submission[Submissions.code],
                    testCases
                )
            }
        }

        return submissionData
    }

    override fun setResult(id: Int, result: Judger.Result) {
        transaction {
            Submissions.update({
                Submissions.id.eq(id)
            }) {
                it[Submissions.result] = result.toString()
            }
        }
    }
}