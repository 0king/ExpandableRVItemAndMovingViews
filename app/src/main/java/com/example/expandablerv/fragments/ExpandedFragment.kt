package com.example.expandablerv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.expandablerv.R

class ExpandedFragment: Fragment() {

    lateinit var box1: View
    lateinit var box2: View
    lateinit var box3: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_expanded, container, false)
        box1 = rootView.findViewById(R.id.box1)
        box2 = rootView.findViewById(R.id.box2)
        box3 = rootView.findViewById(R.id.box3)


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // View is created so postpone the transition
        //postponeEnterTransition()

        /*viewModel.liveList.observe(this) {
            controller.setList(it)

            // Data is loaded so lets wait for our parent to be drawn
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                // Parent has been drawn. Start transitioning!
                startPostponedEnterTransition()
            }
        }*/
    }

    fun collapse(fragmentManager: FragmentManager, f:CollapsedFragment){
        //val f = CollapsedFragment()
        fragmentManager.beginTransaction()
                //enter, exit
            //.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_out)
            .addSharedElement(box1, ViewCompat.getTransitionName(box1)?:"box1")
            .addSharedElement(box2, ViewCompat.getTransitionName(box2)?:"box2")
            .addSharedElement(box3, ViewCompat.getTransitionName(box3)?:"box3")
            .replace(R.id.fragmentContainer, f)
            .commit()
    }
}