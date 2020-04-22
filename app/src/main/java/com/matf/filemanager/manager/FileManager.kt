package com.matf.filemanager.manager

import android.util.Log
import com.matf.filemanager.util.ClipboardMode
import com.matf.filemanager.versions.StateSaver
import com.matf.filemanager.util.FileManagerChangeListener
import com.matf.filemanager.util.MenuMode
import java.io.File

object FileManager {

    private var stateSaver: StateSaver<FileEntry> = StateSaver()

    var currentDirectory: File? = null
    var entries: ArrayList<FileEntry> = ArrayList()

    var menuMode: MenuMode = MenuMode.OPEN
    var clipboardMode: ClipboardMode = ClipboardMode.NONE
    private var clipboard: ArrayList<File> = ArrayList()

    private var listener: FileManagerChangeListener? = null

    fun goTo(newElement: FileEntry): Boolean {
        if(newElement.file.isDirectory) {
            if(stateSaver.goTo(newElement)) {
                // TODO isto ovo u go back i forward
                currentDirectory = newElement.file
                refresh()
                return true
            } else {
                //OVO NE BI TREBALO DA MOZE DA SE DESI UOPSTE
                return false
            }
        } else {
            Log.d("TODO", "OPEN THIS FILE")
            // TODO Pozvati onRequestFileOpen
            return false
        }
    }

    fun goBack(): Boolean {
        if(stateSaver.goBack()) {
            refresh()
            return true
        }
        return false
    }

    fun goForward(): Boolean {
        if(stateSaver.goForward()) {
            refresh()
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

    fun moveSelectedToClipboard(mode: ClipboardMode) {
        clipboardMode = mode
        when(menuMode){
            MenuMode.SELECT -> {
                clipboard.clear()
                // Malo funkcionalnog programiranja :)
                clipboard.addAll(entries.filter { e -> e.selected }.map { e -> e.file })
                println("moved to clipboard")
            }
            MenuMode.OPEN -> {
                // TODO Nismo u modu za selekciju, ili ocistiti clipboard ili ne raditi nista
            }

        }
        notifyClipboardChanged()
    }

    fun copy() {
        println("copying files")
//        if(currentDirectory==null)
//            return
        for(f in clipboard) {
            if(currentDirectory?.startsWith(f) == true){
                continue
                //TODO Nalazimo se unutar fajla koji kopiramo
            }
            var new_name = f.nameWithoutExtension
            //TODO Limit this by hardcoded value
            while(true) {
                if(currentDirectory?.resolve(new_name+"."+f.extension)?.exists() == true) {
                    new_name += "-copy"
                } else {
                    break;
                }
            }

            if(!f.isDirectory)
                new_name += "." + f.extension
            listener?.copyFile(f, currentDirectory?.resolve(new_name) as File)

        }
        refresh()
    }

    fun paste() {
        when(clipboardMode) {
            ClipboardMode.NONE -> {
                //TODO Error shouldn be able to press button
            }
            ClipboardMode.COPY -> {
                copy()
                //TODO Clear clipboard
                clipboard.clear()
                clipboardMode = ClipboardMode.NONE
                notifyClipboardChanged()
            }
        }
    }

    fun setListener(listener: FileManagerChangeListener) {
        this.listener = listener
    }

    private fun notifyEntryChanged() {
        listener?.onEntriesChange()
    }

    private fun notifySelectionModeChanged() {
        listener?.onSelectionModeChange(menuMode)
    }

    private fun notifyClipboardChanged() {
        listener?.onClipboardChange(clipboardMode)
    }

    
    fun canGoBack() : Boolean = stateSaver.canGoBack()

    fun canGoForward() : Boolean = stateSaver.canGoForward()

}