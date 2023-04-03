package com.appify.scaneye.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.appify.scaneye.MainViewModel
import com.appify.scaneye.R
import com.appify.scaneye.dataModels.MLKitResponses
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newSingleThreadExecutor


class MainFragment : Fragment() {
    private val myMainViewModel: MainViewModel by activityViewModels()
    private lateinit var cameraExecutorThread: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setUpStatusBar(); return inflater.inflate(R.layout.fragment_main, container, false)
    }

    // Setting up the transparent status bar to match up the application
    // UI in an efficient way by changing the current window status bar color
    private fun setUpStatusBar() {
        val currentWindow =
            activity?.window; currentWindow?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        currentWindow?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.transparent)
        currentWindow?.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.transparent)
        if (currentWindow != null) {
            WindowCompat.setDecorFitsSystemWindows(currentWindow, false)
            WindowCompat.getInsetsController(currentWindow, currentWindow.decorView).apply {
                isAppearanceLightStatusBars = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Checking whether the user has granted the camera access to
        // the application or not, if its given then move ahead with
        // setting up the cameraViewer, else request user for the permission
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // setting up the camera view for the user
            setUpMyCameraView()
        } else {
            // Requesting permission from the user
            // if the user has not granted the permission before
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setUpBottomSheet()

        // setting up the history fragment button
        view.findViewById<ImageButton>(R.id.button_historyButton)
            .setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_historyFragment) }

        view.findViewById<ImageButton>(R.id.button_shutterButton)
            .setOnClickListener { captureImageAndNavigate() }
    }

    // Listening the camera provider to bind the preview with the camera
    private fun setUpMyCameraView() {
        // Preview View and Future camera provider
        val cameraPreviewView: PreviewView = requireView().findViewById(R.id.layout_cameraPreview)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProvider = cameraProviderFuture.get()

        cameraProviderFuture.addListener({
            bindPreviewView(
                cameraProvider,
                cameraPreviewView
            )
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // Checking whether the user has granted the camera access
    // to the application or not
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) setUpMyCameraView()
    }

    // Binding the Preview View with the camera
    // to display the preview and then setting up the
    // analyzer to analyze the image to read the QR codes
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindPreviewView(
        cameraProvider: ProcessCameraProvider,
        cameraPreviewView: PreviewView
    ) {
        val myCameraPreviewBuilder = Preview.Builder().build()
        val myCameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        myCameraPreviewBuilder.setSurfaceProvider(cameraPreviewView.surfaceProvider)

        val myImageAnalysisBuilder = ImageAnalysis.Builder().setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
        cameraExecutorThread = newSingleThreadExecutor()

        // Setting up the image analyzer for the camera

        myImageAnalysisBuilder.setAnalyzer(cameraExecutorThread) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val currentImage = imageProxy.image

            if (currentImage != null) myMainViewModel.sendScansToMLKit(
                currentImage, rotationDegrees
            )
            imageProxy.close()
        }

        imageCapture = ImageCapture.Builder().build()

        // Binding the camera view with the user camera and
        // all the features and use cases of the CameraX we
        // will be using in the near future
        cameraProvider.bindToLifecycle(
            this.viewLifecycleOwner,
            myCameraSelector,
            myImageAnalysisBuilder,
            imageCapture,
            myCameraPreviewBuilder
        )
    }

    private fun captureImageAndNavigate() {
        imageCapture.takePicture(
            cameraExecutorThread,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    super.onCaptureSuccess(imageProxy)
                    val imageBuffer = imageProxy.planes[0].buffer;
                    val imageByte = ByteArray(imageBuffer.capacity())
                    imageBuffer[imageByte]

                    myMainViewModel.lastCapturedImageBitmap =
                        BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)

                    navigateToDisplayImage()
                }
            })
    }

    private fun navigateToDisplayImage() {
        ContextCompat.getMainExecutor(requireContext())
            .execute { findNavController().navigate(R.id.action_mainFragment_to_displayImageFragment) }
    }

    private fun setUpBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this.requireContext())
        val parentViewGroup = view?.parent as ViewGroup
        val bottomSheetLayout = layoutInflater.inflate(
            R.layout.layout_bottom_sheet,
            parentViewGroup,
            false
        )
        bottomSheetDialog.setContentView(bottomSheetLayout)
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        val myClipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val bottomSheetTitle: TextView = bottomSheetLayout.findViewById(R.id.tv_scanResultTitle)
        val bottomSheetSubtitle: TextView =
            bottomSheetLayout.findViewById(R.id.tv_scanResultSubtitle)
        val bottomSheetButton1: Button = bottomSheetLayout.findViewById(R.id.button_scanResultbt1)
        val bottomSheetButton2: Button = bottomSheetLayout.findViewById(R.id.button_scanResultbt2)
        val bottomSheetButton3: Button = bottomSheetLayout.findViewById(R.id.button_scanResultbt3)
        val bottomSheetIcon: ImageView = bottomSheetLayout.findViewById(R.id.imgView_scanResultIcon)

        myMainViewModel.liveDataAnalyzerScanResult.observe(viewLifecycleOwner) {
            if (it is MLKitResponses.SuccessResponse) {
                val scannedItem = it.scanHistoryItem!!
                bottomSheetTitle.text = scannedItem.historyItemTitle; bottomSheetSubtitle.text =
                    scannedItem.historyItemDesc

                // Sharing the scanned item description as a URI to let user choose
                // the application to share
                bottomSheetButton3.setOnClickListener {
                    val myIntent = Intent(Intent.ACTION_SEND); myIntent.type = "text/plain"
                    myIntent.putExtra(Intent.EXTRA_SUBJECT, "Scan")
                    myIntent.putExtra(Intent.EXTRA_TEXT, scannedItem.historyItemDesc)

                    startActivity(Intent.createChooser(myIntent, "Choose to share"))
                }

                if (scannedItem.historyItemType != "Website") {
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
            } else {
                bottomSheetDialog.hide()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        myMainViewModel.liveDataAnalyzerScanResult.postValue(null)
    }
}