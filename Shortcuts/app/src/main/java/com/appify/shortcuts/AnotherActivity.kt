package com.appify.shortcuts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.appify.shortcuts.databinding.ActivityAnotherBinding

class AnotherActivity : AppCompatActivity() {
    // View Binding Variable to refer various views in our layout
    private lateinit var myViewBinding: ActivityAnotherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflating our viewBinding variable using the Inflater
        // and setting up the activity content as the ViewBinding root
        myViewBinding =
            ActivityAnotherBinding.inflate(layoutInflater); setContentView(myViewBinding.root)

        // if we are getting the DynamicShortcuts list as an empty list we are
        // proceeding to make the button and the layout as addLayout

        // Else we will check whether the list contains the respective shortcut or not
        if (ShortcutManagerCompat.getShortcuts(this, ShortcutManagerCompat.FLAG_MATCH_DYNAMIC)
                .isEmpty()
        ) setAddShortcutLayout()
        else setRemoveShortcutLayout()

        myViewBinding.buttonAnotherActivity.setOnClickListener {
            if (myViewBinding.buttonAnotherActivity.text == getString(R.string.add_shortcut_text)) {
                val newShortcut = ShortcutInfoCompat.Builder(this, "dynamicAnotherShortcut")
                    .setShortLabel("DynamicAnother")
                    .setLongLabel("Open the Another Activity")
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_another))
                    .setIntent(
                        Intent(Intent.ACTION_VIEW).setPackage("com.appify.shortcuts")
                            .setClass(this, AnotherActivity::class.java)
                    ).build()
                ShortcutManagerCompat.pushDynamicShortcut(this, newShortcut)
                setRemoveShortcutLayout()
            } else {
                ShortcutManagerCompat.removeDynamicShortcuts(this, listOf("dynamicAnotherShortcut"))
                setAddShortcutLayout()
            }
        }
    }

    private fun setAddShortcutLayout() {
        myViewBinding.tvButtonTitleAnotherActivity.text =
            getString(R.string.want_a_new_shortcut_for_this_activity_text)
        myViewBinding.buttonAnotherActivity.text = getString(R.string.add_shortcut_text)
    }

    private fun setRemoveShortcutLayout() {
        myViewBinding.tvButtonTitleAnotherActivity.text =
            getString(R.string.want_to_remove_shortcut_for_this_activity_text)
        myViewBinding.buttonAnotherActivity.text = getString(R.string.remove_shortcut_text)
    }
}