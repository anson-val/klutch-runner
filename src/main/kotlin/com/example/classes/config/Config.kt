package com.example.classes.config

data class Config(
    val sqlDatabase: SqlDatabaseConfig,
    val cacheDatabase: CacheDatabaseConfig,
    val runner: RunnerConfig,
)
