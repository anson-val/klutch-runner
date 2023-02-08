package com.example.classes

import com.example.interfaces.ISubmissionSource
import com.example.model.Problems
import com.example.model.Submissions
import com.example.model.TestCases
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object SqlSubmissionSource : ISubmissionSource {
    private val supportedLanguage = ConfigLoader.config.runner.supportedLanguages

    init {
        val sqlDriver =
            when (ConfigLoader.config.sqlDatabase.type) {
                "postgresql" -> "org.postgresql.Driver"
                "mysql" -> "com.mysql.cj.jdbc.Driver"
                "oracle" -> "oracle.jdbc.OracleDriver"
                "sqlite" -> "org.sqlite.JDBC"
                "h2" -> "org.h2.Driver"
                "sqlserver" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver"
                else -> throw Exception()
            }

        Database.connect(
            ConfigLoader.config.sqlDatabase.host,
            driver = sqlDriver,
            user = ConfigLoader.config.sqlDatabase.user,
            password = ConfigLoader.config.sqlDatabase.pwd
        )

        transaction {
            SchemaUtils.create(Problems, TestCases, Submissions)
        }
    }

    override fun getNextSubmissionData(): SubmissionData? {
        try {
            RedisConnector.tryConnection()
            if (RedisConnector.db == null) return null

            for (language in supportedLanguage) {
                if (!RedisConnector.db!!.exists(language)) continue

                val submissionData = RedisConnector.db!!.lpop(language)
                return Json.decodeFromString<SubmissionData>(submissionData)
            }
        } catch (e: Exception) {
            RedisConnector.db?.disconnect()
            RedisConnector.db = null
            println(e)
            return null
        }

        return null
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