package com.matf.filemanager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.matf.filemanager.launcher.ImageFileActivity
import com.matf.filemanager.launcher.TextFileActivity
import com.matf.filemanager.launcher.VideoFileActivity
import com.matf.filemanager.manager.FileEntry
import com.matf.filemanager.manager.FileManager
import com.matf.filemanager.util.FileManagerChangeListener
import com.matf.filemanager.util.MenuMode

class MainActivity : AppCompatActivity(), FileManagerChangeListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var lFileEntries: ListView

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var sSystemDarkMode: Switch
    private lateinit var sDarkMode: Switch

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

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        adapter = FileEntryAdapter(this)
        FileManager.addEntryChangeListner(this)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer)
        navigationView = findViewById(R.id.nav)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener {item ->
            val directory = when(item.itemId) {
                R.id.menu_storage -> Environment.getExternalStorageDirectory()
                R.id.menu_downloads -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                R.id.menu_music -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                R.id.menu_pictures -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                else -> Environment.getExternalStorageDirectory()
            }
            FileManager.goTo(FileEntry(directory, false))
            drawerLayout.closeDrawers()
            false
        }

        sSystemDarkMode = findViewById(R.id.cbSystemDarkMode)
        sDarkMode = findViewById(R.id.tDarkMode)

        val systemDarkMode = sharedPreferences.getBoolean(getString(R.string.preference_system_dark_mode_key), true)
        val darkMode = sharedPreferences.getBoolean(getString(R.string.preference_dark_mode_key), false)

        sSystemDarkMode.setOnCheckedChangeListener { button, status ->
            sharedPreferences.edit().putBoolean(getString(R.string.preference_system_dark_mode_key), status).apply()
            if(status) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                sDarkMode.isEnabled = false
            } else {
                AppCompatDelegate.setDefaultNightMode(
                    if(sDarkMode.isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
                sDarkMode.isEnabled = true
            }
        }

        sDarkMode.setOnCheckedChangeListener { button, status ->
            if(!sSystemDarkMode.isChecked) {
                sharedPreferences.edit()
                    .putBoolean(getString(R.string.preference_dark_mode_key), status).apply()
                AppCompatDelegate.setDefaultNightMode(
                    if (sDarkMode.isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        sDarkMode.isChecked = darkMode
        sSystemDarkMode.isChecked = systemDarkMode

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
                layoutBottomMenu.visibility = LinearLayout.GONE
            }
            MenuMode.SELECT -> {
                layoutBottomMenu.visibility = LinearLayout.VISIBLE
            }
        }
    }

    // TODO OnBack call FileManager.onBack instead of exiting
}
