package com.appify.shortcuts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.content.pm.ShortcutManagerCompat.FLAG_MATCH_DYNAMIC
import androidx.core.graphics.drawable.IconCompat
import com.appify.shortcuts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var myViewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myViewBinding =
            ActivityMainBinding.inflate(layoutInflater); setContentView(myViewBinding.root)

        if (ShortcutManagerCompat.getShortcuts(this, FLAG_MATCH_DYNAMIC)
                .isEmpty()
        ) setAddShortcutLayout()
        else setRemoveShortcutLayout()

        myViewBinding.buttonMainActivity.setOnClickListener {
            if (myViewBinding.buttonMainActivity.text == getString(R.string.add_shortcut_text)) {
                val newShortcut = ShortcutInfoCompat.Builder(this, "dynamicMainShortcut")
                    .setShortLabel("DynamicMain")
                    .setLongLabel("Open the Main Activity")
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_main))
                    .setIntent(
                        Intent(Intent.ACTION_VIEW).setPackage("com.appify.shortcuts")
                            .setClass(this, MainActivity::class.java)
                    ).build()

                ShortcutManagerCompat.pushDynamicShortcut(this, newShortcut)
                setRemoveShortcutLayout()
            } else {
                ShortcutManagerCompat.removeDynamicShortcuts(this, listOf("dynamicMainShortcut"))
                setAddShortcutLayout()
            }
        }
    }

    private fun setAddShortcutLayout() {
        myViewBinding.tvButtonTitleMainActivity.text =
            getString(R.string.want_a_new_shortcut_for_this_activity_text)
        myViewBinding.buttonMainActivity.text = getString(R.string.add_shortcut_text)
    }

    private fun setRemoveShortcutLayout() {
        myViewBinding.tvButtonTitleMainActivity.text =
            getString(R.string.want_to_remove_shortcut_for_this_activity_text)
        myViewBinding.buttonMainActivity.text = getString(R.string.remove_shortcut_text)
    }
}