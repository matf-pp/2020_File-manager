package com.matf.filemanager.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import androidx.core.app.JobIntentService
import com.matf.filemanager.util.FileActions
import com.matf.filemanager.util.References
import java.io.File

/**
 * Servis za izvrsavanje akcija nad fajlom
 *
 * Akcije ukljucuju kopiranje, premestanje i brisanje fajlova
 * Posto te akcije mogu da traju dosta vremena koristimo servis kako bi se one
 * izvrsavale u pozadini cak i ako korisnik zatvori aplikaciju
 */
class FileActionService : JobIntentService() {

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, FileActionService::class.java, 0, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val rec: ResultReceiver = intent.getParcelableExtra(References.intentReceiver)
        val action = intent.getSerializableExtra(References.intentAction)

        when(action) {
            FileActions.COPY -> {
                val targets = intent.getStringArrayExtra(References.intentTargets).map { t -> File(t) }
                val dest = File(intent.getStringExtra(References.intentDest))

                targets.forEach { target ->
                    var newName = target.nameWithoutExtension
                    //TODO Limit this by hardcoded value
                    while(true) {
                        if(dest.resolve(newName+"."+target.extension).exists()) {
                            newName += "-copy"
                        } else {
                            break
                        }
                    }

                    if(target.isDirectory) {
                        target.copyRecursively(dest.resolve(newName), false)
                    } else {
                        newName += "." + target.extension
                        target.copyTo(dest.resolve(newName), false)
                    }
                }
            }
            FileActions.MOVE -> {
                val targets = intent.getStringArrayExtra(References.intentTargets).map { t -> File(t) }
                val dest = File(intent.getStringExtra(References.intentDest))

                targets.forEach { target ->
                    if(!dest.resolve(target.name).exists())
                        target.renameTo(dest.resolve(target.name))
                }
            }
            FileActions.DELETE -> {
                val targets = intent.getStringArrayExtra(References.intentTargets).map { t -> File(t) }
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
        bundle.putSerializable(References.intentAction, action)
        rec.send(Activity.RESULT_OK, bundle)
    }

}