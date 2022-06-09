package com.addnotes.fragment.welcome

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.addnotes.activity.HomeActivity
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.add_notes_revamped_ui.databinding.IntroScreenOneBinding
import com.addnotes.fragment.BaseFragment

class WelcomeScreenOne : BaseFragment<IntroScreenOneBinding>(), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.skipButton.setOnClickListener(this)

        binding!!.skipButton.text = "Skip"
        binding!!.skipButton.setOnClickListener(this)
        binding!!.tvNotesViewIntro.text = "Add your notes instantly."
        binding!!.tvNotesViewIntro2.text = "Customize them."
        binding!!.notesFrame1.setBackgroundColor(Color.parseColor("#b7da00"))
        binding!!.notesFrame2.setBackgroundColor(Color.parseColor("#f93f3e"))
    }

    override fun onClick(v: View) {
        if (v.id == R.id.skipButton) {
            (activity as HomeActivity).onSkipClicked()
        }
    }

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        binding = IntroScreenOneBinding.inflate(inflater, container, false)
    }
}