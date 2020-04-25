package com.matf.filemanager.types

import android.content.Intent
import android.net.Uri
import java.io.File

object FileTypeDetect {

    fun detectFileType(file: File) : FileType{
        if(file.extension.matches(Regex("^(txt|html|css|js|c|h|cpp|hpp|py|java)$"))) {
            return FileType.TEXT
        } else if(file.extension.matches(Regex("^(jpg|jpeg|png|JPG)$"))){
            return FileType.IMAGE
        } else if(file.extension.matches(Regex("^(mp4|mkv|webm)$"))) {
            return FileType.VIDEO
        }else if(file.extension.matches(Regex("(mp3|flac)"))){
            return FileType.AUDIO
        }else{
            return FileType.UNKNOWN
        }
    }

}