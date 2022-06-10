package com.addnotes.fragment.viewnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.addnotes.add_notes_revamped_ui.databinding.ActivityShoppingBinding
import com.addnotes.fragment.BaseFragment

abstract class BaseNotesFragment<T : ViewBinding> : BaseFragment<T>() {
}