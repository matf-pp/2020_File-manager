package com.matf.filemanager.manager

import android.util.Log
import com.matf.filemanager.FileEntry
import com.matf.filemanager.Versions.StateSaver
import com.matf.filemanager.util.FileManagerChangeListener
import com.matf.filemanager.util.MenuMode

object FileManager {

    private var stateSaver: StateSaver<FileEntry> = StateSaver()
    var entries: ArrayList<FileEntry> = ArrayList()
    var menuMode: MenuMode = MenuMode.OPEN

    private var listeners: ArrayList<FileManagerChangeListener> = ArrayList()

    fun goTo(newElement: FileEntry): Boolean {
        if(newElement.file.isDirectory) {
            if(stateSaver.goTo(newElement)) {
                refresh()
                return true
            } else {
                //OVO NE BI TREBALO DA MOZE DA SE DESI UOPSTE
                return false
            }
        } else {
            Log.d("TODO", "OPEN THIS FILE")
            return false
        }
    }

    fun goBack(): Boolean {
        if(stateSaver.goBack()) {
            refresh();
            return true
        }
        return false
    }

    fun goForward(): Boolean {
        if(stateSaver.goForward()) {
            refresh();
            return true
        }
        return false
    }

    fun refresh() {
        // TODO Dont mutate entries in stateSaver
        entries.clear()
        entries.addAll(stateSaver.getCurrentInstance()?.listFileEntries().orEmpty())

        if(menuMode == MenuMode.SELECT) toggleSelectionMode()
        notifyEntryChanged()
    }

    fun toggleSelectionMode(){
        when(menuMode) {
            MenuMode.OPEN -> {
                menuMode = MenuMode.SELECT
            }
            MenuMode.SELECT -> {
                menuMode = MenuMode.OPEN
                for (f in entries)
                    f.selected = false
            }
        }
        notifySelectionModeChanged()
        notifyEntryChanged()
    }

    fun toggleSelectionAt(i: Int){
        entries[i].selected = !entries[i].selected
        notifyEntryChanged()
    }

    fun addEntryChangeListner(listener: FileManagerChangeListener) {
        listeners.add(listener)
    }

    private fun notifyEntryChanged() {
        for(listener in listeners)
            listener.onEntriesChange()
    }

    private fun notifySelectionModeChanged() {
        for(listener in listeners)
            listener.onSelectionModeChange(menuMode)
    }

}