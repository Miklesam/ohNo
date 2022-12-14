package com.onelinegaming.walkietalk.utils

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import android.util.Log
import java.io.IOException

object AudioRecorder : Runnable {
    val SAMPLE_RATE = 16000

    @Volatile
    var keepRecording = true

    @SuppressLint("MissingPermission")
    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
        var bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        Log.e("AUDIO", "buffersize = $bufferSize")
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2
        }
        try {
            val outputStream = SocketHandler.getSocket()?.getOutputStream()
            val audioBuffer = ByteArray(bufferSize)
            val record = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            if (record.state != AudioRecord.STATE_INITIALIZED) {
                Log.e("AUDIO", "Audio Record can't initialize!")
                return
            }
            record.startRecording()
            Log.e("AUDIO", "STARTED RECORDING")
            while (keepRecording) {
                val numberOfBytes = record.read(audioBuffer, 0, audioBuffer.size)
                val writeToOutputStream = Runnable {
                    try {
                        outputStream?.write(audioBuffer)
                        outputStream?.flush()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                val thread = Thread(writeToOutputStream)
                thread.start()
            }
            record.stop()
            record.release()
            //            outputStream.close();
            Log.e("AUDIO", "Streaming stopped")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}