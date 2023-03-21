package com.appify.scaneye.fragments

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.appify.scaneye.MainViewModel
import com.appify.scaneye.R
import com.google.android.material.bottomsheet.BottomSheetDialog

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

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView: ImageView = view.findViewById(R.id.imageDisplayView)

        setUpBottomSheet()
    }

    private fun setUpBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this.requireContext())
        val parentView: ViewGroup = view?.parent as ViewGroup
        val bottomSheetLayout = layoutInflater.inflate(
            R.layout.layout_bottom_sheet,
            parentView,
            false
        )
        bottomSheetDialog.setContentView(bottomSheetLayout); bottomSheetDialog.setCanceledOnTouchOutside(
            false
        )

        val myClipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


        val bottomSheetTitle: TextView = bottomSheetLayout.findViewById(R.id.tv_scanResultTitle)
        val bottomSheetSubtitle: TextView =
            bottomSheetLayout.findViewById(R.id.tv_scanResultSubtitle)
        val bottomSheetButton1: Button = bottomSheetLayout.findViewById(R.id.button_scanResultbt1)
        val bottomSheetButton2: Button = bottomSheetLayout.findViewById(R.id.button_scanResultbt2)
        val bottomSheetButton3: Button = bottomSheetLayout.findViewById(R.id.button_scanResultbt3)
        val bottomSheetIcon: ImageView = bottomSheetLayout.findViewById(R.id.imgView_scanResultIcon)
    }
}