package com.matf.filemanager

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.matf.filemanager.manager.DefaultFileManagerListener
import com.matf.filemanager.manager.FileManager
import com.matf.filemanager.service.FileActionReceiver
import com.matf.filemanager.util.*


class MainActivity : AppCompatActivity() {

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
    private lateinit var fileActionReceiver: FileActionReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        adapter = FileEntryAdapter(this)

        fileActionReceiver = FileActionReceiver(Handler())
        fileActionReceiver.setReceiver(object: FileActionReceiver.Receiver {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this@MainActivity, "Done", Toast.LENGTH_SHORT).show()
                    FileManager.refresh()
                }
            }
        })

        initViews()

        initFileManagerListener()

    }

    private fun initFileManagerListener() {
        FileManager.setListener(object: DefaultFileManagerListener(this, fileActionReceiver) {
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
                    }
                    MenuMode.SELECT -> {
                        layoutBottomMenu.visibility = LinearLayout.VISIBLE
                        if(FileManager.selectionEmpty()) {
                            bCopy.isEnabled = false
                            bCut.isEnabled = false
                            bDelete.isEnabled = false
                        } else {
                            bCopy.isEnabled = true
                            bCut.isEnabled = true
                            bDelete.isEnabled = true
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
                        layoutBottomMenu.visibility = LinearLayout.GONE
                        bPaste.isEnabled = false
                    }
                }
            }
        })
    }

    private fun checkPermissions() {
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
    }

    private fun initViews() {
        initNavigation()

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

        bBack.setOnClickListener {
            handleBackClick()
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

        bCut.setOnClickListener {
            FileManager.moveSelectedToClipboard(ClipboardMode.CUT)
        }

        bDelete.setOnClickListener {
            FileManager.deleteSelected()
        }

        bPaste.setOnClickListener {
            FileManager.paste()
        }
    }

    private fun initNavigation() {
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
            FileManager.goTo(directory)
            drawerLayout.closeDrawers()
            false
        }

        // Dark mode
        sSystemDarkMode = findViewById(R.id.cbSystemDarkMode)
        sDarkMode = findViewById(R.id.tDarkMode)

        val systemDarkMode = sharedPreferences.getBoolean(getString(R.string.preference_system_dark_mode_key), true)
        val darkMode = sharedPreferences.getBoolean(getString(R.string.preference_dark_mode_key), false)

        sSystemDarkMode.setOnCheckedChangeListener { _, status ->
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

        sDarkMode.setOnCheckedChangeListener { _, status ->
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

    private fun handleBackClick(){
        when(FileManager.menuMode) {
            MenuMode.OPEN -> {
                if (!FileManager.goBack()) {
                    finish()
                }
            }
            MenuMode.SELECT -> {
                FileManager.toggleSelectionMode()
            }
        }
    }

    override fun onBackPressed() {
        handleBackClick()
    }
}
