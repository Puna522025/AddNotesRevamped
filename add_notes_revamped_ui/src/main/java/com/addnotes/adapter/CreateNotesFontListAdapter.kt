package com.addnotes.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.addnotes.add_notes_revamped_ui.R

class CreateNotesFontListAdapter(
    val context: Context,
    val fontList: ArrayList<String>,
    val selection: String,
    val myClickListener: AdapterClickListener
) : RecyclerView.Adapter<CreateNotesFontListAdapter.CustomViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomViewHolder {
        val itemView: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.row, viewGroup, false)
        return CustomViewHolder(itemView, myClickListener)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (!selection.equals("TYPE", ignoreCase = true)) {
            val type =
                Typeface.createFromAsset(context.assets, "fonts/" + fontList.get(position))
            holder.text.typeface = type
            if (this.selection.equals(fontList.get(position), ignoreCase = true)) {
                holder.tick.visibility = View.VISIBLE
            } else {
                holder.tick.visibility = View.GONE
            }
            val animation: Animation = if (position % 2 == 0) {
                AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
            } else {
                AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            }
            holder.card1.startAnimation(animation)
        } else {
            holder.text.setText(fontList.get(position))
        }
    }

    override fun getItemCount(): Int {
        return fontList.size
    }

    class CustomViewHolder(view: View, private val myClickListener: AdapterClickListener) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var text: TextView
        var tick: ImageView
        var card1: CardView
        override fun onClick(v: View) {
            myClickListener.onItemClick(position, v)
        }

        init {
            view.setOnClickListener(this)
            text = view.findViewById<View>(R.id.textFont) as TextView
            tick = view.findViewById<View>(R.id.tick) as ImageView
            card1 = view.findViewById<View>(R.id.card1) as CardView
        }
    }
}