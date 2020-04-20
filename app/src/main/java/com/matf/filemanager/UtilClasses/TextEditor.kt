package com.matf.filemanager.UtilClasses

import android.util.Log
import com.matf.filemanager.Versions.StateSaver
import java.io.*
import java.util.*

class TextEditor : StateSaver<String> {
    private var upToDate: Boolean = false
    private var filepath: String = ""

    constructor(filepath: String) : super("") {
        Log.d("INSIDE", "CONSTRUCTOR")
        upToDate = false
        this.filepath = filepath

        refreshFile()
    }


    private fun closeReader(reader: BufferedReader) {
        try {
            reader.close()
        } catch (e: IOException) {
            Log.d("ERROR", "reader.close()")
        }
    }

    private fun closeWriter(writer: FileWriter) {
        try {
            writer.close()
        } catch (e: IOException) {
            Log.d("ERROR", "writer.close()")
        }
    }

    private fun readAllFromFile(): String {
        val reader: BufferedReader = try {
            BufferedReader(FileReader(filepath))
        } catch (e: FileNotFoundException) {
            Log.d("ERROR", "======== new FileReader() =========")
            return ""
        }
        val lines = ArrayList<String>()
        var line: String?
        try {
            while(true){
                line = reader.readLine();
                if(null == line) break;
                lines.add(line);
            }
        } catch (e: IOException) {
            Log.d("ERROR", "reader.readLine();")
            closeReader(reader)
            return ""
        }
        try {
            reader.close()
        } catch (e: IOException) {
            Log.d("ERROR", "reader.close()")
        }
        val sb = StringBuilder()
        for (i in lines.indices) {
            sb.append(lines[i])
            if (i != lines.size - 1) {
                sb.append("\n")
            }
        }
        closeReader(reader)
        Log.d("FROM READER", sb.toString())
        return sb.toString()
    }

    private fun refreshFile(): Boolean {
        Log.d("REFRESH FILE", "BEFORE IF")
        if (upToDate) return false
        Log.d("REFRESH FILE", "AFTER IF")
        val content = readAllFromFile()
        Log.d("REFRESH FILE", "AFTER READALLFROMFILE")
        if (content == getCurrentInstance()) {
            return false
        }
        Log.d("REFRESH FILE", "BEFORE GOTO")
        goTo(content)
        Log.d("CONTENT: ", content)
        Log.d("REFRESH FILE", "AFTER GOTO, read: "  + getCurrentInstance())
        upToDate = true
        return true
    }

    fun saveChanges(): SaveStatus {
        if (upToDate) return SaveStatus.FILENOTCHANGED
        val writer: FileWriter
        return try {
            writer = FileWriter(filepath)
            Log.d("FROM SAVECHANGES", "CURRENINSTANCE: " + getCurrentInstance())
            writer.write(getCurrentInstance())

            closeWriter(writer)
            upToDate = true
            SaveStatus.FILESAVED
        } catch (e: IOException) {
            Log.d("ERROR", "new FileWriter()")
            SaveStatus.ERRORSAVING
        }
    }

    override fun goTo(newElement: String): Boolean {
        upToDate = false
        return super.goTo(newElement)
    }

    override fun goBack(): Boolean {
        upToDate = false
        return super.goBack()
    }

    override fun goForward(): Boolean {
        upToDate = false
        return super.goForward()
    }
}