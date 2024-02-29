package com.buzzware.truckerworld

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivityMainBinding
import com.buzzware.truckerworld.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) {
            val userId = auth.currentUser!!.uid
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                val token = it.result
                FirebaseFirestore.getInstance().collection("Users").document(userId).update("token", token)
                Log.d("Looged", "token: $token")
            }
            mFirestore.collection("Users").document(userId)
                .get().addOnSuccessListener { task ->
                    val user = task.toObject(User::class.java)
                    user!!.userId = userId
                    Constants.currentUser = user!!
                    val intent = Intent(this, DashBoard::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }.addOnFailureListener {
                    navigateToSignIn()
                }
        } else {
            navigateToSignIn()
        }
    }

    private fun navigateToSignIn() {
        Handler().postDelayed(Runnable {
            val i = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(i)
            finish()
        }, 2000)
    }

}