package com.buzzware.truckerworld

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.buzzware.truckerworld.databinding.ActivityVideoViewBinding


class VideoViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityVideoViewBinding
    lateinit var mDialog: ProgressDialog
    var videoUrl = ""
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDialog = ProgressDialog(this)
        mDialog.setMessage("Loading...")
        mDialog.setCancelable(false)

        videoUrl = intent.getStringExtra("URL").toString()

        player = ExoPlayer.Builder(this).build()

        val uri: Uri = Uri.parse(videoUrl)
        binding.apply {

            backIV.setOnClickListener {
                finish()
            }

            exoVideoView.player = player
            val mediaItem = MediaItem.fromUri(uri)
            player.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        mDialog.hide()
                    } else {
                        mDialog.show()
                    }
                }


                override fun onPlaybackStateChanged(state: Int) {
                    // Handle playback state changes if needed
                }

                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(this@VideoViewActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        player.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }
}