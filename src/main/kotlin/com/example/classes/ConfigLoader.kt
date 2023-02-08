package com.example.classes

import com.example.classes.config.Config
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource

object ConfigLoader {
    val config = ConfigLoaderBuilder.default()
        .addResourceSource("/config.json")
        .build()
        .loadConfigOrThrow<Config>()
}