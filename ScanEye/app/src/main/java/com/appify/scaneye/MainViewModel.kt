package com.appify.scaneye

import android.media.Image
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class MainViewModel : ViewModel() {
    private val myScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC).build()

    // Function used to send the image captured at the moment
    // to the Firebase ML Kit and receive the responses from there
    fun sendImageToMLKit(
        currentImage: Image,
        rotationDegrees: Int
    ) {
        val imageToSend = InputImage.fromMediaImage(currentImage, rotationDegrees)
        val myScanner = BarcodeScanning.getClient(myScannerOptions)
        myScanner.process(imageToSend).addOnSuccessListener { barcodes ->
            for (eachBarCode in barcodes) {
                when (eachBarCode.valueType) {
                    Barcode.TYPE_WIFI -> {
                        val ssid = eachBarCode.wifi!!.ssid
                        val password = eachBarCode.wifi!!.password
                        val type = eachBarCode.wifi!!.encryptionType
                    }
                    Barcode.TYPE_URL -> {
                        val urlTitle = eachBarCode.url!!.title
                        val url = eachBarCode.url!!.url
                    }
                    Barcode.TYPE_TEXT -> {

                    }
                }
            }
        }.addOnFailureListener {
            Log.e("INSIDE_FAILURE_SCAN", "SCANS FAILED, ${it.localizedMessage}")
        }
    }
}