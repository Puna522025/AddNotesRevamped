package com.addnotes.fragment.viewnotes

import android.content.SharedPreferences
import android.text.TextUtils
import android.view.Window
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.fragment.BaseFragment
import com.addnotes.utils.StringUtilities
import com.addnotes.utils.ThemesEngine
import com.addnotes.viewModel.HomeViewModel

abstract class BaseNotesFragment<T : ViewBinding> : BaseFragment<T>() {

    fun setTheme(sharedPreferences: SharedPreferences, toolbar: Toolbar) {
        val themeColor =
            sharedPreferences.getString(HomeViewModel.MYTHEMECOLOR, StringUtilities.EMPTY_STRING)
        val window: Window? = activity?.window
        if (!TextUtils.isEmpty(themeColor)) {
            ThemesEngine.updateThemeColors(
                requireContext(),
                themeColor,
                toolbar,
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

}