package com.addnotes.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.addnotes.add_notes_revamped_ui.R
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

object ThemesEngine {
    public fun updateThemeColors(
        context: Context,
        themeColor: String?,
        toolbar: Toolbar?,
        saveButton: FloatingActionButton?,
        window: Window?,
        mainOrNot: Boolean,
        fabAction: FloatingActionButton?,
        fabAdd: FloatingActionButton?,
        fabDelete: FloatingActionButton?,
        fabShare: FloatingActionButton?,
        imageBackground: ImageView?,
        rlBackgroundNote: RelativeLayout?
    ) {

        val toolBarColor: Int
        val statusBarColor: Int
        val accentColor: Int
        val fab2Color: Int
        val fab3Color: Int
        val fab4Color: Int
        val imageResourceId: Int
        var backgroundNote: Drawable? = null
        var isNoBackgroundThemeSelected = false

        if (mainOrNot) {
            imageBackground?.visibility = View.VISIBLE
        }

        when (themeColor) {
            // SUPER MAN
            context.getString(R.string.orangeRed) -> {
                toolBarColor = R.color.OrangeRedToolBar
                statusBarColor = R.color.OrangeRedStatusBar
                accentColor = R.color.OrangeRedAccent
                fab2Color = R.color.OrangeRedFab2
                fab3Color = R.color.OrangeRedFab3
                fab4Color = R.color.OrangeRedFab4
                imageResourceId = R.drawable.super_man_logo
                backgroundNote = ContextCompat.getDrawable(context, R.drawable.app_bg3_c)
            }
            // IRON MAN
            context.getString(R.string.redYellow) -> {
                toolBarColor = R.color.redYellowToolBar
                statusBarColor = R.color.redYellowStatusBar
                accentColor = R.color.redYellowAccent
                fab2Color = R.color.redYellowFab2
                fab3Color = R.color.redYellowFab3
                fab4Color = R.color.redYellowFab4
                imageResourceId = R.drawable.iron_man
                backgroundNote = ContextCompat.getDrawable(context, R.drawable.app_bg3_c)
            }
            //WHITE JOKER
            context.getString(R.string.whiteJoker) -> {
                toolBarColor = R.color.WhiteJokerToolBar
                statusBarColor = R.color.WhiteJokerStatusBar
                accentColor = R.color.WhiteJokerAccent
                fab2Color = R.color.WhiteJokerFab2
                fab3Color = R.color.WhiteJokerFab3
                fab4Color = R.color.WhiteJokerFab4
                imageResourceId = R.drawable.joker_c
                isNoBackgroundThemeSelected = true
                imageBackground?.let {
                    Glide.with(context)
                        .load(imageResourceId)
                        .centerCrop()
                        .into(it)
                }
                rlBackgroundNote?.setBackgroundColor(
                    Color.parseColor("#EDEDED")
                )
            }
            // DEADPOOL
            context.getString(R.string.blackRed) -> {
                toolBarColor = R.color.BlackRedToolBar
                statusBarColor = R.color.BlackRedStatusBar
                accentColor = R.color.BlackRedAccent
                fab2Color = R.color.BlackRedFab2
                fab3Color = R.color.BlackRedFab3
                fab4Color = R.color.BlackRedFab4
                imageResourceId = R.drawable.deadpool_cc
                isNoBackgroundThemeSelected = true
                imageBackground?.let {
                    Glide.with(context)
                        .load(imageResourceId)
                        .centerCrop()
                        .into(it)
                }
                rlBackgroundNote?.setBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.black)
                )
            }
            // NEON BATMAN
            context.getString(R.string.neonBlue) -> {
                toolBarColor = R.color.NeonBlueToolBar
                statusBarColor = R.color.NeonBlueStatusBar
                accentColor = R.color.NeonBlueAccent
                fab2Color = R.color.NeonBlueFab2
                fab3Color = R.color.NeonBlueFab3
                fab4Color = R.color.NeonBlueFab4
                imageResourceId = R.drawable.xa_c
                imageBackground?.let {
                    Glide.with(context)
                        .load(imageResourceId)
                        .centerCrop()
                        .into(it)
                }
                rlBackgroundNote?.setBackgroundColor(
                    Color.parseColor("#120F20")
                )
            }
            // HULK
            context.getString(R.string.purpleGreen) -> {
                toolBarColor = R.color.PurpleGreenToolBar
                statusBarColor = R.color.PurpleGreenStatusBar
                accentColor = R.color.PurpleGreenAccent
                fab2Color = R.color.PurpleGreenFab2
                fab3Color = R.color.PurpleGreenFab3
                fab4Color = R.color.PurpleGreenFab4
                imageResourceId = R.drawable.hulk
                backgroundNote = ContextCompat.getDrawable(context, R.drawable.app_bg3_c)
            }
            // CAPTAIN AMERICA
            context.getString(R.string.pinkYellow) -> {
                toolBarColor = R.color.PinkYellowToolBar
                statusBarColor = R.color.PinkYellowStatusBar
                accentColor = R.color.PinkYellowAccent
                fab2Color = R.color.PinkYellowFab2
                fab3Color = R.color.PinkYellowFab3
                fab4Color = R.color.PinkYellowFab4
                imageResourceId = R.drawable.cap_america
                backgroundNote = ContextCompat.getDrawable(context, R.drawable.app_bg3_c)
            }
            // BATMAN
            context.getString(R.string.greenBrown) -> {
                toolBarColor = R.color.GreenBrownToolBar
                statusBarColor = R.color.GreenBrownStatusBar
                accentColor = R.color.GreenBrownAccent
                fab2Color = R.color.GreenBrownFab2
                fab3Color = R.color.GreenBrownFab3
                fab4Color = R.color.GreenBrownFab4
                imageResourceId = R.drawable.batman_logo
                backgroundNote = ContextCompat.getDrawable(context, R.drawable.back3_c)
            }
            // MINION
            context.getString(R.string.blue) -> {
                toolBarColor = R.color.BlueToolBar
                statusBarColor = R.color.BlueStatusBar
                accentColor = R.color.BlueAccent
                fab2Color = R.color.BlueFab2
                fab3Color = R.color.BlueFab3
                fab4Color = R.color.BlueFab4
                imageResourceId = R.drawable.minion2
                backgroundNote = ContextCompat.getDrawable(context, R.drawable.app_bg3_c)
            }
            // HELLO KITTY
            context.getString(R.string.pink_hello_kitty) -> {
                toolBarColor = R.color.PinkKittyToolBar
                statusBarColor = R.color.PinkKittyStatusBar
                accentColor = R.color.PinkKittyAccent
                fab2Color = R.color.PinkKittyFab2
                fab3Color = R.color.PinkKittyFab3
                fab4Color = R.color.PinkKittyFab4
                imageResourceId = R.drawable.hellokitty
                backgroundNote = ContextCompat.getDrawable(context, R.drawable.app_bg3_c)
            }
            // ORANGE
            context.getString(R.string.orange) -> {
                toolBarColor = R.color.OrangeToolBar
                statusBarColor = R.color.OrangeStatusBar
                accentColor = R.color.OrangeAccent
                fab2Color = R.color.OrangeFab2
                fab3Color = R.color.OrangeFab3
                fab4Color = R.color.OrangeFab4
                imageResourceId = R.drawable.hellokitty
                isNoBackgroundThemeSelected = true
                if (mainOrNot) {
                    imageBackground?.visibility = View.GONE
                }
                rlBackgroundNote?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.white
                    )
                )
            }
            // DEEP PURPLE
            else -> {
                toolBarColor = R.color.DeepPurpleToolBar
                statusBarColor = R.color.DeepPurpleStatusBar
                accentColor = R.color.DeepPurpleAccent
                fab2Color = R.color.DeepPurpleFab2
                fab3Color = R.color.DeepPurpleFab3
                fab4Color = R.color.DeepPurpleFab4
                imageResourceId = R.drawable.hellokitty
                isNoBackgroundThemeSelected = true
                rlBackgroundNote?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.white
                    )
                )
                if (mainOrNot) {
                    imageBackground?.visibility = View.GONE
                }
            }
        }
        changeTheme(
            context,
            toolBarColor,
            statusBarColor,
            accentColor,
            fab2Color,
            fab3Color,
            fab4Color,
            toolbar,
            saveButton,
            window,
            mainOrNot,
            fabAction,
            fabAdd,
            fabDelete,
            fabShare
        )
        if (mainOrNot && !isNoBackgroundThemeSelected) {
            //imageBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.hellokitty));
            imageBackground?.let {
                Glide.with(context)
                    .load(imageResourceId)
                    .centerCrop()
                    .into(it)
            }
            rlBackgroundNote?.background = backgroundNote
        }
    }

    public fun changeTheme(
        context: Context,
        toolbarColor: Int,
        statusBar: Int,
        accent: Int,
        fab2: Int,
        fab3: Int,
        fab4: Int,
        toolbar: Toolbar?,
        saveButton: FloatingActionButton?,
        window: Window?,
        mainOrNot: Boolean,
        fabAction: FloatingActionButton?,
        fabAdd: FloatingActionButton?,
        fabDelete: FloatingActionButton?,
        fabShare: FloatingActionButton?
    ) {
        toolbar?.setBackgroundColor(ContextCompat.getColor(context, toolbarColor))
        window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = ContextCompat.getColor(context, statusBar)
        if (!mainOrNot) {
            saveButton?.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, accent))
        } else {
            fabAction?.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, accent))
            fabAdd?.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, fab2))
            fabDelete?.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, fab3))
            fabShare?.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, fab4))
        }
    }

}