package com.example.letschat.home.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentsAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle

) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragments = ArrayList<Fragment>()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment){
        fragments.add(fragment)
    }

}