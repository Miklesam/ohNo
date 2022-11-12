package com.onelinegaming.walkietalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.onelinegaming.walkietalk.utils.AudioRecorder
import com.onelinegaming.walkietalk.utils.SocketHandler
import com.onelinegaming.walkietalk.utils.WalkieTalkieService
import kotlinx.android.synthetic.main.fragment_talk.*
import java.io.IOException
import java.io.OutputStream

class TalkFragment : Fragment(R.layout.fragment_talk) {

    var outputStream: OutputStream? = null
    private var audioRecorder: AudioRecorder? = null
    var t: Thread? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val socket = SocketHandler.getSocket()

        try {
            outputStream = socket!!.getOutputStream()
            Log.e("OUTPUT_SOCKET", "SUCCESS")
            context?.startService(Intent(context?.applicationContext, WalkieTalkieService::class.java))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        talk_bttn.setOnClickListener {
            if (talk_bttn.text.toString() == "TALK") {
                // stream audio
                talk_bttn.setText("OVER")
                audioRecorder = AudioRecorder
                t = Thread(audioRecorder)
                if (audioRecorder != null) {
                    AudioRecorder.keepRecording = true
                }
                t?.start()

            } else if (talk_bttn.getText().toString() == "OVER") {
                talk_bttn.setText("TALK")
                if (audioRecorder != null) {
                    AudioRecorder.keepRecording = false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (audioRecorder != null) {
            AudioRecorder.keepRecording = false
        }
    }
}