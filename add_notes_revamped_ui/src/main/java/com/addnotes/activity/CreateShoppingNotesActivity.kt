package com.addnotes.activity

import android.os.Bundle
import android.transition.AutoTransition
import android.view.Window
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.fragment.viewnotes.CreateShoppingNotesFragment
import com.addnotes.interfaces.FragmentBackPressed
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateShoppingNotesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.exitTransition = AutoTransition()
        window.enterTransition = AutoTransition()

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            loadFragment(
                CreateShoppingNotesFragment.newInstance(),
                false,
                CreateShoppingNotesFragment.TAG
            )
        }
    }

    override fun onBackPressed() {
        val fragment = getFragment(CreateShoppingNotesFragment.TAG)
        if (fragment != null && fragment is CreateShoppingNotesFragment) {
            if (!((fragment as FragmentBackPressed).onBackPressed())) {
                super.onBackPressed()
            }
        }
    }
}