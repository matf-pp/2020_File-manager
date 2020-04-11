package com.matf.filemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.lang.StringBuilder

class TextFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_file)

        val titletv = findViewById<TextView>(R.id.titletv)
        val fileet = findViewById<EditText>(R.id.fileet)



        val filePath = intent.getStringExtra("file_path")
        val f: File = File(filePath)

        titletv.text = "EDITING FILE: ${f.name}"

        val sb: StringBuilder = StringBuilder()
//        for(l in f.readLines()){
//            sb.append("\n").append(l)
//        }
        val lines = f.readLines()
        for(i in lines.indices){
            sb.append(lines[i])
            if(i != lines.size - 1){
                sb.append("\n")
            }
        }
        fileet.setText(sb.toString())






    }
}
