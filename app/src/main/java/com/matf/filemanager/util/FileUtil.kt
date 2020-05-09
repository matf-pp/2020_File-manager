package com.matf.filemanager.util

import java.io.File

private val textExtensionRegex = Regex("^(txt|css|js|c|h|cpp|hpp|py|java|pl)$", RegexOption.IGNORE_CASE)
private val imageExtensionRegex = Regex("^(jpg|jpeg|png|bmp)$", RegexOption.IGNORE_CASE)
private val videoExtensionRegex = Regex("^(mp4|mkv|webm|flv|ogg|avi|gif|mov)$", RegexOption.IGNORE_CASE)
private val audioExtensionRegex = Regex("^(mp3|wav|3gp|aac|flac)$", RegexOption.IGNORE_CASE)
private val zipExtensionRegex = Regex("^(zip|rar|7z|iso)$", RegexOption.IGNORE_CASE)
private val pdfExtensionRegex = Regex("^pdf$", RegexOption.IGNORE_CASE)
private val htmlExtensionRegex = Regex("^(html|htm)$", RegexOption.IGNORE_CASE)

fun getTypeFromExtension(extension: String): FileTypes {
    return when {
        extension.matches(textExtensionRegex) -> FileTypes.TEXT
        extension.matches(imageExtensionRegex) -> FileTypes.IMAGE
        extension.matches(videoExtensionRegex) -> FileTypes.VIDEO
        extension.matches(audioExtensionRegex) -> FileTypes.AUDIO
        extension.matches(zipExtensionRegex) -> FileTypes.ZIP
        extension.matches(pdfExtensionRegex) -> FileTypes.PDF
        extension.matches(htmlExtensionRegex) -> FileTypes.HTML
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