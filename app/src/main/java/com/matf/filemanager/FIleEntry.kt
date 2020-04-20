package com.matf.filemanager

import java.io.File

class FileEntry (var file: File, var selected: Boolean){

    fun listFileEntries(): ArrayList<FileEntry> {
        var ret: ArrayList<FileEntry> = ArrayList()
        for (f: File in file.listFiles().sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })){
            ret.add(FileEntry(f, false))
        }
        return ret
    }

}