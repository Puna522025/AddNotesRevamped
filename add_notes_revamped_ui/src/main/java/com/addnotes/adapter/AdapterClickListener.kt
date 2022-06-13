package com.addnotes.adapter

import android.view.View

interface AdapterClickListener {
    fun onItemClick(position: Int, v: View?)
}