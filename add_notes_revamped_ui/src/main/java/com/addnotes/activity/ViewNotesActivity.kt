package com.addnotes.activity

import android.os.Bundle
import android.transition.AutoTransition
import android.view.Window
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.fragment.viewnotes.ViewShoppingNotesFragment
import com.addnotes.interfaces.FragmentBackPressed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewNotesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.exitTransition = AutoTransition()

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            loadFragment(
                ViewShoppingNotesFragment.newInstance(),
                false,
                ViewShoppingNotesFragment.TAG
            )
        }
    }

    override fun onBackPressed() {
        val fragment = getFragment(ViewShoppingNotesFragment.TAG)
        if (fragment != null && fragment is ViewShoppingNotesFragment) {
            if (!((fragment as FragmentBackPressed).onBackPressed())) {
                super.onBackPressed()
            }
        }
    }
}