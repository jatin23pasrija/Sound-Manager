package dk.digitalminds.soundmanager

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var mediaUriHigh = "https://thepurplekids.in/highSound.mp3"
    var mediaUriLow = "https://thepurplekids.in/lowSound.mp3"
    val pattern = longArrayOf(0, 1000, 400, 1000)

    var mediaUri: String? = mediaUriLow
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibratorService: Vibrator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switch1.setOnCheckedChangeListener { _, b ->
            if (b) mediaUri = mediaUriHigh
            if (!b) mediaUri = mediaUriLow
        }

        mediaPlayer = MediaPlayer()
        vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator



        button.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager

            am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
            )

            mediaPlayer.setDataSource(mediaUri)
            mediaPlayer.prepareAsync()

        }

        mediaPlayer.setOnPreparedListener {
            val vibrationEffect = VibrationEffect.createWaveform(createVibrationPattern(pattern, 100), 4)
            vibratorService.vibrate(vibrationEffect)
            mediaPlayer.start()
        }

        mediaPlayer.setOnBufferingUpdateListener { mp, percent ->
            Log.d("t#", percent.toString())
            if (percent > 1 || percent == 100) {
                progressBar.visibility = View.GONE
            }
        }
        button2.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                vibratorService.cancel()
            }
        }
        mediaPlayer.setOnCompletionListener {
            vibratorService.cancel()
            mediaPlayer.reset()
        }
    }


    private fun createVibrationPattern(oneShotPattern: LongArray, repeat: Int): LongArray {
        val repeatPattern = LongArray(oneShotPattern.size * repeat)
        System.arraycopy(oneShotPattern, 0, repeatPattern, 0, oneShotPattern.size)
        for (count in 1 until repeat) {
            repeatPattern[oneShotPattern.size * count] =
                500
            System.arraycopy(
                oneShotPattern,
                1,
                repeatPattern,
                oneShotPattern.size * count + 1,
                oneShotPattern.size - 1
            )
        }
        return repeatPattern
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}