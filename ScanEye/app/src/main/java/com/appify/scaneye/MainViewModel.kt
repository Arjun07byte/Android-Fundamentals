package com.appify.scaneye

import android.graphics.Bitmap
import android.media.Image
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appify.scaneye.dataModels.DateTimeHelper
import com.appify.scaneye.dataModels.MLKitResponses
import com.appify.scaneye.dataModels.ScanHistoryItem
import com.appify.scaneye.localDatabase.ScanHistoryDatabase
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val myScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC).build()
    private val myScanner = BarcodeScanning.getClient(myScannerOptions)
    var databaseInstance: ScanHistoryDatabase? = null
    var lastCapturedImageBitmap: Bitmap? = null
    val liveDataAnalyzerScanResult = MutableLiveData<MLKitResponses>()
    val liveDataImageCaptureResult = MutableLiveData<MLKitResponses>()

    // Function used to send the image captured at the moment
    // to the Firebase ML Kit and receive the responses from there
    fun sendLastCapturedImageToMLKit() {
        // Posting the Live Data indicating the user that
        // the processing of the image has been started
        // and creating the image to send to the firebase ML Kit
        liveDataImageCaptureResult.postValue(MLKitResponses.LoadingResponse())
        val imageToSend = lastCapturedImageBitmap?.let { InputImage.fromBitmap(it, 0) }

        // sending the image to my scanner to process the image
        if (imageToSend != null) {
            processImageWithScanner(false, imageToSend)
        }
    }

    // Function used to send the image captured at the moment
    // to the Firebase ML Kit and receive the responses from there
    fun sendScansToMLKit(
        currentImage: Image,
        rotationDegrees: Int
    ) {
        // Creating the Image to send to the Firebase ML Kit
        val imageToSend = InputImage.fromMediaImage(currentImage, rotationDegrees)
        // sending the image to my scanner to process the image
        processImageWithScanner(true, imageToSend)
    }

    private fun processImageWithScanner(isFromAnalyzer: Boolean, givenImage: InputImage) {
        val processImageTask = myScanner.process(givenImage)
        processImageTask.addOnSuccessListener { barcodes ->
            for (eachBarCode in barcodes) {
                when (eachBarCode.valueType) {
                    Barcode.TYPE_WIFI -> {
                        val ssid = eachBarCode.wifi!!.ssid.toString()
                        val password = eachBarCode.wifi!!.password.toString()
                        // creating a new scanItem to post in the LiveData
                        val newScanItem = ScanHistoryItem(DateTimeHelper.getCurrentDate(), ssid, password, "Wifi",0)
                        if(!isFromAnalyzer) liveDataImageCaptureResult.postValue(MLKitResponses.SuccessResponse(newScanItem))
                        else liveDataAnalyzerScanResult.postValue(MLKitResponses.SuccessResponse(newScanItem))

                        insertNewScanToDB(newScanItem)
                    }
                    Barcode.TYPE_URL -> {
                        val urlTitle = eachBarCode.url!!.title.toString()
                        val url = eachBarCode.url!!.url.toString()
                        // creating a new scanItem to post in the LiveData
                        val newScanItem = ScanHistoryItem(DateTimeHelper.getCurrentDate(), urlTitle, url, "Website",0)
                        if(!isFromAnalyzer) liveDataImageCaptureResult.postValue(MLKitResponses.SuccessResponse(newScanItem))
                        else liveDataAnalyzerScanResult.postValue(MLKitResponses.SuccessResponse(newScanItem))

                        insertNewScanToDB(newScanItem)
                    }
                    Barcode.TYPE_TEXT -> {
                        val newScanItem = ScanHistoryItem(DateTimeHelper.getCurrentDate(), "", "", "Text",0)
                        if(!isFromAnalyzer) liveDataImageCaptureResult.postValue(MLKitResponses.SuccessResponse(newScanItem))
                        else liveDataAnalyzerScanResult.postValue(MLKitResponses.SuccessResponse(newScanItem))

                        insertNewScanToDB(newScanItem)
                    }
                }
            }
        }

        if (!isFromAnalyzer) {
            processImageTask.addOnFailureListener {
                liveDataImageCaptureResult.postValue(
                    MLKitResponses.ErrorResponse(
                        it.localizedMessage ?: "Some Error Occurred"
                    )
                )
            }
        }
    }

    private fun insertNewScanToDB(givenScan: ScanHistoryItem) {
        viewModelScope.launch {
            databaseInstance?.getScanHistoryDAO()?.insertNewScan(givenScan)
        }
    }

    fun getScanHistory() = databaseInstance?.getScanHistoryDAO()?.getAllScanHistory()
}