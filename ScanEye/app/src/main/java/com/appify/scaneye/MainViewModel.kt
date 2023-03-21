package com.appify.scaneye

import android.media.Image
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    var databaseInstance: ScanHistoryDatabase? = null
    val liveDataAnalyzerScanResult = MutableLiveData<ScanHistoryItem?>()

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
                        val ssid = eachBarCode.wifi!!.ssid.toString()
                        val password = eachBarCode.wifi!!.password.toString()

                        liveDataAnalyzerScanResult.postValue(ScanHistoryItem("", ssid, password, "Wifi"))
                    }
                    Barcode.TYPE_URL -> {
                        val urlTitle = eachBarCode.url!!.title.toString()
                        val url = eachBarCode.url!!.url.toString()

                        liveDataAnalyzerScanResult.postValue(ScanHistoryItem("", urlTitle, url, "Web"))
                    }
                    Barcode.TYPE_TEXT -> {
                        liveDataAnalyzerScanResult.postValue(ScanHistoryItem("", "", "", ""))
                    }
                }
            }
        }
    }

    fun insertNewScanToDB(givenScan: ScanHistoryItem) {
        viewModelScope.launch {
            databaseInstance?.getScanHistoryDAO()?.insertNewScan(givenScan)
        }
    }

    fun getScanHistory() = databaseInstance?.getScanHistoryDAO()?.getAllScanHistory()
}