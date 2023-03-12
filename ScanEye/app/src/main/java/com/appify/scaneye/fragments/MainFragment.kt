package com.appify.scaneye.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.appify.scaneye.R

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setUpStatusBar(); return inflater.inflate(R.layout.fragment_main, container, false)
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

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setUpMyCameraView()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setUpMyCameraView() {
        val cameraPreviewView: PreviewView = requireView().findViewById(R.id.layout_cameraPreview)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            bindPreviewView(cameraProviderFuture.get(), cameraPreviewView)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreviewView(
        cameraProvider: ProcessCameraProvider, cameraPreviewView: PreviewView
    ) {
        val cameraPreviewBuilder = Preview.Builder().build()
        val cameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraPreviewBuilder.setSurfaceProvider(cameraPreviewView.surfaceProvider)
        cameraProvider.bindToLifecycle(
            this.viewLifecycleOwner, cameraSelector, cameraPreviewBuilder
        )
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) setUpMyCameraView()
    }
}