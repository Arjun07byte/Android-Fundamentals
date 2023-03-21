package com.appify.scaneye

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.appify.scaneye.localDatabase.ScanHistoryDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var myMainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        myMainViewModel.databaseInstance = ScanHistoryDatabase(this)
    }
}