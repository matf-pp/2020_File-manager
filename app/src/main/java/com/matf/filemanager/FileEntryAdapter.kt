package com.matf.filemanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.matf.filemanager.manager.FileManager
import com.matf.filemanager.util.MenuMode

class FileEntryAdapter(context: Context) : BaseAdapter() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = inflater.inflate(R.layout.file_entry, parent, false)
        view.findViewById<TextView>(R.id.fileTitletv).text = FileManager.entries[position].file.name

        val imgIcon: ImageView = view.findViewById(R.id.imgIcon)
        val bProperties: ImageView = view.findViewById(R.id.bProperties)
        val cbSelected: CheckBox = view.findViewById(R.id.cbSelected)

        if(!FileManager.entries[position].file.isDirectory) {
            when {
                FileManager.entries[position].file.extension.matches(Regex("^(jpg|jpeg|png|JPG)$")) -> imgIcon.setImageResource(R.drawable.image)
                FileManager.entries[position].file.extension.matches(Regex("^(mp4|mkv|webm)$")) -> imgIcon.setImageResource(R.drawable.music)
                FileManager.entries[position].file.extension.matches(Regex("^(mp3)$")) -> imgIcon.setImageResource(R.drawable.audio1)
                FileManager.entries[position].file.extension.matches(Regex("^(html)$")) -> imgIcon.setImageResource(R.drawable.html)
                FileManager.entries[position].file.extension.matches(Regex("^(pdf)$")) -> imgIcon.setImageResource(R.drawable.pdf)
                FileManager.entries[position].file.extension.matches(Regex("^(zip|rar|7z)$")) -> imgIcon.setImageResource(R.drawable.zip)
                else -> imgIcon.setImageResource(R.drawable.text)
            }
        } else {
            imgIcon.setImageResource(R.drawable.emptyfolder)
        }

        if (!FileManager.entries[position].file.isDirectory)
            view.findViewById<TextView>(R.id.fileSizetv).text =
                view.context.getString(R.string.text_size, FileManager.entries[position].file.length())
        else
            view.findViewById<TextView>(R.id.fileSizetv).text = ""

        when(FileManager.menuMode) {
            MenuMode.SELECT -> {
                bProperties.visibility = ImageButton.GONE
                cbSelected.visibility = CheckBox.VISIBLE
            }
            MenuMode.OPEN -> {
                bProperties.visibility = ImageButton.VISIBLE
                cbSelected.visibility = CheckBox.GONE
            }
        }

        if (FileManager.entries[position].selected) {
            cbSelected.isChecked = true
            view.setBackgroundColor(view.resources.getColor(R.color.colorHighlight))
        } else {
            cbSelected.isChecked = false
            view.setBackgroundColor(Color.TRANSPARENT)
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