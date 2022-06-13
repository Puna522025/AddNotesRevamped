package com.addnotes.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.utils.StringUtilities

class ShoppingListAdapter(
    val context: Context, val shoppingList: ArrayList<ShoppingListData>,
    private val myClickListener: AdapterClickListener,
    private val myCheckedChangeListener: MyCheckedChangeListener
) :
    RecyclerView.Adapter<ShoppingListAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ShoppingListAdapter.CustomViewHolder {
        val itemView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_shopping_list, viewGroup, false)
        return ShoppingListAdapter.CustomViewHolder(
            itemView,
            myClickListener,
            myCheckedChangeListener
        )
    }

    class CustomViewHolder(
        view: View, private val myClickListener: AdapterClickListener,
        private val myCheckedChangeListener: MyCheckedChangeListener
    ) : RecyclerView.ViewHolder(view),
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        var value: TextView
        var itemToBuy: TextView
        var count: TextView
        var totalValue: TextView
        var isDone: SwitchCompat
        var crossLine: ImageView
        var imgDeleteNote: ImageView
        var rlExtraShoppingParams: RelativeLayout
        override fun onClick(v: View) {
            myClickListener.onItemClick(adapterPosition, v)
        }

        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            myCheckedChangeListener.onCheckedChanged(
                adapterPosition,
                buttonView,
                isChecked
            )
        }

        init {
            view.setOnClickListener(this)
            value = view.findViewById<View>(R.id.value) as TextView
            itemToBuy = view.findViewById<View>(R.id.itemToBuy) as TextView
            count = view.findViewById<View>(R.id.count) as TextView
            totalValue = view.findViewById<View>(R.id.totalValueofItems) as TextView
            rlExtraShoppingParams =
                view.findViewById<View>(R.id.rlExtraShoppingParams) as RelativeLayout
            isDone = view.findViewById<View>(R.id.isDone) as SwitchCompat
            isDone.tag = adapterPosition
            crossLine = view.findViewById<View>(R.id.crossLine) as ImageView
            imgDeleteNote = view.findViewById<View>(R.id.imgDeleteNote) as ImageView
            isDone.setOnCheckedChangeListener(this)
            imgDeleteNote.setOnClickListener(this)
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (shoppingList != null && shoppingList.size > 0) {

            holder.itemToBuy.text = shoppingList[position].itemToBuy
            holder.totalValue.text = StringUtilities.EMPTY_STRING

            if (!TextUtils.isEmpty(shoppingList[position].value)
                || !TextUtils.isEmpty(shoppingList[position].countOfItemsToBuy)
            ) {
                holder.rlExtraShoppingParams.visibility = View.VISIBLE

                holder.value.text =
                    context.getString(R.string.shopping_value) + shoppingList[position].value
                holder.count.text =
                    context.getString(R.string.shopping_count) + shoppingList[position].countOfItemsToBuy

                if (!TextUtils.isEmpty(shoppingList[position].value) &&
                    !TextUtils.isEmpty(shoppingList[position].countOfItemsToBuy)
                ) {
                    val totalValue: Int = shoppingList[position].value
                        .toInt() * shoppingList[position].countOfItemsToBuy.toInt()
                    holder.totalValue.text =
                        context.getString(R.string.shopping_amount) + totalValue.toString()
                }
            } else {
                holder.rlExtraShoppingParams.visibility = View.GONE
                holder.value.text = context.getString(R.string.shopping_value)
                holder.count.text = context.getString(R.string.shopping_count)
            }
            if (shoppingList[position].doneOrNot.equals("yes", true)) {
                holder.isDone.isChecked = false
                holder.crossLine.visibility = View.VISIBLE
            } else if (shoppingList[position].doneOrNot.equals("no", true)) {
                holder.isDone.isChecked = true
                holder.crossLine.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return shoppingList.size
    }

    interface MyCheckedChangeListener {
        fun onCheckedChanged(position: Int, buttonView: CompoundButton?, isChecked: Boolean)
    }
}