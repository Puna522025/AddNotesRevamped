package com.addnotes.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.transition.AutoTransition
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.handlers.NavigationHandler

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

    // TODO: TO UPDATE THE LINK
    public fun rateApp() {
        try {
            val rateIntent = rateIntentForUrl("market://details")
            if (rateIntent.resolveActivity(packageManager) != null) {
                startActivity(rateIntent)
            }
        } catch (e: ActivityNotFoundException) {
            val rateIntent = rateIntentForUrl("http://play.google.com/store/apps/details")
            startActivity(rateIntent)
        }
    }

    fun rateIntentForUrl(url: String): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                String.format(
                    "%s?id=%s", url,
                    packageName
                )
            )
        )
        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        intent.addFlags(flags)
        return intent
    }
}
