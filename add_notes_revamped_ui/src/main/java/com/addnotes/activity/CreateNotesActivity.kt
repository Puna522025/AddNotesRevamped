package com.addnotes.activity

import android.os.Bundle
import android.transition.AutoTransition
import android.view.Window
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.fragment.viewnotes.CreateNotesFragment
import com.addnotes.interfaces.FragmentBackPressed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateNotesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.exitTransition = AutoTransition()
        window.enterTransition = AutoTransition()

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            loadFragment(
                CreateNotesFragment.newInstance(),
                false,
                CreateNotesFragment.TAG
            )
        }
    }

    override fun onBackPressed() {
        val fragment = getFragment(CreateNotesFragment.TAG)
        if (fragment != null && fragment is CreateNotesFragment) {
            if (!((fragment as FragmentBackPressed).onBackPressed())) {
                super.onBackPressed()
            }
        }
    }
}