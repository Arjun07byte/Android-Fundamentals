package com.appify.scaneye.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
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
import java.util.concurrent.Executors.newSingleThreadExecutor


class MainFragment : Fragment() {
    private val myMainViewModel: MainViewModel by activityViewModels()

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
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        view.findViewById<ImageButton>(R.id.button_historyButton)
            .setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_historyFragment) }
    }

    // Listening the camera provider to bind the preview with the camera
    private fun setUpMyCameraView() {
        // Preview View and Future camera provider
        val cameraPreviewView: PreviewView = requireView().findViewById(R.id.layout_cameraPreview)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val shutterButton: ImageButton = requireView().findViewById(R.id.button_shutterButton)

        cameraProviderFuture.addListener({
            bindPreviewView(
                cameraProviderFuture.get(),
                cameraPreviewView,
                shutterButton
            )
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // Binding the Preview View with the camera
    // to display the preview and then setting up the
    // analyzer to analyze the image to read the QR codes
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindPreviewView(
        cameraProvider: ProcessCameraProvider,
        cameraPreviewView: PreviewView,
        shutterButton: ImageButton
    ) {
        val myCameraPreviewBuilder = Preview.Builder().build()
        val myCameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        myCameraPreviewBuilder.setSurfaceProvider(cameraPreviewView.surfaceProvider)

        val myImageAnalysisBuilder = ImageAnalysis.Builder().setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
        val cameraExecutor = newSingleThreadExecutor()

        myImageAnalysisBuilder.setAnalyzer(cameraExecutor) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val currentImage = imageProxy.image

            if (currentImage != null) myMainViewModel.sendImageToMLKit(
                currentImage,
                rotationDegrees
            )
            imageProxy.close()
        }

        val imageCapture =
            ImageCapture.Builder().setTargetRotation(requireView().display.rotation).build()

        shutterButton.setOnClickListener {
            imageCapture.takePicture(
                cameraExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                        super.onCaptureSuccess(imageProxy)
                        val imageBuffer = imageProxy.planes[0].buffer
                        val imageBytes = ByteArray(imageBuffer.capacity()); imageBuffer[imageBytes]

                        val imageBitmap =
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        val rotationDegree = imageProxy.imageInfo.rotationDegrees
                        myMainViewModel.capturedImageBitmap =
                            imageBitmap; myMainViewModel.rotationDegree = rotationDegree
                        navigateToDisplayImage()
                        imageProxy.close()
                    }
                })
        }

        // making sure that there are no previous bindings to
        // the camera provider
        cameraProvider.unbindAll()

        // Binding the camera view with the user camera and
        // all the features and use cases of the CameraX we
        // will be using in the near future
        cameraProvider.bindToLifecycle(
            this.viewLifecycleOwner,
            myCameraSelector,
            imageCapture,
            myImageAnalysisBuilder,
            myCameraPreviewBuilder
        )
    }

    private fun navigateToDisplayImage() {
        ContextCompat.getMainExecutor(requireContext())
            .execute { findNavController().navigate(R.id.action_mainFragment_to_displayImageFragment) }
    }

    // Checking whether the user has granted the camera access
    // to the application or not
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) setUpMyCameraView()
    }
}