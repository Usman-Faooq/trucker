package com.buzzware.truckerworld

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buzzware.truckerworld.databinding.ActivityVideoViewBinding


class VideoViewActivity : AppCompatActivity() {

    lateinit var binding : ActivityVideoViewBinding
    lateinit var mDialog: ProgressDialog
    var videoUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDialog = ProgressDialog(this)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        videoUrl = intent.getStringExtra("URL").toString()

        val uri: Uri = Uri.parse(videoUrl)
        binding.videoView.setVideoURI(uri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        mediaController.setMediaPlayer(binding.videoView)
        binding.videoView.setMediaController(mediaController)

        mDialog.show()
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            mDialog.dismiss()
            binding.videoView.start()
        }

        binding.videoView.setOnErrorListener { mediaPlayer, i, i1 ->
            mDialog.dismiss()
            Toast.makeText(this@VideoViewActivity, "Error playing video", Toast.LENGTH_SHORT).show()
            true
        }
    }
}