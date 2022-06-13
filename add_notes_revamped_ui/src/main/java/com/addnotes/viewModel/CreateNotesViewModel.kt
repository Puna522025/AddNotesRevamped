package com.addnotes.viewModel

import android.content.Intent
import android.content.SharedPreferences
import javax.inject.Inject

class CreateNotesViewModel @Inject constructor(val sharedPreferences: SharedPreferences) :
    BaseViewModel() {

    fun isWidgetFlowSelected(intent: Intent?): Boolean {
        return intent?.action != null
                && intent.action == "android.appwidget.action.APPWIDGET_CONFIGURE"

    }

    fun getValueFromSharedPreference(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

//    fun isNonWidgetFlowSelected(intent: Intent?): Boolean {
//        return intent?.getAction() == null || intent.action != "android.appwidget.action.APPWIDGET_CONFIGURE"
//
//    }

    companion object {
        const val PREF_VERSION_CODE_KEY = "version_code"
        const val MYTHEMECOLOR = "myThemeColor"
        const val PREF_WIDGET_PREFIX_KEY = "appwidget_"
    }

}