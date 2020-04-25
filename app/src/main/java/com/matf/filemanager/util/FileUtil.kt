package com.matf.filemanager.util

import java.io.File

private val textExtensionRegex = Regex("^(txt|html|css|js|c|h|cpp|hpp|py|java|pl)$", RegexOption.IGNORE_CASE)
private val imageExtensionRegex = Regex("^(jpg|jpeg|png|bmp)$", RegexOption.IGNORE_CASE)
private val videoExtensionRegex = Regex("^(mp4|mkv|webm|flv|ogg|avi|gif|mov)$", RegexOption.IGNORE_CASE)
private val audioExtensionRegex = Regex("^(mp3|wav|3gp|aac|flac)$", RegexOption.IGNORE_CASE)

fun getTypeFromExtension(extension: String): FileTypes {
    return when {
        extension.matches(textExtensionRegex) -> FileTypes.TEXT
        extension.matches(imageExtensionRegex) -> FileTypes.IMAGE
        extension.matches(videoExtensionRegex) -> FileTypes.VIDEO
        extension.matches(audioExtensionRegex) -> FileTypes.AUDIO
        else -> FileTypes.UNKNOWN
    }
}

private val sizeUnits: Array<String> = arrayOf("B", "KB", "MB", "GB")

fun getSizeString(file: File): String {
    var size = file.length().toFloat()
    var unit = 0
    for(i in 1 until sizeUnits.size) {
        if(size<1024)
            break
        size /= 1024
        unit++
    }
    if(unit==0)
        return "${size.toInt()}${sizeUnits[unit]}"
    return "%.2f${sizeUnits[unit]}".format(size)
}