package com.juansandoval.sandovalportfolio.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.juansandoval.sandovalportfolio.ui.fragments.*

class SectionPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return UsersFragment()
            1 -> return WorkFragment()
            2 -> return SkillsFragment()
            3 -> return AboutFragment()
            4 -> return ContactFragment()
        }
        return null!!
    }
}