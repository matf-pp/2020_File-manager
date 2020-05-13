package com.matf.filemanager.manager

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.matf.filemanager.launcher.AudioFileActivity
import com.matf.filemanager.launcher.ImageFileActivity
import com.matf.filemanager.launcher.TextFileActivity
import com.matf.filemanager.launcher.VideoFileActivity
import com.matf.filemanager.service.FileActionReceiver
import com.matf.filemanager.service.FileActionService
import com.matf.filemanager.util.*
import java.io.File

/**
 * Implementacija osnovnih metoda FileManagerChangeListener interfejsa
 *
 * @param context Kontekst iz kog ce se pozivati drugi prozori
 * @param fileActionReceiver Objekat kome proslediti rezultate operacija
 */
abstract class DefaultFileManagerListener(private val context: Context, private val fileActionReceiver: FileActionReceiver): FileManagerChangeListener {

    // Otvaranje fajlova nasim programima
    override fun onRequestFileOpen(file: File): Boolean {
        val intent: Intent? = when(getTypeFromExtension(file.extension)) {
            FileTypes.TEXT, FileTypes.HTML -> Intent(context, TextFileActivity::class.java)
            FileTypes.IMAGE -> Intent(context, ImageFileActivity::class.java)
            FileTypes.VIDEO -> Intent(context, VideoFileActivity::class.java)
            FileTypes.AUDIO -> Intent(context, AudioFileActivity::class.java)

            else -> null
        }
        if(intent != null) {
            intent.putExtra(References.intentFilePath, file.absolutePath.toString())
            context.startActivity(intent)
            return true
        }
        return false
    }

    // Otvaranje fajlova drugim programima na uredjaju
    override fun onRequestFileOpenWith(file: File): Boolean {
        val uri = FileProvider.getUriForFile(context, "android.matf", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val mimeType: String = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension) ?: "*/*"
        intent.setDataAndType(uri, mimeType)
        try {
            context.startActivity(intent)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(context, "No app can open this file.", Toast.LENGTH_LONG).show()
        }
        return true
    }

    override fun copyFile(targets: List<File>, dest: File) {
        val i = Intent(context, FileActionService::class.java)

        i.putExtra(References.intentAction, FileActions.COPY)
        i.putExtra(References.intentTargets, targets.map { f -> f.absolutePath }.toTypedArray())
        i.putExtra(References.intentDest, dest.absolutePath)
        i.putExtra(References.intentReceiver, fileActionReceiver)

        FileActionService.enqueueWork(context, i)
    }

    override fun moveFile(targets: List<File>, dest: File) {
        val i = Intent(context, FileActionService::class.java)

        i.putExtra(References.intentAction, FileActions.MOVE)
        i.putExtra(References.intentTargets, targets.map { f -> f.absolutePath }.toTypedArray())
        i.putExtra(References.intentDest, dest.absolutePath)
        i.putExtra(References.intentReceiver, fileActionReceiver)

        FileActionService.enqueueWork(context, i)
    }

    override fun deleteFile(targets: List<File>) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Confirm")
        builder.setMessage("Are you sure you want to delete selected files?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { _, _ ->
            val i = Intent(context, FileActionService::class.java)
            i.putExtra(References.intentAction, FileActions.DELETE)
            i.putExtra(References.intentTargets, targets.map { f -> f.absolutePath }.toTypedArray())
            i.putExtra(References.intentReceiver, fileActionReceiver)

            FileActionService.enqueueWork(context, i)
        }
        builder.setNegativeButton("No") { _, _ -> }

        builder.show()
    }

}