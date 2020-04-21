package com.matf.filemanager

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

class CopyReceiver(handler: Handler?) : ResultReceiver(handler) {
    private var receiver: Receiver? = null

    fun setReceiver(receiver: Receiver?) {
        this.receiver = receiver
    }

    interface Receiver {
        fun onReceiveResult(resultCode: Int, resultData: Bundle?)
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        if (receiver != null) {
            receiver!!.onReceiveResult(resultCode, resultData)
        }
    }
}