package com.matf.filemanager.service

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

class FileActionReceiver(handler: Handler?) : ResultReceiver(handler) {
    private var receiver: Receiver? = null

    fun setReceiver(receiver: Receiver?) {
        this.receiver = receiver
    }

    interface Receiver {
        fun onReceiveResult(resultCode: Int, resultData: Bundle?)
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        receiver?.onReceiveResult(resultCode, resultData)
    }
}