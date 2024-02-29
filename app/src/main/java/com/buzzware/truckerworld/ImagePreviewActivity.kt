package com.buzzware.truckerworld

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.databinding.ActivityDetailBinding
import com.buzzware.truckerworld.databinding.ActivityImagePreviewBinding

class ImagePreviewActivity : AppCompatActivity() {
    lateinit var binding: ActivityImagePreviewBinding
    var videoUrl = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePreviewBinding.inflate(layoutInflater)
        videoUrl = intent.getStringExtra("URL").toString()
        val uri: Uri = Uri.parse(videoUrl)
        binding.apply {
            Glide.with(this@ImagePreviewActivity).load(uri).into(myZoomageView)
        }
        setContentView(binding.root)
    }
}