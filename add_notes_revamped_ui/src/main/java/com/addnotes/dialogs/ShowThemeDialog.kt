package com.addnotes.dialogs

import android.app.Dialog
import android.content.Context
import android.transition.AutoTransition
import android.view.View
import android.view.Window
import android.widget.ImageView
import com.addnotes.add_notes_revamped_ui.R
import com.bumptech.glide.Glide

class ShowThemeDialog(context: Context, private val selectedListener: DialogSelectionListener) :
    Dialog(context), View.OnClickListener {

    init {
        window?.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window?.exitTransition = AutoTransition()
        window?.enterTransition = AutoTransition()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.background_color_change)

        Glide.with(context)
            .load(R.drawable.deadpool_cc)
            .fitCenter()
            .into(findViewById<View>(R.id.imgDeadpool) as ImageView)

        Glide.with(context)
            .load(R.drawable.xa_c)
            .fitCenter()
            .into(findViewById<View>(R.id.imgBatmanNeon) as ImageView)

        Glide.with(context)
            .load(R.drawable.joker_c)
            .fitCenter()
            .into(findViewById<View>(R.id.imgJoker) as ImageView)


        Glide.with(context)
            .load(R.drawable.batman_logo)
            .fitCenter()
            .into(findViewById<View>(R.id.imgBatmanLogo) as ImageView)

        Glide.with(context)
            .load(R.drawable.hulk)
            .fitCenter()
            .into(findViewById<View>(R.id.imgHulk) as ImageView)

        Glide.with(context)
            .load(R.drawable.super_man_logo)
            .fitCenter()
            .into(findViewById<View>(R.id.imgSuperman) as ImageView)

        Glide.with(context)
            .load(R.drawable.hellokitty)
            .fitCenter()
            .into(findViewById<View>(R.id.imgKitty) as ImageView)

        Glide.with(context)
            .load(R.drawable.minion2)
            .fitCenter()
            .into(findViewById<View>(R.id.imgMinion) as ImageView)

        Glide.with(context)
            .load(R.drawable.iron_man)
            .fitCenter()
            .into(findViewById<View>(R.id.imgIronMan) as ImageView)

        Glide.with(context)
            .load(R.drawable.cap_america)
            .fitCenter()
            .into(findViewById<View>(R.id.imgCapAmerica) as ImageView)

        findViewById<View>(R.id.rlRedYellow).setOnClickListener(this)
        findViewById<View>(R.id.rlPinkYellow).setOnClickListener(this)
        findViewById<View>(R.id.rlPurpleGreen).setOnClickListener(this)
        findViewById<View>(R.id.rlOrangRed).setOnClickListener(this)
        findViewById<View>(R.id.rlGreenBrown).setOnClickListener(this)
        findViewById<View>(R.id.rlblue).setOnClickListener(this)
        findViewById<View>(R.id.rlOrange).setOnClickListener(this)
        findViewById<View>(R.id.rlPink_helloKitty).setOnClickListener(this)
        findViewById<View>(R.id.rlDeepPurple).setOnClickListener(this)
        findViewById<View>(R.id.rlBlackRed).setOnClickListener(this)
        findViewById<View>(R.id.rlNeonBlue).setOnClickListener(this)
        findViewById<View>(R.id.rlWhiteJoker).setOnClickListener(this)
        show()
    }

    override fun onClick(view: View?) {
        selectedListener.onDialogInputSelected(view?.id!!)
    }
}