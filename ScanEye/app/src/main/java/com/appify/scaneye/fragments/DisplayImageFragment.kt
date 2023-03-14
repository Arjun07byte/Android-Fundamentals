package com.appify.scaneye.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.appify.scaneye.MainViewModel
import com.appify.scaneye.R

class DisplayImageFragment : Fragment() {
    private val myMainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setUpStatusBar(); return inflater.inflate(R.layout.fragment_display_image, container, false)
    }

    private fun setUpStatusBar() {
        val currentWindow =
            activity?.window; currentWindow?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        currentWindow?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.transparent)
        currentWindow?.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.transparent)
        if (currentWindow != null) {
            WindowCompat.setDecorFitsSystemWindows(currentWindow, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageBitmap = myMainViewModel.capturedImageBitmap
        val rotationDegree = myMainViewModel.rotationDegree
        val imageView: ImageView = view.findViewById(R.id.imageDisplayView)

        imageView.rotation = rotationDegree.toFloat()
        imageView.setImageBitmap(imageBitmap)
    }
}