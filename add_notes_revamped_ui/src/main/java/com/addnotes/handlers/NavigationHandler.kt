package com.addnotes.handlers

import androidx.fragment.app.Fragment

interface NavigationHandler {

    fun loadFragment(fragment: Fragment, isAddToBackStack: Boolean, tag: String)

    fun setScreenTitle(title: String)

    fun getFragment(fragmentTag: String) : Fragment?

}