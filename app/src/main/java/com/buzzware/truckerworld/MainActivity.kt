package com.buzzware.truckerworld

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.buzzware.truckerworld.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Handler().postDelayed(Runnable {
            val i = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(i)
            finish()
        }, 2000)

    }

}