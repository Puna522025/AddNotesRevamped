package com.addnotes.dialogs

import android.app.Dialog
import android.content.Context
import android.transition.AutoTransition
import android.view.View
import android.view.Window
import com.addnotes.add_notes_revamped_ui.R

class NotesTypeDialog(context: Context, private val dialogListener: DialogSelectionListener) :
    Dialog(context),
    View.OnClickListener {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.exitTransition = AutoTransition()
        window!!.enterTransition = AutoTransition()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        // activity?.setFinishOnTouchOutside(false)
        setCanceledOnTouchOutside(true)
        setContentView(R.layout.row_type_of_note)
        findViewById<View>(R.id.rlShoppingSelected).setOnClickListener(this)
        findViewById<View>(R.id.rlNoteSelected).setOnClickListener(this)
        show()
    }

    override fun onClick(view: View?) {
        dialogListener.onDialogInputSelected(view?.id!!)
    }
}