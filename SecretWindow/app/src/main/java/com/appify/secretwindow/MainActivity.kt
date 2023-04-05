package com.appify.secretwindow

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appify.secretwindow.localDatabase.SecretItemsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var dialogBuilder: Dialog
    private lateinit var tvDialog: TextView
    private lateinit var buttonDialog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val databaseInst = SecretItemsDatabase(this)
        val tvEmptySecrets: TextView = findViewById(R.id.tv_EmptySecrets)
        val buttonClearWindow: TextView = findViewById(R.id.button_clearWindow)
        val buttonQuestionMark: ImageButton = findViewById(R.id.button_questionMark)
        val rvSecret: RecyclerView = findViewById(R.id.rv_mainActivity)
        val rvAdapter = SecretsAdapter()
        inflateDialogBox()

        buttonClearWindow.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                databaseInst.getDatabaseDAO().clearAllSecrets()
            }
        }
        buttonQuestionMark.setOnClickListener { showDialogBox() }
        rvSecret.apply {
            adapter = rvAdapter; layoutManager = LinearLayoutManager(this@MainActivity)
        }
        databaseInst.getDatabaseDAO().getAllSecrets().observe(this) {
            if (it.isNotEmpty()) {
                tvEmptySecrets.visibility = View.GONE
                rvSecret.visibility = View.VISIBLE

                rvAdapter.submitListToDiffer(it)
            } else {
                tvEmptySecrets.visibility = View.VISIBLE
                rvSecret.visibility = View.GONE
            }
        }

        if (!isSecretWindowEnabled(this)) {
            showDialogBox()
        }
    }

    private fun showDialogBox() {
        if (!isSecretWindowEnabled(this)) {
            tvDialog.text = getString(R.string.enable_accessibility_text)
            buttonDialog.text = getString(R.string.go_to_settings_text)
            buttonDialog.setOnClickListener {
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