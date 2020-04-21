package com.matf.filemanager

import android.app.Activity
import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import androidx.core.app.JobIntentService
import java.io.File


class CopyService : IntentService("test") {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent) {
//  TODO  override fun onHandleWork(intent: Intent) {
        val rec: ResultReceiver = intent.getParcelableExtra("receiver")
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

        val bundle = Bundle()
        bundle.putString("resultValue", "Done")
        rec.send(Activity.RESULT_OK, bundle)
    }

}