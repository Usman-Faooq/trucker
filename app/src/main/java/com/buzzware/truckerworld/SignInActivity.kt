package com.buzzware.truckerworld

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivitySignInBinding
import com.buzzware.truckerworld.fragments.ForgotPasswordDialog
import com.buzzware.truckerworld.model.User
import com.buzzware.truckerworld.utils.setKeyboardHideOnClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class SignInActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignInBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mDialog: ProgressDialog
    private lateinit var forgotPasswordDialog: ForgotPasswordDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setKeyboardHideOnClickListener()
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

        binding.forgetPasswordTV.setOnClickListener {
            forgotPasswordDialog = ForgotPasswordDialog()
            forgotPasswordDialog.show(supportFragmentManager,"Forgot Password")
        }

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

    private fun showDialog() {
        val dialog = Dialog(this@SignInActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.forgot_password_dialog)


        val yesBtn = dialog.findViewById(R.id.signUpTV) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    val token = it.result
                    FirebaseFirestore.getInstance().collection("Users").document(userId).update("token", token)
                    Log.d("Looged", "token: $token")
                }
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
                        Toast.makeText(this@SignInActivity, it.message, Toast.LENGTH_SHORT).show()
                        mDialog.dismiss()
                    }
            }.addOnFailureListener {
                Toast.makeText(this@SignInActivity, it.message, Toast.LENGTH_SHORT).show()
                mDialog.dismiss()
            }

    }
}