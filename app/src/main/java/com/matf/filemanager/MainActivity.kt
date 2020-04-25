package com.matf.filemanager

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.matf.filemanager.launcher.ImageFileActivity
import com.matf.filemanager.launcher.TextFileActivity
import com.matf.filemanager.launcher.VideoFileActivity
import com.matf.filemanager.manager.FileEntry
import com.matf.filemanager.manager.FileManager
import com.matf.filemanager.service.FileActionReceiver
import com.matf.filemanager.service.FileActionService
import com.matf.filemanager.types.FileType
import com.matf.filemanager.types.FileTypeDetect
import com.matf.filemanager.util.ClipboardMode
import com.matf.filemanager.util.FileActions
import com.matf.filemanager.util.FileManagerChangeListener
import com.matf.filemanager.util.MenuMode
import java.io.File


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
    private lateinit var bOpenWith: Button

    private lateinit var adapter: FileEntryAdapter
    private lateinit var fileActionReceiver: FileActionReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = FileEntryAdapter(this)
        FileManager.setListener(this)

        lFileEntries = findViewById(R.id.lFileEntries)
        lFileEntries.adapter = adapter

        bBack = findViewById(R.id.bBack)
        bForward = findViewById(R.id.bForward)
        bRefresh = findViewById(R.id.bRefresh)
        bOpenWith = findViewById(R.id.bOpenWith)

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
                if (!FileManager.goTo(item.file)) {
                    Toast.makeText(this, "Nije moguce otvoriti!", Toast.LENGTH_LONG).show()
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

        bOpenWith.setOnClickListener {
            FileManager.requestFileOpenWith()
        }

        bCopy.setOnClickListener {
            FileManager.moveSelectedToClipboard(ClipboardMode.COPY)
        }

        bCut.setOnClickListener {
            FileManager.moveSelectedToClipboard(ClipboardMode.CUT)
        }

        bDelete.setOnClickListener {
            FileManager.delete()
        }

        bPaste.setOnClickListener {
            FileManager.paste()
        }

        fileActionReceiver = FileActionReceiver(Handler())
        fileActionReceiver.setReceiver(object: FileActionReceiver.Receiver {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == Activity.RESULT_OK) {
//                    val resultValue = resultData?.getString("resultValue")
                    Toast.makeText(this@MainActivity, "Done", Toast.LENGTH_SHORT).show()
                    FileManager.refresh()
                }
            }
        })
    }

    private fun initDirectory() {
        FileManager.goTo(
            Environment.getExternalStorageDirectory()
        )
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
        bBack.isEnabled = FileManager.canGoBack()
        bForward.isEnabled = FileManager.canGoForward()
    }

    override fun onSelectionModeChange(mode: MenuMode) {
        when(mode) {
            MenuMode.OPEN -> {
                if(FileManager.clipboardMode == ClipboardMode.NONE)
                    layoutBottomMenu.visibility = LinearLayout.GONE
                bCopy.isEnabled = false
                bCut.isEnabled = false
                bDelete.isEnabled = false
                bOpenWith.visibility = View.INVISIBLE
            }
            MenuMode.SELECT -> {
                layoutBottomMenu.visibility = LinearLayout.VISIBLE
                if(FileManager.selectionEmpty()) {
                    bCopy.isEnabled = false
                    bCut.isEnabled = false
                    bDelete.isEnabled = false
                    bOpenWith.visibility = View.INVISIBLE
                } else {
                    bCopy.isEnabled = true
                    bCut.isEnabled = true
                    bDelete.isEnabled = true
                    if(FileManager.selectionSize == 1)
                        bOpenWith.visibility = View.VISIBLE
                    else
                        bOpenWith.visibility = View.INVISIBLE


                }
            }
        }
    }

    override fun onClipboardChange(mode: ClipboardMode) {
        when(mode) {
            ClipboardMode.COPY, ClipboardMode.CUT -> {
                layoutBottomMenu.visibility = LinearLayout.VISIBLE
                bPaste.isEnabled = true
            }
            else -> {

                bPaste.isEnabled = false
            }
        }
    }

    override fun onRequestFileOpen(file: File): Boolean {
        val fileType: FileType = FileTypeDetect.detectFileType(file)
        var intent: Intent
        when (fileType) {
            FileType.TEXT -> {
                intent = Intent(this, TextFileActivity::class.java)
            }
            FileType.VIDEO -> {
                intent = Intent(this, VideoFileActivity::class.java)
            }
            FileType.AUDIO -> {
                TODO("Not implemented")
            }
            FileType.IMAGE -> {
                intent = Intent(this, ImageFileActivity::class.java)
            }
            FileType.UNKNOWN -> {
                return onRequestFileOpenWith(file)
            }

        }
        intent.putExtra("file_path", file.absolutePath.toString())
        startActivity(intent)
        return true

    }

    override fun onRequestFileOpenWith(file: File): Boolean {
        val fileURI: Uri = Uri.fromFile(file)
        intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileURI, "application/octet-stream")
        intent.putExtra("file_path", file.absolutePath.toString())
        startActivity(Intent.createChooser(intent, "Choose an app to open " + file.name))
        return true
    }

    override fun copyFile(targets: List<File>, dest: File) {
        val i = Intent(this, FileActionService::class.java)

        i.putExtra("action", FileActions.COPY)
        i.putExtra("targets", targets.map { f -> f.absolutePath }.toTypedArray())
        i.putExtra("dest", dest.absolutePath)
        i.putExtra("receiver", fileActionReceiver)

        FileActionService.enqueueWork(this, i)
    }

    override fun moveFile(targets: List<File>, dest: File) {
        val i = Intent(this, FileActionService::class.java)

        i.putExtra("action", FileActions.MOVE)
        i.putExtra("targets", targets.map { f -> f.absolutePath }.toTypedArray())
        i.putExtra("dest", dest.absolutePath)
        i.putExtra("receiver", fileActionReceiver)

        FileActionService.enqueueWork(this, i)
    }

    override fun deleteFile(targets: List<File>) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm")
        builder.setMessage("Are you sure you want to delete selected files?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialog, which ->
            val i = Intent(this, FileActionService::class.java)
            i.putExtra("action", FileActions.DELETE)
            i.putExtra("targets", targets.map { f -> f.absolutePath }.toTypedArray())
            i.putExtra("receiver", fileActionReceiver)

            FileActionService.enqueueWork(this, i)
        }
        builder.setNegativeButton("No") { _, _ -> }

        builder.show()
    }



    override fun onBackPressed() {
        FileManager.goBack()
    }
}
