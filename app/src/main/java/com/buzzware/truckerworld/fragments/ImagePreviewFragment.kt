package com.buzzware.truckerworld.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.databinding.FragmentImagePreviewBinding
import com.buzzware.truckerworld.utils.getColor


class ImagePreviewFragment(var imageURL: String) : DialogFragment() {

    lateinit var binding: FragmentImagePreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogStyleConcrete)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImagePreviewBinding.inflate(layoutInflater)

        binding.apply {

            when (imageURL) {
                "first" -> {
                    myZoomageView.setImageResource(R.drawable.img_live_fit_one)
                }

                "second" -> {
                    myZoomageView.setImageResource(R.drawable.img_live_fit_two)
                }

                "third" -> {
                    myZoomageView.setImageResource(R.drawable.img_live_fit_three)
                }

                "four" -> {
                    myZoomageView.setImageResource(R.drawable.img_live_fit_four)
                }
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setDimAmount(0.8F)
            it.setBackgroundDrawable(ColorDrawable(getColor(R.color.transparent)))
        }
    }
}