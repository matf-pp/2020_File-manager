package com.matf.filemanager.launcher

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.matf.filemanager.R
import kotlinx.android.synthetic.main.activity_audio_file.*

// Klasa koja implementira otvaranje audio fajlova

class AudioFileActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_file)

        val myUri: Uri = Uri.parse(intent.getStringExtra("file_path"))
        audioTitle.text = myUri.lastPathSegment
        mp = MediaPlayer.create(this, myUri)
        mp.isLooping = false
        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration

        // Podesavanje jacine zvuka
        volumeBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser){
                        var volumeNum = progress / 100.0f
                        mp.setVolume(volumeNum, volumeNum)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            }
        )

        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser){
                        mp.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            }
        )

        Thread(Runnable {
            while (mp != null){
                try {
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException){}
            }
        }).start()

    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler(){
        override fun handleMessage(msg: Message?) {
            var currentPosition = msg!!.what

            positionBar.progress = currentPosition

            var elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = elapsedTime

            var remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel = "$min:"
        if(sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    // Akcija koja se desava na klik play dugmeta
    fun playBtnClick(v: View) {
        if(mp.isPlaying){
            // Stop
            mp.pause()
            playBtn.setBackgroundResource(R.drawable.play)
        }
        else{
            // Strat
            mp.start()
            playBtn.setBackgroundResource(R.drawable.stop)
        }
    }

    override fun onPause(){
        super.onPause()
        if(this.isFinishing)
            mp.stop()
    }

}
