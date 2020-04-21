package com.matf.filemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.matf.filemanager.manager.FileManager
import com.matf.filemanager.util.FileManagerChangeListener
import com.matf.filemanager.util.MenuMode

class MainActivity : AppCompatActivity(), FileManagerChangeListener {

    private lateinit var adapter: FileEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = FileEntryAdapter(this)
        FileManager.addEntryChangeListner(this)

        val lista = findViewById<ListView>(R.id.lFileEntries)
        lista.adapter = adapter

        val btnBack = findViewById<Button>(R.id.bBack)
        val btnForward = findViewById<Button>(R.id.bForward)

        val btnCopy = findViewById<Button>(R.id.bCopy)
        val btnRefresh = findViewById<Button>(R.id.bRefresh)

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
            if(FileManager.menuMode == MenuMode.OPEN){
                val item: FileEntry = lista.getItemAtPosition(position) as FileEntry
                if (!FileManager.goTo(item)) {
                    // TODO Pomeriti ovo u adapter.goTo
                    if(item.file.extension.matches(Regex("^(txt|html|css|js|c|h|cpp|hpp|py|java)$"))) {
                        val intent = Intent(this, TextFileActivity::class.java)
                        intent.putExtra("file_path", item.file.absolutePath.toString())
                        startActivity(intent)
                    } else if(item.file.extension.matches(Regex("^(jpg|jpeg|png|JPG)$"))){
                        val intent = Intent(this, ImageFileActivity::class.java)
                        intent.putExtra("file_path", item.file.absolutePath.toString())
                        startActivity(intent)
                    } else if(item.file.extension.matches(Regex("^(mp4|mkv|webm)$"))) {
                        val intent = Intent(this, VideoFileActivity::class.java)
                        intent.putExtra("file_path", item.file.absolutePath.toString())
                        startActivity(intent)
                    } else{
                        Toast.makeText(this, "Nije moguce otvoriti fajl!", Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                FileManager.toggleSelectionAt(position)
            }
        }

        lista.setOnItemLongClickListener { adapterView, view, position, l ->
            if(FileManager.menuMode == MenuMode.OPEN) {
                FileManager.toggleSelectionMode()
                FileManager.toggleSelectionAt(position)
            }
            true
        }

        btnBack.setOnClickListener {
            when(FileManager.menuMode) {
                MenuMode.OPEN -> {
                    if (!FileManager.goBack()) {
                        Toast.makeText(this, "greska", Toast.LENGTH_SHORT).show()
                    }
                }
                MenuMode.SELECT -> {
                    FileManager.toggleSelectionMode()
                }
            }
        }

        btnForward.setOnClickListener {
            if (!FileManager.goForward()) {
                Toast.makeText(this, "greska", Toast.LENGTH_SHORT).show()
            }
        }

        btnRefresh.setOnClickListener {
            FileManager.refresh()
        }
    }

    fun initDirectory() {
        val btnBack = findViewById<Button>(R.id.bBack)
        val btnForward = findViewById<Button>(R.id.bForward)

        FileManager.goTo(FileEntry(Environment.getExternalStorageDirectory(), false))

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

    override fun onEntriesChange() {
        adapter.notifyDataSetChanged()
    }

    override fun onSelectionModeChange(mode: MenuMode) {
        val bottomMenu = findViewById<LinearLayout>(R.id.layoutBottomMenu)
        when(mode) {
            MenuMode.OPEN -> {
                bottomMenu.visibility = LinearLayout.GONE
            }
            MenuMode.SELECT -> {
                bottomMenu.visibility = LinearLayout.VISIBLE
            }
        }
    }
}
