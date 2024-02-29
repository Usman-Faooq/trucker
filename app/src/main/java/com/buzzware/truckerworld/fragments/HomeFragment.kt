package com.buzzware.truckerworld.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.PostsAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentHomeBinding
import com.buzzware.truckerworld.model.Products
import com.buzzware.truckerworld.model.User
import com.buzzware.truckerworld.utils.gone
import com.buzzware.truckerworld.utils.goneViews
import com.buzzware.truckerworld.utils.isVisible
import com.buzzware.truckerworld.utils.show
import com.buzzware.truckerworld.utils.visible
import com.buzzware.truckerworld.utils.visibleViews
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE_VIDEO = 1001
        private const val REQUEST_CODE_PERMISSIONS = 1002
        private const val REQUEST_CODE_CAPTURE_IMAGE = 1003
        private const val REQUEST_CODE_RECORD_VIDEO = 1004
    }

    private lateinit var fragmentContext: Context
    lateinit var binding: FragmentHomeBinding
    var videoURI: Uri? = null
    lateinit var mDialog: ProgressDialog
    lateinit var adapter: PostsAdapter
    private lateinit var previewDialog: ImagePreviewFragment
    private var videoDuration: Long = 0
    private lateinit var thumbnail: Uri
    private lateinit var currentPhotoPath: String
    private var postList: ArrayList<Products?>? = ArrayList()
    private var userList: ArrayList<User?>? = ArrayList()
    private var adminPostList: ArrayList<Products?>? = ArrayList()


    private val requiredPermissions: Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        mDialog = ProgressDialog(requireContext())
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        setView()
        setListener()
        setRecyclerListener()
        getData()

        binding.apply {
            liveFitIV.setOnClickListener {
                previewDialog = ImagePreviewFragment("first")
                previewDialog.show(childFragmentManager)
            }

            liveFitFourIV.setOnClickListener {
                previewDialog = ImagePreviewFragment("four")
                previewDialog.show(childFragmentManager)
            }
        }
        return binding.root
    }

    private fun getData() {
        postList!!.clear()
        userList!!.clear()
        mDialog.show()
        FirebaseFirestore.getInstance().collection("Products")
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    mDialog.dismiss()
                    Log.d("LOGGER", "Exception " + error.message);
                    return@addSnapshotListener;
                }

                adminPostList!!.clear()
                val tempPostList = mutableListOf<Products>()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    queryDocumentSnapshots.forEach {
                        val model = it.toObject(Products::class.java)
                        model.postId = it.id
                        when (model.addedby) {
                            "admin" -> {
                                adminPostList!!.add(model)
                            }

                            "user" -> {
                                tempPostList.add(model)
                            }
                        }
                    }
                }
                postList!!.clear()
                postList!!.addAll(tempPostList)
                getAllUser()
            }
    }

    private fun getAllUser() {
        FirebaseFirestore.getInstance().collection("Users")
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val tempUserList = mutableListOf<User>()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    queryDocumentSnapshots.forEach {
                        val model = it.toObject(User::class.java)
                        model.userId = it.id
                        tempUserList.add(model)
                    }
                    userList?.addAll(tempUserList)
                    setTimeLineAdapter()
                    setTruckerWorldAdapter()
                    mDialog.hide()
                }
            }
    }

    private fun setTimeLineAdapter() {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView1.layoutManager = layoutManager
        adapter = PostsAdapter(fragmentContext, postList, userList, Constants.currentUser.userId)
        binding.recyclerView1.adapter = adapter
    }

    private fun setTruckerWorldAdapter() {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.livefitRV.layoutManager = layoutManager
        adapter =
            PostsAdapter(fragmentContext, adminPostList, userList, Constants.currentUser.userId)
        binding.livefitRV.adapter = adapter
    }

    private fun setView() {
        Glide.with(fragmentContext)
            .load(Constants.currentUser.image)
            .placeholder(R.drawable.post_place_holder_iv)
            .into(binding.profileIV)
        binding.profileIV.scaleType = ImageView.ScaleType.FIT_XY

    }

    private fun setListener() {

        binding.postSendIV.setOnClickListener {
            val description = binding.editText.text.toString()
            if (!videoURI.toString().contains(".mp4")) {
                Log.d("videoURI", "setListener: Image URI contain")
                if (videoURI != null && !description.equals("") && description != null) {
                    mDialog.show()
                    val compressedImage = compressImage(videoURI!!)
                    val postId = UUID.randomUUID().toString()
                    val postImageReference =
                        FirebaseStorage.getInstance().reference.child("Posts/Images/${postId}.jpg")

                    val uploadThumbnailToStorage = postImageReference.putBytes(compressedImage!!)
                    uploadThumbnailToStorage.addOnSuccessListener {
                        postImageReference.downloadUrl.addOnSuccessListener { imageUri ->
                            val imageUrl = imageUri.toString()
                            val postMap = hashMapOf(
                                "addedby" to "user",
                                "description" to description,
                                "duration" to 0,
                                "postType" to "image",
                                "publishDate" to System.currentTimeMillis(),
                                "userId" to Constants.currentUser.userId,
                                "thumbnailImage" to imageUrl,
                                "videoLink" to "",
                            )

                            FirebaseFirestore.getInstance().collection("Products")
                                .document(postId).set(postMap)
                                .addOnSuccessListener {
                                    mDialog.dismiss()
                                    binding.editText.setText("")
                                    videoURI = null
                                    Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT)
                                        .show()
                                    adapter.notifyDataSetChanged()

                                }.addOnFailureListener { postError ->
                                    mDialog.dismiss()
                                    Log.d("LOGGER", "uploading Error: ${postError.message}")
                                }
                        }.addOnFailureListener {
                            mDialog.dismiss()
                            Log.d("LOGGER", "Downloading Error: ${it.message}")
                        }
                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Log.d("LOGGER", "uploading Error: ${it.message}")
                    }
                } else {
                    Toast.makeText(context, "Please enter a description", Toast.LENGTH_SHORT).show()
                }

            } else {
                if (videoURI != null && !description.equals("") && description != null) {
                    mDialog.show()
                    val postId = UUID.randomUUID().toString()
                    thumbnail = getThumbnailFromVideo(videoURI!!)!!
                    videoDuration = getDurationFromVideo(videoURI!!)!!

                    val videoThumbnailReference =
                        FirebaseStorage.getInstance().reference.child("Posts/Images/${postId}.jpg")

                    val uploadThumbnailToStorage = videoThumbnailReference.putFile(thumbnail)
                    uploadThumbnailToStorage.addOnSuccessListener {

                        videoThumbnailReference.downloadUrl.addOnSuccessListener { imageUri ->
                            val imageUrl = imageUri.toString()
                            val videoPathReference =
                                FirebaseStorage.getInstance().reference.child("Posts/Video/${postId}.mp4")
                            val uploadVideoToStorage = videoPathReference.putFile(videoURI!!)
                            uploadVideoToStorage.addOnSuccessListener {

                                videoPathReference.downloadUrl.addOnSuccessListener { videoUri ->

                                    var videoUrl = videoUri.toString()
                                    val postMap = hashMapOf(
                                        "addedby" to "user",
                                        "description" to description,
                                        "duration" to videoDuration,
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
                                            Toast.makeText(
                                                context,
                                                "Upload Success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            adapter.notifyDataSetChanged()

                                        }.addOnFailureListener { it2 ->
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
                            Toast.makeText(
                                requireActivity(),
                                "Error1" + it.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Log.d("LOGGER", "Image Exception: ${it.message}")
                    }

                } else {
                    if (videoURI == null) {
                        Toast.makeText(context, "Video Required", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Description Required", Toast.LENGTH_SHORT).show()
                    }
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

    private fun setRecyclerListener() {

        binding.apply {
            friendRequestTV.apply {
                this.setOnClickListener {
                    friendRequestTV.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        setBackgroundResource(R.color.gray_color)
                        recyclerView1.visible()
                        goneViews(warmupTv, liveFitIV, liveFitFourIV, livefitRV)
                    }
                    groupRequestTV.apply {
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        setBackgroundResource(R.color.white)
                    }
                }
            }
        }

        binding.apply {
            groupRequestTV.setOnClickListener {
                groupRequestTV.apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    setBackgroundResource(R.color.gray_color)
                    livefitRV.visible()
                    recyclerView1.gone()
                }
                visibleViews(warmupTv, liveFitIV, liveFitFourIV)
                friendRequestTV.apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    setBackgroundResource(R.color.white)
                }

            }
        }

    }

    private fun showImageVideoDialog() {
        val options = arrayOf<CharSequence>("Choose from Gallery", "Take Video", "Take Picture")

        val builder = AlertDialog.Builder(fragmentContext)
        builder.setTitle("Select Option")
        builder.setItems(options) { _, item ->
            when (item) {
                0 -> openGalleryOrFilePicker()
                1 -> recordVideo()
                2 -> openCamera()
            }
        }

        builder.show()
    }

    private fun openGalleryOrFilePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*,video/*"
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_VIDEO)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            photoFile?.let {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.android.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE)
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_RECORD_VIDEO)
    }

    private fun hasRequiredPermissions(): Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    fragmentContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        requestPermissions(requiredPermissions, REQUEST_CODE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
        } else if (requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            val photoURI: Uri = Uri.fromFile(File(currentPhotoPath))
            if (photoURI != null) {
                videoURI = photoURI
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

    fun getDurationFromVideo(videoUri: Uri): Long? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val duration =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()

        try {
            retriever.release()
        } catch (e: IOException) {
            Log.d("LOGGER", "Exception: ${e.message}")
        }

        if (duration != null) {
            return duration
        }

        return null
    }


    fun getImageUri(bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "Thumbnail",
            null
        )
        return Uri.parse(path)
    }

    fun compressImage(filePath: Uri): ByteArray? {
        val options = BitmapFactory.Options()
        options.inSampleSize = 2 // You can adjust this value as needed for your use case
        val bitmap = BitmapFactory.decodeFile(filePath.path, options) ?: return null

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        bitmap.recycle()
        val compressedData = outputStream.toByteArray()
        outputStream.close()

        return compressedData
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}