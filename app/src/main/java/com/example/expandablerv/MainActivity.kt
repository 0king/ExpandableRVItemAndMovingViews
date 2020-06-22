package com.example.expandablerv

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.transition.*
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expandablerv.fragments.CollapsedFragment
import com.example.expandablerv.fragments.ExpandedFragment

class MainActivity : AppCompatActivity() {

    //1. expand CL from h1 to full
    //2. fragments
    //3. dynamic RV item insertion
    //4. ELL - work in CL
    //5. alternative to ELL
    //6. RL spread, then get TV height


    lateinit var recyclerView: RecyclerView

    var expanded = false
    val f1 = CollapsedFragment()
    val f2 = ExpandedFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRV()
    }

    fun usingFragments(){
        //load
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, f1)
            .commit()

        // Define the shared element transition.

        // Define the shared element transition.
        f1.setSharedElementEnterTransition(DetailsTransition())
        f2.setSharedElementEnterTransition(DetailsTransition())
        f1.setSharedElementReturnTransition(DetailsTransition())
        f2.setSharedElementReturnTransition(DetailsTransition())

        // The rest of the views are just fading in/out.

        // The rest of the views are just fading in/out.
        f1.setEnterTransition(Fade())
        f2.setEnterTransition(Fade())
        f1.setExitTransition(Fade())
        if (Build.VERSION.SDK_INT >= 21) f2.setExitTransition(Slide(Gravity.TOP))

        val textView = findViewById<TextView>(R.id.tvClick)
        textView.setOnClickListener{
            if (expanded) collapse()
            else expand()
        }
    }

    fun collapse(){
        expanded = false
        f2.collapse(supportFragmentManager, f1)
    }

    fun expand(){
        expanded = true
        f1.expand(supportFragmentManager, f2)
    }

    fun initRV(){
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = RVAdapter(pop())
    }

    fun pop() : ArrayList<String>{
        val l = ArrayList<String>()
        l.add("A")
        l.add("B")
        l.add("A")
        l.add("B")
        for (i in 0..2) l.add("A")
        for (i in 0..2) l.add("B")
        for (i in 0..2) l.add("A")
        for (i in 0..2) l.add("B")
        return l
    }

    fun log(msg: String) = Log.d("durga", "MA: $msg")

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class DetailsTransition : TransitionSet() {
        init {
            ordering = ORDERING_TOGETHER
            addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
        }
    }

}