package com.matf.filemanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.matf.filemanager.manager.FileManager
import com.matf.filemanager.util.*

class FileEntryAdapter(context: Context) : BaseAdapter() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = inflater.inflate(R.layout.file_entry, parent, false)
        val entry = FileManager.entries[position]

        val imgIcon: ImageView = view.findViewById(R.id.imgIcon)
        val tvTitle: TextView = view.findViewById(R.id.fileTitletv)
        val tvSize: TextView = view.findViewById(R.id.fileSizetv)
        val bProperties: ImageView = view.findViewById(R.id.bProperties)
        val cbSelected: CheckBox = view.findViewById(R.id.cbSelected)

        tvTitle.text = entry.file.name

        // Podesavanje ikonice fajla
        if(!entry.file.isDirectory) {

            when(getTypeFromExtension(entry.file.extension)) {
                FileTypes.IMAGE -> imgIcon.setImageResource(R.drawable.file_image)
                FileTypes.AUDIO -> imgIcon.setImageResource(R.drawable.audio1)
                FileTypes.VIDEO -> imgIcon.setImageResource(R.drawable.file_media)
                FileTypes.HTML -> imgIcon.setImageResource(R.drawable.html)
                FileTypes.PDF -> imgIcon.setImageResource(R.drawable.pdf)
                FileTypes.ZIP -> imgIcon.setImageResource(R.drawable.zip)
                else -> imgIcon.setImageResource(R.drawable.file_text)
            }
        } else {

            if(entry.file.listFiles().isEmpty())
                imgIcon.setImageResource(R.drawable.folder_empty)
            else
                imgIcon.setImageResource(R.drawable.folder_filled)
        }

        // Postavljanje prostora koji zauzima
        if (!entry.file.isDirectory)
            tvSize.text = view.context.getString(R.string.text_size, getSizeString(FileManager.entries[position].file))
        else
            tvSize.text = ""

        // Inicijalizacija
        when(FileManager.menuMode) {
            MenuMode.SELECT -> {
                bProperties.visibility = ImageButton.GONE
                cbSelected.visibility = CheckBox.VISIBLE
            }
            MenuMode.OPEN -> {
                bProperties.visibility = ImageButton.VISIBLE
                bProperties.setOnClickListener {
                    val popup = PopupMenu(it.context, it)
                    popup.menuInflater.inflate(R.menu.file_entry_menu, popup.menu)
                    if(!FileManager.canOpenWith(entry.file))
                        popup.menu.findItem(R.id.menu_open_with).isVisible = false
                    popup.setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.menu_open_with -> FileManager.requestFileOpenWith(entry.file)
                            R.id.menu_copy -> FileManager.moveToClipboard(entry.file, ClipboardMode.COPY)
                            R.id.menu_cut -> FileManager.moveToClipboard(entry.file, ClipboardMode.CUT)
                            R.id.menu_delete -> FileManager.delete(entry.file)
                        }
                        true
                    }
                    popup.show()
                }
                cbSelected.visibility = CheckBox.GONE
            }
        }

        // Markiranje selektovanih entry-ja
        if (entry.selected) {
            cbSelected.isChecked = true
            view.setBackgroundColor(view.resources.getColor(R.color.colorHighlight))
        } else {
            cbSelected.isChecked = false
            view.setBackgroundColor(Color.TRANSPARENT)
        }

        // Handler-i dogadjaja
        view.setOnClickListener {
            view.setBackgroundColor(view.resources.getColor(R.color.colorHighlight))
            view.animate().setDuration(20).withEndAction {
                view.setBackgroundColor(Color.TRANSPARENT)
                if(FileManager.menuMode == MenuMode.OPEN){
                    if (!FileManager.goTo(entry.file)) {
                        FileManager.requestFileOpenWith(entry.file)
                    }
                }else{
                    FileManager.toggleSelectionAt(position)
                }
            }.start()
        }

        view.setOnLongClickListener {
            if(FileManager.menuMode == MenuMode.OPEN) {
                FileManager.toggleSelectionMode()
                FileManager.toggleSelectionAt(position)
            }
            true
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