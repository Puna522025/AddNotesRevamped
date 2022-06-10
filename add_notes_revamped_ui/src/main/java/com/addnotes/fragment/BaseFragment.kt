package com.addnotes.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.handlers.NavigationHandler
import com.addnotes.utils.StringUtilities
import com.addnotes.utils.ThemesEngine
import com.addnotes.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

abstract class BaseFragment<T : ViewBinding> : Fragment() {
    @JvmField
    var binding: T? = null
    var navigationHandler: NavigationHandler? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onCreateViewBinding(inflater, container, savedInstanceState)
        return binding?.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            navigationHandler = context as NavigationHandler
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement NavigationalHandler")
        }
    }

    fun setToolBar(toolbar: Toolbar) {
        try {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        } catch (e: Exception) {
            Log.d("ToolBar Error", "ClassCastException")
        }
    }

    abstract fun onCreateViewBinding(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    )

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}