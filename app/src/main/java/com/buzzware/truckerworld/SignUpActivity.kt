package com.buzzware.truckerworld

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.classes.LocationUtility
import com.buzzware.truckerworld.databinding.ActivitySignUpBinding
import com.buzzware.truckerworld.model.User
import com.buzzware.truckerworld.utils.setKeyboardHideOnClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.io.IOException
import java.util.*

class SignUpActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mDialog: ProgressDialog
    private lateinit var locationUtility: LocationUtility
    private var token : String = ""

    companion object {
        val REQUEST_CODE = 1000
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setKeyboardHideOnClickListener()
        locationUtility = LocationUtility(this)

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        mDialog = ProgressDialog(this)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        setView()
        setListener()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_CODE
            )
        }

    }

    private fun setView() {
        val text = "<font color=#333333>Already have account?</font><font color=#818274> Sign In</font>"
        binding.signInTV.text = Html.fromHtml(text)
    }

    private fun setListener() {

        binding.signUpTV.setOnClickListener {
            createUser()
        }

        binding.signInTV.setOnClickListener {
            finish()
        }

    }

    private fun createUser() {

        val firstName = binding.firstNameET.text.toString()
        val lastName = binding.lastNameET.text.toString()
        val email = binding.emailET.text.toString()
        val phone = binding.phoneET.text.toString()
        val password = binding.passwordET.text.toString()

        // Check if any of the fields are empty
        when {
            firstName.isEmpty() -> binding.firstNameET.error = "required"
            lastName.isEmpty() -> binding.lastNameET.error = "required"
            email.isEmpty() -> binding.emailET.error = "required"
            phone.isEmpty() -> binding.phoneET.error = "required"
            password.isEmpty() -> binding.passwordET.error = "required"
            else -> signup(firstName, lastName, email, phone, password)
        }

    }

    private fun signup(firstName: String, lastName: String,
                       email: String, phone: String, password: String) {

        mDialog.show()
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            token = it.result
        }

        locationUtility.requestLocationUpdates { currentLocation ->
            val lat = 33.66800727271232
            val lng = 72.99849110126966
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d("Looged", "createUserWithEmailAndPassword: $it")
                    val user: FirebaseUser? = it.user
                    var userID = user!!.uid

                    val address = getAddressFromLocation(currentLocation.latitude, currentLocation.longitude)

                    val userMap = hashMapOf(
                        "email" to email,
                        "password" to password,
                        "firstName" to firstName,
                        "image" to "",
                        "isOnline" to true,
                        "isBanned" to true,
                        "lastName" to lastName,
                        "phoneNumber" to phone,
                        "userDate" to System.currentTimeMillis(),
                        "username" to "$firstName.$lastName",
                        "userRole" to "user",
                        "address" to address,
                        "lat" to currentLocation.latitude,
                        "lng" to currentLocation.longitude,
                        "deviceType" to "Android",
                        "token" to token
                    )

                    val userModel = User(
                        userID, firstName, lastName, "$firstName.$lastName",
                        "", email, password, phone, "user", address, lat = currentLocation.latitude, lng = currentLocation.longitude, isOnline = true)

                    locationUtility.removeLocationUpdates()

                    mFirestore.collection("Users").document(userID)
                        .set(userMap).addOnSuccessListener { _ ->
                            mDialog.dismiss()
                            Constants.currentUser = userModel
                            val intent = Intent(this, DashBoard::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener {
                            mDialog.dismiss()
                            Toast.makeText(this@SignUpActivity, it.message, Toast.LENGTH_SHORT).show()
                            Log.d("LOGGER", "Error2: ${it.message}")
                        }


                }.addOnFailureListener {
                    mDialog.dismiss()
                    locationUtility.removeLocationUpdates()
                    Toast.makeText(this@SignUpActivity, it.message, Toast.LENGTH_SHORT).show()
                    Log.d("LOGGER", "Error1: ${it.message}")
                }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this@SignUpActivity, Locale.getDefault())
        var state: String? = null
        var country: String? = null
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                state = address.adminArea
                country = address.countryName
            }
        } catch (e: IOException) {
            Log.e("Geocoder", "Error getting address: ${e.message}")
            Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_SHORT).show()
        }
        return "$state, $country"
    }

}