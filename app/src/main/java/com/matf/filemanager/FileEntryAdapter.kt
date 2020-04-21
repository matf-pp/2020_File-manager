package com.matf.filemanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.matf.filemanager.manager.FileManager

class FileEntryAdapter(context: Context) : BaseAdapter() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = inflater.inflate(R.layout.file_entry, parent, false)
        view.findViewById<TextView>(R.id.fileTitletv).text = FileManager.entries[position].file.name

        val imageView: ImageView = view.findViewById(R.id.icon)

        if(!FileManager.entries[position].file.isDirectory) {
            when {
                FileManager.entries[position].file.extension.matches(Regex("^(jpg|jpeg|png|JPG)$")) -> imageView.setImageResource(R.drawable.image)
                FileManager.entries[position].file.extension.matches(Regex("^(mp4|mkv|webm)$")) -> imageView.setImageResource(R.drawable.music)
                else -> imageView.setImageResource(R.drawable.text)
            }
        } else {
            imageView.setImageResource(R.drawable.emptyfolder)
        }

        if (!FileManager.entries[position].file.isDirectory)
            view.findViewById<TextView>(R.id.fileSizetv).text =
                "size: " + FileManager.entries[position].file.length().toString() + " bytes"
        else
            view.findViewById<TextView>(R.id.fileSizetv).text = ""

        if (FileManager.entries[position].selected) {
            view.setBackgroundColor(Color.DKGRAY)
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return FileManager.entries[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return FileManager.entries.size
    }

}