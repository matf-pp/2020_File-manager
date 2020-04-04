package com.matf.filemanager

import android.Manifest
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
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    val adapter: FileManagerAdapter = FileManagerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 5
            )
        }
        val permission2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.MANAGE_DOCUMENTS
        )

        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.MANAGE_DOCUMENTS), 5
            )
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lista = findViewById<ListView>(R.id.mListView)

        val btnBack = findViewById<Button>(R.id.backbtn)
        val btnForward = findViewById<Button>(R.id.forwardbtn)

        adapter.init(FileEntry(Environment.getExternalStorageDirectory(), false), this)
        lista.adapter = adapter

        lista.setOnItemClickListener { _, _, position, _ ->
            if (!adapter.goTo(lista.getItemAtPosition(position) as FileEntry)) {
                Toast.makeText(this, "otvori ovaj fajl", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            if (!adapter.goBack()) {
                Toast.makeText(this, "greska", Toast.LENGTH_SHORT).show()
            }
        }
        btnForward.setOnClickListener {
            if (!adapter.goForward()) {
                Toast.makeText(this, "greska", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
