package com.juansandoval.sandovalportfolio.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.juansandoval.sandovalportfolio.ui.fragments.*

class SectionPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return AboutFragment()
            1 -> return WorkFragment()
            2 -> return SkillsFragment()
            3 -> return UsersFragment()
        }
        @Suppress("UNREACHABLE_CODE")
        return null!!
    }
}