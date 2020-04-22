package com.matf.filemanager.util

import java.io.File

interface FileManagerChangeListener {
    fun onEntriesChange()
    fun onSelectionModeChange(mode : MenuMode)
    fun onClipboardChange(mode : ClipboardMode)
    fun onRequestFileOpen(file: File): Boolean
    fun copyFile(src: File, dest: File)
}