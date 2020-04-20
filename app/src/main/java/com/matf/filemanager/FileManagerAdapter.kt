package com.matf.filemanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.matf.filemanager.Versions.StateSaver
import com.matf.filemanager.Versions.Versionable

class FileManagerAdapter : BaseAdapter(), Versionable<FileEntry> {
    private var stateSaver: StateSaver<FileEntry>? = null
    var currentSubdirectories: ArrayList<FileEntry> = ArrayList()
    var selectionMode: Boolean = false

    private var mInflator: LayoutInflater? = null

    fun init(entry: FileEntry, context: Context) {
        mInflator = LayoutInflater.from(context)
        stateSaver = StateSaver(entry)
        sync()
    }

    override fun getCurrentInstance(): FileEntry {
        return stateSaver!!.getCurrentInstance()
    }

    override fun goTo(newElement: FileEntry): Boolean {
        if(newElement.file.isDirectory){
            if(stateSaver!!.goTo(newElement)){
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
        if(stateSaver!!.goBack()){
            sync();
            return true;
        }
        return false;
    }

    override fun goForward(): Boolean {
        if(stateSaver!!.goForward()){
            sync();
            return true;
        }
        return false;
    }

    fun sync() {
        currentSubdirectories = getCurrentInstance().listFileEntries()
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

        val imageView: ImageView = view.findViewById<ImageView>(R.id.icon)

        if(!currentSubdirectories[position].file.isDirectory){
            if(currentSubdirectories[position].file.extension.matches(Regex("^(jpg|jpeg|png|JPG)$"))){
                imageView.setImageResource(R.drawable.image)
            }
            else if(currentSubdirectories[position].file.extension.matches(Regex("^(mp4|mkv|webm)$"))){
                imageView.setImageResource(R.drawable.music)
            }
            else{
                imageView.setImageResource(R.drawable.text)
            }
        }
        else{
            imageView.setImageResource(R.drawable.emptyfolder)
        }

        if (!currentSubdirectories[position].file.isDirectory)
            view.findViewById<TextView>(R.id.fileSizetv).text =
                "size: " + currentSubdirectories[position].file.length().toString() + " bytes"
        else
            view.findViewById<TextView>(R.id.fileSizetv).text = ""

        if (currentSubdirectories[position].selected){
            view.setBackgroundColor(Color.DKGRAY)
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