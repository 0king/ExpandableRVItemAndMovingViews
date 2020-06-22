package com.example.expandablerv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.expandablerv.R

class CollapsedFragment: Fragment() {

    lateinit var box1: View
    lateinit var box2: View
    lateinit var box3: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_collapsed, container, false)
        box1 = rootView.findViewById(R.id.box1)
        box2 = rootView.findViewById(R.id.box2)
        box3 = rootView.findViewById(R.id.box3)
        return rootView
    }

    fun expand(fm: FragmentManager, f:ExpandedFragment){
        //val f = ExpandedFragment()
        fm.beginTransaction()
            .addSharedElement(box1, ViewCompat.getTransitionName(box1)?:"box1")
            .addSharedElement(box2, ViewCompat.getTransitionName(box2)?:"box2")
            .addSharedElement(box3, ViewCompat.getTransitionName(box3)?:"box3")
            .replace(R.id.fragmentContainer, f)
            .commit()
    }

}