package com.example.classes.config

data class RunnerConfig(
    val supportedLanguages: List<String>,
    val dockerWorkspace: String,
    val kotlin: KotlinConfig,
    val java: JavaConfig,
    val gcc: GCCConfig,
    val python: PythonConfig,
    val jvm: JVMConfig
)
