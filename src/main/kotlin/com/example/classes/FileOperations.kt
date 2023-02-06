package com.example.classes

import java.io.File
import java.nio.file.FileSystems

fun String.overwriteFile(filePath: String): File {
    val file = File(filePath)
    if (file.exists()) file.delete()
    file.writeText(this)
    return file
}

fun String.deleteFileAtPath() = File(this).delete()

fun String.appendPath(subDirectory: String): String {
    val separator: String = FileSystems.getDefault().separator
    return if (this.endsWith(separator)) this + subDirectory else "$this$separator$subDirectory"
}

fun String.appendPathUnix(subDirectory: String): String =
    if (this.endsWith('/')) this + subDirectory else "$this/$subDirectory"
