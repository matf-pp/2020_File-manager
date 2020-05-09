package com.matf.filemanager.launcher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.matf.filemanager.R

import com.matf.filemanager.util.SaveStatus
import com.matf.filemanager.util.StringEntry
import com.matf.filemanager.util.TextEditor
import java.io.File
import kotlin.math.abs

// Klasa koja implementira otvaranje tekstualnih fajlova
class TextFileActivity : AppCompatActivity() {

    private lateinit var textEditor: TextEditor

    private lateinit var etFile: EditText
    private lateinit var tvTitle: TextView
    private lateinit var btnUndo: Button
    private lateinit var btnRedo: Button
    private lateinit var btnSave : Button
    private var cursorPosition: Int = 0

    private fun forceSync(){
        val currText: String = etFile.text.toString()
        if(textEditor.getCurrentInstance()?.content != currText){
            textEditor.goTo(StringEntry(currText, etFile.selectionStart))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_file)

        tvTitle = findViewById(R.id.titletv)
        etFile = findViewById(R.id.fileet)
        btnUndo = findViewById(R.id.undobtn)
        btnRedo = findViewById(R.id.redobtn)
        btnSave = findViewById(R.id.savebtn)

        val filePath = intent.getStringExtra("file_path")
        tvTitle.text = File(filePath).name
        textEditor = TextEditor(filePath)

        etFile.setText(textEditor.getCurrentInstance()?.content)

        etFile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val oldText: String = textEditor.getCurrentInstance()?.content.orEmpty()
                val newText: String = etFile.text.toString()

                if(abs(newText.length - oldText.length) > 5){
                    Log.d("TEXT-CHANGED", "SIGNIFICANT CHANGE DETECTED")
                    textEditor.goTo(StringEntry(newText, etFile.selectionStart))
                }
            }
        })

        // Dugme za vracanje sadrzaja korak u nazad
        btnUndo.setOnClickListener {
            forceSync()
            if(textEditor.goBack()){
                etFile.setText(textEditor.getCurrentInstance()?.content)
                etFile.setSelection(textEditor.getCurrentInstance()!!.cursorPosition)
            }
        }

        // Dugme za pomeranje sadrzaja u napred
        btnRedo.setOnClickListener{
            forceSync()
            if(textEditor.goForward()){
                etFile.setText(textEditor.getCurrentInstance()?.content)
                etFile.setSelection(textEditor.getCurrentInstance()!!.cursorPosition)
            }
        }

        // Dugme za cuvanje sadrzaja
        btnSave.setOnClickListener {
            forceSync()
            val ss: SaveStatus = textEditor.saveChanges()
            Toast.makeText(this, ss.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
