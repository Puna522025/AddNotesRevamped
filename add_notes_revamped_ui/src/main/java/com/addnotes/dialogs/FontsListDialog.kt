package com.addnotes.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.transition.AutoTransition
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addnotes.adapter.AdapterClickListener
import com.addnotes.adapter.CreateNotesFontListAdapter
import com.addnotes.add_notes_revamped_ui.R
import java.io.IOException

class FontsListDialog(
    context: Context,
    private val fontSelectionListener: FontSelectionListener,
    fontsSelected: String
) :
    Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.exitTransition = AutoTransition()
        window?.enterTransition = AutoTransition()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        setContentView(R.layout.dialog_font)

        val listView = findViewById<View>(R.id.listView) as RecyclerView
        val typeHeading = Typeface.createFromAsset(context.assets, "fonts/Basing Regular.ttf")
        findViewById<TextView>(R.id.tvFonts).setTypeface(typeHeading)

        listView.layoutManager = LinearLayoutManager(context)

        //getting list of fonts.
        val fontList = ArrayList<String>()
        var f: Array<String>? = null
        try {
            f = context.assets.list("fonts")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        for (f1 in f!!) {
            fontList.add(f1)
        }
        val createNotesFontListAdapter =
            CreateNotesFontListAdapter(context, fontList, fontsSelected,
                object : AdapterClickListener {
                    override fun onItemClick(position: Int, v: View?) {
                        fontSelectionListener.onFontSelected(fontList, position)
                    }
                })

        val llm = LinearLayoutManager(context)
        listView.layoutManager = llm
        listView.setHasFixedSize(true)
        listView.itemAnimator = DefaultItemAnimator()
        listView.adapter = createNotesFontListAdapter
        show()
    }
}