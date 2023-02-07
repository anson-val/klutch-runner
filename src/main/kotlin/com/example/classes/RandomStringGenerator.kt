package com.example.classes

object RandomStringGenerator {
    private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generate(length: Int) =
        (0 until length).map { charPool.random() }.joinToString("")
}