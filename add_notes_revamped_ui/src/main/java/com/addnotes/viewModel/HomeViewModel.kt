package com.addnotes.viewModel

import android.content.SharedPreferences
import javax.inject.Inject

class HomeViewModel @Inject constructor(val sharedPreferences: SharedPreferences) :
    BaseViewModel() {

    private val firstRunDetails: StateLiveData<Boolean> = StateLiveData()

    fun getValueFromSharedPreference(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun checkFirstLaunch() {
        val savedVersionCode = sharedPreferences.getInt(PREF_VERSION_CODE_KEY, -1)

        // Check for first run or upgrade
        // Check for first run or upgrade
        if (savedVersionCode == -1) {
            // TODO This is a new install (or the user cleared the shared preferences)

            // Update the shared preferences with the current version code
            sharedPreferences.edit().putInt(PREF_VERSION_CODE_KEY, 0).apply()
            firstRunDetails.postSuccess(true)
        } else {
            firstRunDetails.postSuccess(false)
        }
    }

    fun getFirstLaunchDetails(): StateLiveData<Boolean> {
        return firstRunDetails
    }

    fun updateThemeInPreferences(themeColor: String) {
        sharedPreferences.edit().putString(MYTHEMECOLOR, themeColor).apply()
    }

    companion object {
        const val PREF_VERSION_CODE_KEY = "version_code"
        const val MYTHEMECOLOR = "myThemeColor"
    }
}