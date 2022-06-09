package com.addnotes.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.addnotes.add_notes_revamped_ui.R

class ViewPagerAdapter(fm: FragmentManager, private val context: Context) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT),
    OnPageChangeListener {
    var currentPage = 0
    var fragmentsAttached = mutableListOf<Class<*>>()

    override fun getCount(): Int {
        return fragmentsAttached.size
    }

    override fun getItem(position: Int): Fragment {
        try {
            return (fragmentsAttached[position].newInstance() as Fragment)
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        throw Exception()
    }

    fun setFragments(fragments: MutableList<Class<*>>) {
        fragmentsAttached = fragments
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        TODO("Not yet implemented")
    }

    override fun onPageSelected(newPageIndex: Int) {
        currentPage = newPageIndex
    }

    override fun onPageScrollStateChanged(state: Int) {
        TODO("Not yet implemented")
    }

    fun drawPageSelectionIndicators(
        viewPagerCountDots: LinearLayout,
        list: MutableList<Class<*>>,
        mPosition: Int
    ) {
        viewPagerCountDots.removeAllViews()
        val dots = arrayOfNulls<ImageView>(list.size)
        for (i in list.indices) {
            dots[i] = ImageView(context)
            if (i == mPosition) {
                dots[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.item_selected
                    )
                )
            } else {
                dots[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.item_unselected
                    )
                )
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(15, 0, 15, 0)
            viewPagerCountDots.addView(dots[i], params)
        }
    }
}