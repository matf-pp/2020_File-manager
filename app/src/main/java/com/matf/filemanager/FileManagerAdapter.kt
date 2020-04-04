package com.matf.filemanager

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.io.File

class FileManagerAdapter : BaseAdapter() {
    private var context: Context? = null
    private var currentDirectoryIndex: Int = -1
    private var history: ArrayList<FileEntry> = ArrayList()
    private var currentSubdirectories: ArrayList<FileEntry>? = null

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
        currentSubdirectories = history.get(currentDirectoryIndex).listFileEntries()

        notifyDataSetChanged()
        return true
    }

    fun goBack(): Boolean{
        if(currentDirectoryIndex == 0) return false
        currentDirectoryIndex--
        notifyDataSetChanged()
        return true
    }

    fun goForward(): Boolean{
        if(currentDirectoryIndex == history.size - 1) return false
        currentDirectoryIndex++
        notifyDataSetChanged()
        return true
    }



    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = this.mInflator!!.inflate(R.layout.listitem, parent, false)
        view.findViewById<TextView>(R.id.fileTitletv).text = files[position].name
        if (!files[position].isDirectory)
            view.findViewById<TextView>(R.id.fileSizetv).text =
                "size: " + files[position].length().toString() + " bytes"
        else
            view.findViewById<TextView>(R.id.fileSizetv).text = ""
        if (selected[position]){
            view.setBackgroundColor(Color.RED)
        }
        return view
    }

    override fun getItem(p0: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemId(p0: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}