package com.example.classes

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

import com.example.interfaces.ISubmissionSource
import com.example.model.Problems
import com.example.model.Submissions
import com.example.model.TestCases

const val SUPPORTED_LANGUAGE = "kotlin"

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
        try {
            RedisConnector.tryConnection()
            if (RedisConnector.db == null) return null

            if (!RedisConnector.db!!.exists(SUPPORTED_LANGUAGE)) return null
            val submissionData = RedisConnector.db!!.lpop(SUPPORTED_LANGUAGE)
            return Json.decodeFromString<SubmissionData>(submissionData)
        } catch(e: Exception) {
            RedisConnector.db?.disconnect()
            RedisConnector.db = null
            println(e)
            return null
        }
    }

    override fun setResult(id: Int, verdict: Judge.Verdict) {
        transaction {
            Submissions.update({
                Submissions.id.eq(id)
            }) {
                it[result] = verdict.status.toString()
                it[executionTimeSeconds] = verdict.executionTimeSeconds
                it[score] = verdict.score
            }
        }
    }
}