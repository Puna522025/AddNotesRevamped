package com.addnotes.activity

import android.os.Bundle
import android.transition.AutoTransition
import android.view.Window
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.fragment.home.HomeFragment
import com.addnotes.interfaces.WelcomeSkipClicked
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : BaseActivity(), WelcomeSkipClicked {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.exitTransition = AutoTransition()

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance(), false, HomeFragment.TAG)
        }
    }

    override fun onSkipClicked() {
        val fragment = getFragment(HomeFragment.TAG)
        if (fragment != null && fragment is HomeFragment)
            fragment.onSkipClicked()
    }
}