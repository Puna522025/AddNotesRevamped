package com.addnotes.fragment.home

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.addnotes.add_notes_revamped_ui.BuildConfig
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.add_notes_revamped_ui.databinding.AboutUsBinding
import com.addnotes.fragment.BaseFragment
import com.addnotes.utils.StringUtilities
import com.addnotes.utils.ThemesEngine
import com.addnotes.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutUsFragment : BaseFragment<AboutUsBinding>() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        binding = AboutUsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(binding!!.toolbar.toolbar)

        binding!!.copyright.version.text =
            getString(R.string.version_number) + " " + BuildConfig.VERSION_NAME
        setTheme()
    }

    private fun setTheme() {
        val themeColor =
            sharedPreferences.getString(HomeViewModel.MYTHEMECOLOR, StringUtilities.EMPTY_STRING)
        val window: Window? = activity?.getWindow()
        if (!TextUtils.isEmpty(themeColor)) {
            ThemesEngine.updateThemeColors(
                requireContext(),
                themeColor,
                binding!!.toolbar.toolbar,
                null,
                window,
                false,
                null,
                null,
                null,
                null,
                null,
                null
            )
        } else {
            window?.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.primary_dark)
        }
    }

    companion object {
        const val TAG = "AboutUsFragment"

        fun newInstance(): AboutUsFragment {
            return AboutUsFragment()
        }
    }
}