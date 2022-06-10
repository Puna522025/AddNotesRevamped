package com.addnotes.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import com.addnotes.adapter.ShoppingListData
import com.addnotes.add_notes_revamped_ui.R

class ShoppingListItemDetailsDialog(
    context: Context,
    private val dialogListener: DialogSelectionListener,
    private val shoppingListArray: ArrayList<ShoppingListData>,
    private val position: Int
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.shopping_extra_param)

        val et_value = findViewById<View>(R.id.etValue) as EditText
        val et_count = findViewById<View>(R.id.etCount) as EditText

        et_value.setText(shoppingListArray.get(position).value)
        et_count.setText(shoppingListArray.get(position).countOfItemsToBuy)
        findViewById<View>(R.id.CancelExtraParams).setOnClickListener { dismiss() }
        findViewById<View>(R.id.saveExtraParams).setOnClickListener {
            shoppingListArray.get(position).value = et_value.text.toString()
            shoppingListArray.get(position).countOfItemsToBuy = et_count.text.toString()
            dialogListener.onDialogInputSelected(0)
        }
        show()
    }
}