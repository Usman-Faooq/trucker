package com.buzzware.truckerworld

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivitySignUpSubscriptionBinding
import com.google.firebase.firestore.FirebaseFirestore


class SignUpSubscriptionActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignUpSubscriptionBinding

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpSubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFirestore = FirebaseFirestore.getInstance()

        mDialog = ProgressDialog(this)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        setView()
        setListener()

    }

    private fun setView() {
        val text = "<font color=#333333>Already have account?</font><font color=#818274> Sign In</font>"
        binding.signInTV.setText(Html.fromHtml(text))

    }

    private fun setListener() {

        binding.payNowTV.setOnClickListener {

            payNow()

        }

        binding.signInTV.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }

    private fun payNow() {

        mFirestore.collection("Users")
            .document(Constants.currentUser.userId)
            .update("subscribed", true).addOnSuccessListener {
                val intent = Intent(this, DashBoard::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }.addOnFailureListener {
                Log.d("LOGGER", "Error: ${it.message}")
            }
    }
}