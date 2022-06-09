package com.addnotes.activity

import com.addnotes.handlers.NavigationHandler
import android.os.Bundle
import android.os.PersistableBundle
import android.transition.AutoTransition
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.fragment.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

open class BaseActivity : AppCompatActivity(), NavigationHandler {

    private var mFragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.exitTransition = AutoTransition()

        mFragmentManager = supportFragmentManager
    }

    override fun loadFragment(fragment: Fragment, isAddToBackStack: Boolean, tag: String) {
        val mFragmentTransaction: FragmentTransaction =
            mFragmentManager?.beginTransaction() ?: supportFragmentManager.beginTransaction()

        mFragmentTransaction.replace(R.id.baseContainer, fragment, tag)

        if (isAddToBackStack) {
            mFragmentTransaction.addToBackStack(tag)
        }
        mFragmentTransaction.commit()
    }

    override fun setScreenTitle(title: String) {
        setTitle(title)
    }

    override fun getFragment(fragmentTag: String): Fragment? {
        val fm: FragmentManager = supportFragmentManager
        return fm.findFragmentByTag(fragmentTag)
    }
}
