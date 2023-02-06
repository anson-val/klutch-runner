package com.example.classes

import java.io.File

fun String.overwriteFile(filename: String): File {
    val file = File(filename)
    if (file.exists()) file.delete()
    file.writeText(this)

    return file
}

fun String.deleteFileAtPath() = File(this).delete()