package com.addnotes.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.addnotes.add_notes_revamped_ui.R

class NotesColorOptionsDialog(
    context: Context,
    private val dialogListener: DialogSelectionListener
) :
    Dialog(context),
    View.OnClickListener {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(true)
        setContentView(R.layout.row_type_of_note)
        setCancelable(true)

        findViewById<RelativeLayout>(R.id.imageOneLayout).setOnClickListener(this)
        findViewById<RelativeLayout>(R.id.imageTwoLayout).setOnClickListener(this)

        findViewById<ImageView>(R.id.imageOne).setImageResource(R.drawable.backgroundchange)
        findViewById<ImageView>(R.id.imageTwo).setImageResource(R.drawable.textchange2)

        findViewById<TextView>(R.id.imageTwoText).text = "Text"
        findViewById<TextView>(R.id.imageOneText).text = "Notepad"

        show()
    }

    override fun onClick(view: View?) {
        dialogListener.onDialogInputSelected(view?.id!!)
    }
}