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
import com.matf.filemanager.launcher.ImageFileActivity
import com.matf.filemanager.launcher.TextFileActivity
import com.matf.filemanager.launcher.VideoFileActivity
import com.matf.filemanager.manager.FileEntry
import com.matf.filemanager.manager.FileManager
import com.matf.filemanager.util.ClipboardMode
import com.matf.filemanager.util.FileManagerChangeListener
import com.matf.filemanager.util.MenuMode

class MainActivity : AppCompatActivity(), FileManagerChangeListener {

    private lateinit var lFileEntries: ListView

    private lateinit var bBack: Button
    private lateinit var bForward: Button
    private lateinit var bRefresh: Button

    private lateinit var layoutBottomMenu: LinearLayout
    private lateinit var bCopy: Button
    private lateinit var bCut: Button
    private lateinit var bDelete: Button
    private lateinit var bPaste: Button

    private lateinit var adapter: FileEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = FileEntryAdapter(this)
        FileManager.addEntryChangeListener(this)

        lFileEntries = findViewById(R.id.lFileEntries)
        lFileEntries.adapter = adapter

        bBack = findViewById(R.id.bBack)
        bForward = findViewById(R.id.bForward)
        bRefresh = findViewById(R.id.bRefresh)

        layoutBottomMenu = findViewById(R.id.layoutBottomMenu)
        bCopy = findViewById(R.id.bCopy)
        bCut = findViewById(R.id.bCut)
        bDelete = findViewById(R.id.bDelete)
        bPaste = findViewById(R.id.bPaste)

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

        lFileEntries.setOnItemClickListener { _, _, position, _ ->
            if(FileManager.menuMode == MenuMode.OPEN){
                val item: FileEntry = lFileEntries.getItemAtPosition(position) as FileEntry
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

        lFileEntries.setOnItemLongClickListener { adapterView, view, position, l ->
            if(FileManager.menuMode == MenuMode.OPEN) {
                FileManager.toggleSelectionMode()
                FileManager.toggleSelectionAt(position)
            }
            true
        }

        bBack.setOnClickListener {
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

        bForward.setOnClickListener {
            if (!FileManager.goForward()) {
                Toast.makeText(this, "greska", Toast.LENGTH_SHORT).show()
            }
        }

        bRefresh.setOnClickListener {
            FileManager.refresh()
        }

        bCopy.setOnClickListener {
            FileManager.moveSelectedToClipboard(ClipboardMode.COPY)
        }

        bPaste.setOnClickListener {
            FileManager.paste()
        }
    }

    private fun initDirectory() {
        FileManager.goTo(
            FileEntry(
                Environment.getExternalStorageDirectory(),
                false
            )
        )

        bBack.isEnabled = true
        bForward.isEnabled = true
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
        // TODO Ukljuciti i iskljuciti back i forward dugmice
    }

    override fun onSelectionModeChange(mode: MenuMode) {
        when(mode) {
            MenuMode.OPEN -> {
                if(FileManager.clipboardMode == ClipboardMode.NONE)
                    layoutBottomMenu.visibility = LinearLayout.GONE
            }
            MenuMode.SELECT -> {
                layoutBottomMenu.visibility = LinearLayout.VISIBLE
                bCopy.isEnabled = true
                bCut.isEnabled = true
                bDelete.isEnabled = true
            }
        }
    }

    override fun onClipboardChange(mode: ClipboardMode) {
        when(mode) {
            ClipboardMode.COPY, ClipboardMode.CUT -> {
                bPaste.isEnabled = true
            }
            else -> {
                bPaste.isEnabled = false
            }
        }
    }

    // TODO OnBack call FileManager.onBack instead of exiting
}
