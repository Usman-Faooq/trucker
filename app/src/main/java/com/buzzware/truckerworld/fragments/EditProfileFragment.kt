package com.buzzware.truckerworld.fragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentEditProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class EditProfileFragment : Fragment() {

    lateinit var binding : FragmentEditProfileBinding

    lateinit var imageURI : Uri

    lateinit var mFirestore: FirebaseFirestore

    lateinit var mDialog : ProgressDialog
    private lateinit var fragmentContext: Context


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        mFirestore = FirebaseFirestore.getInstance()
        mDialog = ProgressDialog(fragmentContext)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        setView()
        setListener()


        return binding.root
    }


    private fun setView() {

        binding.fullNameET.setText("${Constants.currentUser.firstName} ${Constants.currentUser.lastName}")
        binding.userNameET.setText(Constants.currentUser.username)
        binding.emailET.setText(Constants.currentUser.email)
        binding.phoneET.setText(Constants.currentUser.phoneNumber)
        Glide.with(requireActivity())
            .load(Constants.currentUser.image)
            .placeholder(R.drawable.profile_dummy)
            .fitCenter().into(binding.profileIV)
        binding.profileIV.scaleType = ImageView.ScaleType.FIT_XY

    }

    private fun setListener() {

        binding.profileIV.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()

        }

        binding.saveChangeTV.setOnClickListener {

            var fullName = binding.fullNameET.text
            var userName : String = binding.userNameTV.text.toString()
            var phone : String = binding.phoneET.text.toString()

            if (fullName.isEmpty()){

            }else if (userName.isEmpty()){

            }else if (phone.isEmpty()){

            }else{
                var nameParts = fullName.split(" ")
                var firstName = nameParts.firstOrNull()
                var lastName = nameParts.lastOrNull()

                updateProfile(firstName, lastName, userName, phone)
            }

        }

    }

    private fun updateProfile(firstName: String?, lastName: String?, userName: String, phone: String) {

        if (imageURI != null){
            mDialog.show()
            var storageRef = FirebaseStorage.getInstance().reference.child("Images/${UUID.randomUUID()}.jpg")
            var uploadTask = storageRef.putFile(imageURI)
            uploadTask.addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener {

                    var imageUrl = it.toString()
                    val updates = hashMapOf("firstName" to firstName, "lastName" to lastName,
                        "username" to userName, "phoneNumber" to phone, "image" to imageUrl) as Map<String, Any>

                    mFirestore.collection("Users").document(Constants.currentUser.userId).update(updates)
                    Toast.makeText(fragmentContext, "Updated", Toast.LENGTH_SHORT).show()
                    Constants.currentUser.image = imageUrl
                    Constants.currentUser.firstName = firstName!!
                    Constants.currentUser.lastName = lastName!!
                    Constants.currentUser.username = userName
                    Constants.currentUser.phoneNumber = phone
                    mDialog.dismiss()

                }.addOnFailureListener {
                    mDialog.dismiss()
                    Toast.makeText(requireActivity(), "Error1" + it.message, Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener{
                mDialog.dismiss()
                Toast.makeText(requireActivity(), "Error2" + it.message, Toast.LENGTH_SHORT).show()
            }


        }else{
            Toast.makeText(requireActivity(), "Image is Empty", Toast.LENGTH_SHORT).show()
        }

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            var uri: Uri? = data?.data
            binding.profileIV.setImageURI(uri)
            binding.profileIV.scaleType = ImageView.ScaleType.FIT_XY
            imageURI = uri!!

            binding.profileIV.setImageURI(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(fragmentContext, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(fragmentContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}