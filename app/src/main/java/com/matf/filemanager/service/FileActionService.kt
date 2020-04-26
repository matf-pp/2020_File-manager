package com.matf.filemanager.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import androidx.core.app.JobIntentService
import com.matf.filemanager.util.FileActions
import java.io.File


class FileActionService : JobIntentService() {

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, FileActionService::class.java, 0, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val rec: ResultReceiver = intent.getParcelableExtra("receiver")
        val action = intent.getSerializableExtra("action")

        when(action) {
            FileActions.COPY -> {
                val targets = intent.getStringArrayExtra("targets").map { t -> File(t) }
                val dest = File(intent.getStringExtra("dest"))

                targets.forEach { target ->
                    var new_name = target.nameWithoutExtension
                    //TODO Limit this by hardcoded value
                    while(true) {
                        if(dest.resolve(new_name+"."+target.extension).exists()) {
                            new_name += "-copy"
                        } else {
                            break;
                        }
                    }

                    if(target.isDirectory) {
                        target.copyRecursively(dest.resolve(new_name), false)
                    } else {
                        new_name += "." + target.extension
                        target.copyTo(dest.resolve(new_name), false)
                    }
                }
            }
            FileActions.MOVE -> {
                val targets = intent.getStringArrayExtra("targets").map { t -> File(t) }
                val dest = File(intent.getStringExtra("dest"))

                targets.forEach { target ->
                    if(!dest.resolve(target.name).exists())
                        target.renameTo(dest.resolve(target.name))
                }
            }
            FileActions.DELETE -> {
                val targets = intent.getStringArrayExtra("targets").map { t -> File(t) }
                targets.forEach { target ->
                    if (target.exists()) {
                        if (target.isDirectory) {
                            target.deleteRecursively()
                        } else {
                            target.delete()
                        }
                    }
                }
            }
        }

        val bundle = Bundle()
        bundle.putSerializable("action", action)
        rec.send(Activity.RESULT_OK, bundle)
    }

}