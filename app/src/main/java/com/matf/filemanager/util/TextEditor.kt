package com.matf.filemanager.util

import android.util.Log
import com.matf.filemanager.versions.StateSaver
import java.io.*
import java.util.*

// Klasa koja implementira tekst editor
class TextEditor(private var filepath: String) : StateSaver<StringEntry>() {
    private var upToDate: Boolean = false

    init {
        Log.d("INSIDE", "CONSTRUCTOR")
        upToDate = false
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

        if (upToDate) return false

        val content = readAllFromFile()

        if (content == getCurrentInstance()?.content) {
            return false
        }

        goTo(StringEntry(content, 0))

        upToDate = true
        return true
    }

    fun saveChanges(): SaveStatus {
        if (upToDate) return SaveStatus.FILENOTCHANGED
        val writer: FileWriter
        return try {
            writer = FileWriter(filepath)

            writer.write(getCurrentInstance()?.content)

            closeWriter(writer)
            upToDate = true
            SaveStatus.FILESAVED
        } catch (e: IOException) {

            SaveStatus.ERRORSAVING
        }
    }

    override fun goTo(newElement: StringEntry): Boolean {
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