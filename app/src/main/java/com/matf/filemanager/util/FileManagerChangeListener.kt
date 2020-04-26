package com.matf.filemanager.util

import java.io.File

interface FileManagerChangeListener {
    fun onEntriesChange()
    fun onSelectionModeChange(mode : MenuMode)
    fun onClipboardChange(mode : ClipboardMode)
    fun onRequestFileOpen(file: File): Boolean
    fun onRequestFileOpenWith(file: File) : Boolean

    fun copyFile(targets: List<File>, dest: File)
    fun moveFile(targets: List<File>, dest: File)
    fun deleteFile(targets: List<File>)
}