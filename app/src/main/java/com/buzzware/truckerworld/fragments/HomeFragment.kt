package com.buzzware.truckerworld.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.PostsAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentHomeBinding
import com.buzzware.truckerworld.model.Posts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class HomeFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE_VIDEO = 1001
        private const val REQUEST_CODE_PERMISSIONS = 1002
        private const val REQUEST_CODE_CAPTURE_IMAGE = 1003
        private const val REQUEST_CODE_RECORD_VIDEO = 1004
    }

    private lateinit var fragmentContext: Context
    lateinit var binding : FragmentHomeBinding
    var videoURI : Uri? = null
    lateinit var mDialog : ProgressDialog
    lateinit var adapter: PostsAdapter
    private var postList: ArrayList<Posts?>? = ArrayList()



    private val requiredPermissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION)
    } else {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        mDialog = ProgressDialog(requireContext())
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        setView()
        setListener()
        getData()

        return binding.root
    }

    private fun getData() {
        postList!!.clear()
        //mDialog.show()
        FirebaseFirestore.getInstance().collection("Products")
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    mDialog.dismiss()
                    Log.d("LOGGER", "Exception " + error.message);
                    return@addSnapshotListener;
                }

                postList!!.clear()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    queryDocumentSnapshots.forEach {
                        val model = it.toObject(Posts::class.java)
                        model.postId = it.id
                        if (model.userId != Constants.currentUser.userId){
                            postList!!.add(model)
                        }
                    }
                    setAdapter()

                }

            }

    }

    private fun setAdapter() {
        //mDialog.dismiss()
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView1.layoutManager = layoutManager
        adapter = PostsAdapter(fragmentContext, postList, Constants.currentUser.userId)
        binding.recyclerView1.adapter = adapter

    }

    private fun setView() {
        Glide.with(requireContext()).load(Constants.currentUser.image).placeholder(R.drawable.profile_dummy).fitCenter().into(binding.profileIV)
        binding.profileIV.scaleType = ImageView.ScaleType.FIT_XY

    }

    private fun setListener() {

        binding.postSendIV.setOnClickListener {
            var description = binding.editText.text.toString()
            if (videoURI != null && !description.equals("") && description != null){
                mDialog.show()
                var postId = UUID.randomUUID().toString()
                val thumbnail: Uri = getThumbnailFromVideo(videoURI!!)!!
                var storageRef = FirebaseStorage.getInstance().reference.child("Posts/Images/${postId}.jpg")
                var uploadTask = storageRef.putFile(thumbnail)
                uploadTask.addOnSuccessListener {

                    storageRef.downloadUrl.addOnSuccessListener {

                        var imageUrl = it.toString()
                        var storageRef2 = FirebaseStorage.getInstance().reference.child("Posts/Video/${postId}.mp4")
                        var uploadTask2 = storageRef2.putFile(videoURI!!)
                        uploadTask2.addOnSuccessListener {

                            storageRef2.downloadUrl.addOnSuccessListener { it2->

                                var videoUrl = it2.toString()
                                val postMap = hashMapOf(
                                    "description" to description,
                                    "duration" to description,
                                    "postType" to "video",
                                    "publishDate" to System.currentTimeMillis(),
                                    "userId" to Constants.currentUser.userId,
                                    "thumbnailImage" to imageUrl,
                                    "videoLink" to videoUrl,
                                )

                                FirebaseFirestore.getInstance().collection("Products")
                                    .document(postId).set(postMap)
                                    .addOnSuccessListener {
                                        mDialog.dismiss()
                                        binding.editText.setText("")
                                        videoURI = null
                                        Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show()
                                        adapter.notifyDataSetChanged()

                                    }.addOnFailureListener {it2 ->
                                        mDialog.dismiss()
                                        Log.d("LOGGER", "uploading Error: ${it2.message}")
                                    }

                            }.addOnFailureListener {
                                mDialog.dismiss()
                                Log.d("LOGGER", "uploading Error: ${it.message}")
                            }

                        }.addOnFailureListener {
                            mDialog.dismiss()
                            Log.d("LOGGER", "Video Exception: ${it.message}")
                        }



                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Toast.makeText(requireActivity(), "Error1" + it.message, Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener{
                    mDialog.dismiss()
                    Log.d("LOGGER", "Image Exception: ${it.message}")
                }

            }else{
                if (videoURI == null) {
                    Toast.makeText(context, "Video Required", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Description Required", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.mediaPickerIV.setOnClickListener {
            if (hasRequiredPermissions()) {
                showImageVideoDialog()
            } else {
                requestPermissions()
            }
        }
    }

    private fun showImageVideoDialog() {
        val options = arrayOf<CharSequence>("Choose from Gallery", "Take Video")

        val builder = AlertDialog.Builder(fragmentContext)
        builder.setTitle("Select Option")
        builder.setItems(options) { _, item ->
            when (item) {
                0 -> openGalleryOrFilePicker()
                //1 -> openCamera()
                1 -> recordVideo()
            }
        }

        builder.show()
    }

    private fun openGalleryOrFilePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_VIDEO)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE)
    }

    private fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_RECORD_VIDEO)
    }

    private fun hasRequiredPermissions(): Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(fragmentContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        requestPermissions(requiredPermissions, REQUEST_CODE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageVideoDialog()
            } else {
                // Handle permission denied case here
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE_VIDEO && resultCode == RESULT_OK) {
            val selectedMediaUri = data?.data
            if (selectedMediaUri != null) {
                videoURI = selectedMediaUri
            }
        } else if (requestCode == REQUEST_CODE_RECORD_VIDEO && resultCode == RESULT_OK) {
            val capturedVideoUri = data?.data
            if (capturedVideoUri != null) {
                videoURI = capturedVideoUri
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setView()
    }

    fun getThumbnailFromVideo(videoUri: Uri): Uri? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)

        val thumbnail = retriever.getFrameAtTime()
        try {
            retriever.release()
        } catch (e: IOException) {
            Log.d("LOGGER", "Exception: ${e.message}")
        }

        if (thumbnail != null) {
            // Convert thumbnail to URI
            return getImageUri(thumbnail)
        }

        return null
    }


    fun getImageUri(bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "Thumbnail", null)
        return Uri.parse(path)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}