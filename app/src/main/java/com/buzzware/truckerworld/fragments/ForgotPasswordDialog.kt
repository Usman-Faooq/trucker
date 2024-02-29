package com.buzzware.truckerworld.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.buzzware.truckerworld.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordDialog : DialogFragment() {

    private lateinit var progressDialog : ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.forgot_password_dialog, container, false)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        val email = view.findViewById<TextView>(R.id.emailET)

        view.findViewById<TextView>(R.id.forgotPasswordTV).setOnClickListener {
            progressDialog.show()
            if (email.text.toString().isNotEmpty()){
                forgotPassword(email.text.toString())
            }else{
                progressDialog.hide()
                email.setError("Please enter valid email.")
            }
        }
        return view
    }

    private fun forgotPassword(email:String){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener {
            progressDialog.hide()
            dismiss()
            Toast.makeText(requireContext(), "Request sent. Please check your email", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}