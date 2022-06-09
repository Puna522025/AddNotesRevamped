package com.addnotes.fragment

import com.addnotes.handlers.NavigationHandler
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint

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