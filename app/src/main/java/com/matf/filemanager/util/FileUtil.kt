package com.matf.filemanager.util

import com.matf.filemanager.R
import java.io.File

private val textExtensionRegex = Regex("^(txt|css|js|c|h|cpp|hpp|py|java|pl)$", RegexOption.IGNORE_CASE)
private val imageExtensionRegex = Regex("^(jpg|jpeg|png|bmp)$", RegexOption.IGNORE_CASE)
private val videoExtensionRegex = Regex("^(mp4|mkv|webm|flv|ogg|avi|gif|mov)$", RegexOption.IGNORE_CASE)
private val audioExtensionRegex = Regex("^(mp3|wav|3gp|aac|flac)$", RegexOption.IGNORE_CASE)
private val zipExtensionRegex = Regex("^(zip|rar|7z|iso)$", RegexOption.IGNORE_CASE)
private val pdfExtensionRegex = Regex("^pdf$", RegexOption.IGNORE_CASE)
private val htmlExtensionRegex = Regex("^(html|htm)$", RegexOption.IGNORE_CASE)

/**
 * Vraca tip fajla za prosledjenu ekstenziju
 *
 * @param extension Ekstenzija na osnovu koje trazimo tip
 * @return Tip fajla koji ima prosledjnu ekstenziju
 */
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

/**
 * Trazi odgovarajucu ikonicu za prosledjen fajl
 *
 * @param file Fajl ciju ikonicu trazimo
 * @return Id resursa koji sadrzi ikonicu
 */
fun getIconForFile(file: File): Int {
    if(file.isDirectory) {
        return if(file.listFiles().isEmpty())
            R.drawable.folder_empty
        else
            R.drawable.folder_filled
    } else {
        return when(getTypeFromExtension(file.extension)) {
            FileTypes.IMAGE -> R.drawable.file_image
            FileTypes.AUDIO -> R.drawable.audio1
            FileTypes.VIDEO -> R.drawable.file_media
            FileTypes.HTML -> R.drawable.html
            FileTypes.PDF -> R.drawable.pdf
            FileTypes.ZIP -> R.drawable.zip
            else -> R.drawable.file_text
        }
    }
}

private val sizeUnits: Array<String> = arrayOf("B", "KB", "MB", "GB")

/**
 * Vraca nisku koja predstavlja velicinu fajla u odgovarajucoj jedinici
 *
 * @param file Fajl ciju velicinu trazimo
 * @return Velicina fajla [file]
 */
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