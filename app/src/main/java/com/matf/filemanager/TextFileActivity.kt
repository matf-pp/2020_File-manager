package com.matf.filemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.matf.filemanager.UtilClasses.JTextEditor
import com.matf.filemanager.UtilClasses.SaveStatus
import java.io.File
import java.lang.StringBuilder
import kotlin.math.abs

class TextFileActivity : AppCompatActivity() {

    var textEditor: JTextEditor? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_file)

        val titletv = findViewById<TextView>(R.id.titletv)
        val fileet = findViewById<EditText>(R.id.fileet)
        val btnUndo = findViewById<Button>(R.id.undobtn)
        val btnRedo = findViewById<Button>(R.id.redobtn)
        val btnSave = findViewById<Button>(R.id.savebtn)

        val filePath = intent.getStringExtra("file_path")
        titletv.text = "EDITING FILE: " + filePath

        textEditor = JTextEditor(filePath)
        fileet.setText(textEditor!!.currentInstance)

        fileet.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val oldText: String = textEditor!!.currentInstance
                val newText: String = p0.toString()

                if(abs(newText.length - oldText.length) > 5){
                    Log.d("TEXT-CHANGED", "SIGNIFICANT CHANGE DETECTED")
                    textEditor!!.goTo(newText)
                }
            }
        })

        btnUndo.setOnClickListener {

            if(textEditor!!.goBack()){
                fileet.setText(textEditor!!.currentInstance)
            }
        }

        btnRedo.setOnClickListener{
            if(textEditor!!.goForward()){
                fileet.setText(textEditor!!.currentInstance)
            }
        }

        btnSave.setOnClickListener {
            val currText: String = fileet.text.toString()
            if(textEditor!!.currentInstance != currText){
                textEditor!!.goTo(currText)
            }
            val ss: SaveStatus  = textEditor!!.saveChanges()
            Toast.makeText(this, ss.toString(), Toast.LENGTH_LONG).show()
        }










    }
}
