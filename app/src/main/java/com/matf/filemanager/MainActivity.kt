package com.matf.filemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val adapter: FileManagerAdapter = FileManagerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lista = findViewById<ListView>(R.id.mListView)

        val btnBack = findViewById<Button>(R.id.backbtn)
        val btnForward = findViewById<Button>(R.id.forwardbtn)
        val btnCopy = findViewById<Button>(R.id.copybtn)

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            initDirectory()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0
            )
        }

        lista.setOnItemClickListener { _, _, position, _ ->
            if(!adapter.selectionMode){
                val item: FileEntry = lista.getItemAtPosition(position) as FileEntry
                if (!adapter.goTo(item)) {
                    val intent = Intent(this, TextFileActivity::class.java)
                    intent.putExtra("file_path", item.file.absolutePath.toString())
                    startActivity(intent)
                }
            }else{
                adapter.toggleSelectionAt(position)
            }
        }

        lista.setOnItemLongClickListener { adapterView, view, position, l ->

            if(!adapter.selectionMode){

                adapter.toggleSelectionMode()
                adapter.toggleSelectionAt(position)
            }
            true
        }

        btnBack.setOnClickListener {
            if(adapter.selectionMode) {
                adapter.toggleSelectionMode()
            }else{
                if (!adapter.goBack()) {
                    Toast.makeText(this, "greska", Toast.LENGTH_SHORT).show()
                }
            }

        }

        btnForward.setOnClickListener {
            if (!adapter.goForward()) {
                Toast.makeText(this, "greska", Toast.LENGTH_SHORT).show()
            }
        }

        btnCopy.setOnClickListener {
            adapter.printSelected();
        }

    }

    fun initDirectory() {
        val lista = findViewById<ListView>(R.id.mListView)
        val btnBack = findViewById<Button>(R.id.backbtn)
        val btnForward = findViewById<Button>(R.id.forwardbtn)

        adapter.init(FileEntry(Environment.getExternalStorageDirectory(), false), this)
        lista.adapter = adapter

        btnBack.isEnabled = true
        btnForward.isEnabled = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (!grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                initDirectory()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0
                )
            }
        }
    }

}
