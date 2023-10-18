package com.appify.secretwindow.activities

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appify.secretwindow.R

class MainActivity : AppCompatActivity() {
    private lateinit var dialogBuilder: Dialog
    private lateinit var tvDialog: TextView
    private lateinit var buttonDialog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inflateDialogBox()
        if (!isSecretWindowEnabled(this)) {
            showDialogBox()
        }
    }

    private fun showDialogBox() {
        if (!isSecretWindowEnabled(this)) {
            tvDialog.text = getString(R.string.enable_accessibility_text)
            buttonDialog.text = getString(R.string.go_to_settings_text)
            buttonDialog.setOnClickListener {
                dialogBuilder.dismiss()
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                )
            }
        } else {
            tvDialog.text = getString(R.string.enabled_accessibility_text)
            buttonDialog.text = getString(R.string.exit_text)
            buttonDialog.setOnClickListener { dialogBuilder.dismiss() }
        }

        dialogBuilder.show()
    }

    private fun inflateDialogBox() {
        dialogBuilder = Dialog(this)
        dialogBuilder.setContentView(R.layout.dialog_layout)
        dialogBuilder.setCancelable(false)
        tvDialog = dialogBuilder.findViewById(R.id.tv_dialogBox)
        buttonDialog = dialogBuilder.findViewById(R.id.button_dialogBox)
    }

    private fun isSecretWindowEnabled(context: Context): Boolean {
        val myAccessibilityManager =
            getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledList =
            myAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (eachAccessibilityService in enabledList) {
            val eachServiceInfo = eachAccessibilityService.resolveInfo.serviceInfo
            if (eachServiceInfo.packageName.equals(context.packageName) && eachServiceInfo.name.equals(
                    getString(R.string.service_name)
                )
            ) return true
        }

        return false
    }

    override fun onResume() {
        super.onResume()

        if (!isSecretWindowEnabled(this)) {
            showDialogBox()
        } else {
            dialogBuilder.dismiss()
        }
    }
}