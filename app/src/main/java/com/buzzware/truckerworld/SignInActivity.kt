package com.buzzware.truckerworld

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivitySignInBinding
import com.buzzware.truckerworld.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignInActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignInBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        mDialog = ProgressDialog(this)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        setView()
        setListener()
    }

    private fun setView() {
        val text = "<font color=#333333>Don't have an account?</font><font color=#10454F> Sign Up</font>"
        binding.signUpTV.setText(Html.fromHtml(text))

    }

    private fun setListener() {

        binding.signUpTV.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginTV.setOnClickListener {
            signInUser()
        }


    }

    private fun signInUser() {

        val email = binding.emailET.text.toString()
        val password = binding.passwordET.text.toString()

        // Check if any of the fields are empty
        if (email.isEmpty()) {
            binding.emailET.error = "required"
        } else if (password.isEmpty()) {
            binding.passwordET.error = "required"
        } else {
            logInUser(email, password)
        }

    }

    private fun logInUser(email: String, password: String) {

        mDialog.show()
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userId = it.user!!.uid
                mFirestore.collection("Users").document(userId)
                    .get().addOnSuccessListener { task->
                        mDialog.dismiss()
                        var user = task.toObject(User::class.java)
                        user!!.userId = userId
                        Constants.currentUser = user!!
                        val intent = Intent(this, DashBoard::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }.addOnFailureListener {
                        mDialog.dismiss()
                    }





            }.addOnFailureListener {
                mDialog.dismiss()
            }

    }
}