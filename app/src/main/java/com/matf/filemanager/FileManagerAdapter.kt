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
import android.widget.Toast
import com.matf.filemanager.Versions.JStateSaver
import com.matf.filemanager.Versions.JVersionable
import org.json.JSONTokener

class FileManagerAdapter : BaseAdapter(), JVersionable<FileEntry> {
    private var stateSaver: JStateSaver<FileEntry> = JStateSaver<FileEntry>(null)
    var currentSubdirectories: ArrayList<FileEntry> = ArrayList()
    var selectionMode: Boolean = false

    private var mInflator: LayoutInflater? = null

    fun init(entry: FileEntry, context: Context) {
        mInflator = LayoutInflater.from(context)
        stateSaver = JStateSaver(entry)
        sync()
    }

    override fun getCurrentInstance(): FileEntry {
        return stateSaver.currentInstance
    }

    override fun goTo(newElement: FileEntry?): Boolean {
        if(newElement == null) return false;
        if(newElement.file.isDirectory){
            if(stateSaver.goTo(newElement)){
                sync()
                return true
            }else{
                //OVO NE BI TREBALO DA MOZE DA SE DESI UOPSTE
                return false;
            }
        }else{
            Log.d("TODO", "OPEN THIS FILE")
            return false
        }
    }

    override fun goBack(): Boolean {
        if(stateSaver.goBack()){
            sync();
            return true;
        }
        return false;
    }

    override fun goForward(): Boolean {
        if(stateSaver.goForward()){
            sync();
            return true;
        }
        return false;
    }

    fun sync() {
        currentSubdirectories = currentInstance.listFileEntries()
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