package com.matf.filemanager.manager

import java.io.File

/**
 * Model za entitet predstavljen u listi fajlova
 */
data class FileEntry (var file: File, var selected: Boolean = false)