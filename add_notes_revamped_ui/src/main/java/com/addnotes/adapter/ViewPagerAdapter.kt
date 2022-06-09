package com.addnotes.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import java.lang.Exception

class ViewPagerAdapter(fm: FragmentManager) :
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

}