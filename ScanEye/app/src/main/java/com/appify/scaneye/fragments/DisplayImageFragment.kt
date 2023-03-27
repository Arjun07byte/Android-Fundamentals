package com.appify.scaneye.fragments

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.appify.scaneye.MainViewModel
import com.appify.scaneye.R
import com.appify.scaneye.dataModels.MLKitResponses
import com.google.android.material.bottomsheet.BottomSheetDialog

class DisplayImageFragment : Fragment() {
    private val myMainViewModel: MainViewModel by activityViewModels()
    private lateinit var imageView: ImageView

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
        imageView = view.findViewById(R.id.imageDisplayView)

        setUpImageView(); setUpBottomSheet()
    }

    private fun setUpImageView() {
        imageView.setImageBitmap(myMainViewModel.lastCapturedImageBitmap)
        myMainViewModel.sendLastCapturedImageToMLKit()
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

        myMainViewModel.liveDataImageCaptureResult.observe(viewLifecycleOwner) { currResponse ->
            if (currResponse is MLKitResponses.SuccessResponse) {
                val scannedItem = currResponse.scanHistoryItem!!
                bottomSheetTitle.text = scannedItem.historyItemTitle; bottomSheetSubtitle.text =
                    scannedItem.historyItemDesc

                bottomSheetIcon.visibility = View.VISIBLE; bottomSheetButton1.visibility =
                    View.VISIBLE; bottomSheetButton2.visibility = View.VISIBLE
                bottomSheetButton3.visibility = View.VISIBLE
                bottomSheetSubtitle.visibility = View.VISIBLE

                // Sharing the scanned item description as a URI to let user choose
                // the application to share
                bottomSheetButton3.setOnClickListener {
                    val myIntent = Intent(Intent.ACTION_SEND); myIntent.type = "text/plain"
                    myIntent.putExtra(Intent.EXTRA_SUBJECT, "Scan")
                    myIntent.putExtra(Intent.EXTRA_TEXT, scannedItem.historyItemDesc)

                    startActivity(Intent.createChooser(myIntent, "Choose to share"))
                }

                if (scannedItem.historyItemType != "Web") {
                    bottomSheetButton2.visibility = View.GONE; bottomSheetButton1.text =
                        "Copy Password"
                    bottomSheetIcon.setImageResource(R.drawable.ic_wifi)

                    // Copying the password of the Wifi / scanned Text  to the clipBoard
                    bottomSheetButton1.setOnClickListener {
                        myClipboard.setPrimaryClip(
                            ClipData.newPlainText(
                                "Text",
                                scannedItem.historyItemDesc
                            )
                        )
                        Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    bottomSheetButton2.visibility = View.VISIBLE; bottomSheetButton1.text = "Visit"
                    bottomSheetButton2.text = "Copy"
                    bottomSheetIcon.setImageResource(R.drawable.ic_website)

                    // Opening the scanned URL in the web browser
                    bottomSheetButton1.setOnClickListener {
                        requireContext().startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(scannedItem.historyItemDesc)
                            )
                        )
                    }

                    // Copying the scanned URL to the clipBoard
                    bottomSheetButton2.setOnClickListener {
                        myClipboard.setPrimaryClip(
                            ClipData.newPlainText(
                                "Url",
                                scannedItem.historyItemDesc
                            )
                        )
                        Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT).show()
                    }
                }

                bottomSheetDialog.show()
            } else if (currResponse is MLKitResponses.ErrorResponse) {
                bottomSheetTitle.text = "No Barcode Found"; bottomSheetSubtitle.text =
                    "Some Error Occurred"

                bottomSheetIcon.visibility = View.GONE; bottomSheetButton2.visibility = View.GONE
                bottomSheetButton3.visibility = View.GONE; bottomSheetButton1.visibility = View.GONE

                bottomSheetDialog.show()
            } else if (currResponse is MLKitResponses.LoadingResponse) {
                bottomSheetTitle.text = "Loading Please Wait"
                bottomSheetSubtitle.visibility = View.GONE; bottomSheetButton1.visibility =
                    View.GONE
                bottomSheetButton2.visibility = View.GONE; bottomSheetButton3.visibility =
                    View.GONE
                bottomSheetIcon.visibility = View.GONE

                bottomSheetDialog.show()
            }
        }

        bottomSheetDialog.setOnCancelListener { findNavController().popBackStack() }
    }

    override fun onPause() {
        super.onPause()
        myMainViewModel.liveDataImageCaptureResult.postValue(MLKitResponses.LoadingResponse())
    }
}