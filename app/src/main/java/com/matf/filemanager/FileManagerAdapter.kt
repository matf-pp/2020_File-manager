package com.matf.filemanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.io.File

class FileManagerAdapter : BaseAdapter() {
    private var currentDirectoryIndex: Int = -1
    var history: ArrayList<FileEntry> = ArrayList()
    var currentSubdirectories: ArrayList<FileEntry> = ArrayList()
    var selectionMode: Boolean = false

    private var mInflator: LayoutInflater? = null

    fun init(entry: FileEntry, context: Context) {
        mInflator = LayoutInflater.from(context)
        currentDirectoryIndex = 0;
        history.clear()

        history.add(entry)
        sync()
    }

    fun goTo(entry: FileEntry): Boolean{
        if(! entry.file.isDirectory){
            Log.d("TODO", "Proveriti kog tipa je fajl i otvoriti odgovarajuci program/nas ugradjen sistem za otvaranje")
            return false
        }
        if(currentDirectoryIndex == history.size - 1){
            history.add(entry)
            currentDirectoryIndex++
        }else{
            for(i in history.size - 1 downTo currentDirectoryIndex + 1){
                history.removeAt(i)
            }
            history.add(entry)
            currentDirectoryIndex++
        }
        sync()
        return true
    }

    fun goBack(): Boolean{
        if(currentDirectoryIndex == 0) return false
        currentDirectoryIndex--
        sync()
        return true
    }

    fun goForward(): Boolean{
        if(currentDirectoryIndex == history.size - 1) return false
        currentDirectoryIndex++
        sync()
        return true
    }

    fun sync() {
        currentSubdirectories = history.get(currentDirectoryIndex).listFileEntries()
        if(selectionMode) toggleSelectionMode()
        notifyDataSetChanged()
    }

    fun toggleSelectionMode(){
        if(selectionMode){
            selectionMode = false
            for (f in currentSubdirectories){
                f.selected = false
            }
        }else{
            selectionMode = true
        }
        notifyDataSetChanged()
    }

    fun toggleSelectionAt(i: Int){
        currentSubdirectories[i].selected = !currentSubdirectories[i].selected
        notifyDataSetChanged()
    }

    fun printSelected(){
        for (f in currentSubdirectories){
            if(f.selected){
                Log.d("SELEKTOVANO:", f.file.name)
            }
        }
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = this.mInflator!!.inflate(R.layout.listitem, parent, false)
        view.findViewById<TextView>(R.id.fileTitletv).text = currentSubdirectories[position].file.name
        if (!currentSubdirectories[position].file.isDirectory)
            view.findViewById<TextView>(R.id.fileSizetv).text =
                "size: " + currentSubdirectories[position].file.length().toString() + " bytes"
        else
            view.findViewById<TextView>(R.id.fileSizetv).text = ""

        if (currentSubdirectories[position].selected){
            view.setBackgroundColor(Color.RED)
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return currentSubdirectories[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return currentSubdirectories.size
    }

}