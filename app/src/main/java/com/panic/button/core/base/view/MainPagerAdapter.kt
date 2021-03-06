package com.panic.button.core.base.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainPagerAdapter internal constructor(fm: FragmentManager, var fragments: List<Fragment>, var titles: List<String>? = null) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (titles?.size == fragments.size) {
            titles?.get(position)
        } else {
            super.getPageTitle(position)
        }
    }
}