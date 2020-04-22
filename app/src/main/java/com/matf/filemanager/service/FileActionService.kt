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
        println("service working")
        val rec: ResultReceiver = intent.getParcelableExtra("receiver")

        val action = intent.getSerializableExtra("action")

        when(action) {
            FileActions.COPY -> {
                val type = intent.getStringExtra("type")
                val src = File(intent.getStringExtra("src"))
                val dest = File(intent.getStringExtra("dest"))

                when(type) {
                    "dir" -> {
                        src.copyRecursively(dest, false)
                    }
                    "file" -> {
                        src.copyTo(dest, false)
                    }
                }
            }
        }

        val bundle = Bundle()
        bundle.putSerializable("action", action)
        rec.send(Activity.RESULT_OK, bundle)
    }

}