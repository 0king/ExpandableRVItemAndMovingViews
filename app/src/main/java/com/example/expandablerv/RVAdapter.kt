package com.example.expandablerv

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseBooleanArray
import android.util.TypedValue
import android.view.*
import android.view.View.*
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.expandablerv.expandablelayout.ExpandableLayoutListenerAdapter
import com.example.expandablerv.expandablelayout.ExpandableLinearLayout
import kotlin.math.ceil


class RVAdapter(var itemList: ArrayList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val expandState = SparseBooleanArray()
    init {
        for (i in itemList.indices) {
            expandState.append(i, false)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(itemList.get(position)){
            "A","a" -> 0
            "B","b" -> 1
            else -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            0 -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_rel, parent, false)
                return ItemHolder1(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item2, parent, false)
                return ItemHolder2(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val type = holder.itemViewType
        when(type){
            0 ->{
                val h = holder as ItemHolder1
                h.expandableLayout.setInRecyclerView(true)
                h.expandableLayout.setExpanded(expandState[position])
                h.expandableLayout.setInterpolator(DecelerateInterpolator())
                h.expandableLayout.setListener(object : ExpandableLayoutListenerAdapter() {
                    override fun onPreOpen() {
                        createRotateAnimator(holder.btn, 0f, 180f)?.start()
                        h.bind(position)
                        expandState.put(position, true)
                    }

                    override fun onPreClose() {
                        createRotateAnimator(holder.btn, 180f, 0f)?.start()
                        h.bind(position)
                        expandState.put(position, false)
                    }

                    override fun onOpened() {
                        //h.row1.visibility = VISIBLE
                        //h.row1.alpha = 1.0F
                        toggleViewVisibility(h.row1, position)
                    }

                    override fun onClosed() {
                        //h.row1.visibility = INVISIBLE
                        //h.row1.alpha = 0.0F
                        toggleViewVisibility(h.row1, position)
                    }
                })

                toggleViewVisibility(h.row1, position)

                //toggle box position
                if (expandState[position]){
                    //h.layoutTheBoxes()
                    h.translateBoxes()
                }
                else
                    h.layoutTheBoxes()

                h.btn.setOnClickListener{
                    h.expandableLayout.toggle()
                    //h.f1()
                }
            }
        }
    }

    fun toggleViewVisibility(view: View, position: Int){
        if (expandState[position])
            view.visibility = VISIBLE
        else
            view.visibility = INVISIBLE
    }


    inner class ItemHolder1(private val view: View) : RecyclerView.ViewHolder(view) {

        var originalHeight = 0
        lateinit var btn: View //arrow rotating btn
        var box1: View
        var box2: View
        var box3: View
        var row2: View
        var row1: View
        var expandableLayout : ExpandableLinearLayout
        var box1_H = 0
        var boxW = dpToPx(80)
        var box1_x = -1
        var box2_x = -1
        //var box1_diff = 0F
        var rl: RelativeLayout

        init {
            box1 = itemView.findViewById(R.id.box0)
            box2 = itemView.findViewById(R.id.box2)
            box3 = itemView.findViewById(R.id.box3)
            row2 = itemView.findViewById(R.id.row2)
            row1 = itemView.findViewById(R.id.row1)
            btn = itemView.findViewById(R.id.box1)
            expandableLayout = itemView.findViewById(R.id.ell)
            rl = itemView.findViewById(R.id.parentContainer)

            layoutTheBoxes()
        }

        fun layoutTheBoxes(){
            val w = screenWidth()
            val leftMargin1 = (w/2 - dpToPx(40))/2 - dpToPx(40)
            val lp1 = box1.layoutParams as RelativeLayout.LayoutParams
            lp1.leftMargin = leftMargin1
            box1.layoutParams = lp1

            val leftMargin2 = w/2 - dpToPx(40)
            val lp2 = box2.layoutParams as RelativeLayout.LayoutParams
            lp2.leftMargin = leftMargin2
            box2.layoutParams = lp2

            val rightMargin = (w/2 - dpToPx(40))/2 - dpToPx(40)
            val lp3 = box3.layoutParams as RelativeLayout.LayoutParams
            lp3.rightMargin = rightMargin
            box3.layoutParams = lp3
        }

        fun boundEL(){ //todo: OR give margin
            //if (row1.isVisible)
            val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT)
            var lp2 = rl.layoutParams
        }

        fun bind(position: Int){
            box1.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            box1_H = box1.measuredHeight

            if (expandState[position]){ //in expanded
                resetBox(box1)
                resetBox(box2)
                resetBox(box3)
                //notifyItemChanged(position)
            }else{ //in collapsed state
                //move box1
                animateBox1()
                animateBox2()
                animateBox3()
            }
        }

        fun translateBoxes(){
            /*box1.viewTreeObserver.addOnGlobalLayoutListener {
                box1.viewTreeObserver.removeOnGlobalLayoutListener { this }
                translateBox1()
            }
            box2.viewTreeObserver.addOnGlobalLayoutListener {
                box2.viewTreeObserver.removeOnGlobalLayoutListener { this }
                translateBox2()
            }
            box3.viewTreeObserver.addOnGlobalLayoutListener {
                box3.viewTreeObserver.removeOnGlobalLayoutListener { this }
                translateBox3()
            }*/
            itemView.doOnLayout {
                translateBox1()
                translateBox2()
                translateBox3()
            }
        }
        fun translateBox1(){
            val dx = dpToPx(10) - box1.absX()
            box1.translationX = dx.toFloat()
            box1.translationY = 0F
        }
        fun translateBox2(){
            val dx = dpToPx(10) - box2.absX()
            box2.translationX = dx.toFloat()
            val widthMS = MeasureSpec.makeMeasureSpec(itemView.width, MeasureSpec.EXACTLY)
            val heightMS = MeasureSpec.makeMeasureSpec(itemView.height, MeasureSpec.AT_MOST)
            row1.measure(widthMS, heightMS)
            val row1Height = row1.measuredHeight
            val dy = row1Height + dpToPx(10)
            box2.translationY = dy.toFloat()
        }
        fun translateBox3(){
            val dx = dpToPx(10) - box3.absX()
            box3.translationX = dx.toFloat()
            //dy = row1.height + row2.height + margin x 2
            val widthMS = MeasureSpec.makeMeasureSpec(itemView.width, MeasureSpec.EXACTLY)
            val heightMS = MeasureSpec.makeMeasureSpec(itemView.height, MeasureSpec.AT_MOST)
            row1.measure(widthMS, heightMS)
            row2.measure(widthMS, heightMS)
            val row1Height = row1.measuredHeight
            val row2Height = row2.measuredHeight
            val dy = row1Height + row2Height + dpToPx(20)
            box3.translationY = dy.toFloat()
        }

        fun animateBox1(){
            //check if extra space in left side
            //if yes, then move to left
            box1.viewTreeObserver.addOnGlobalLayoutListener {
                if (box1_x > -1) {
                    box1.viewTreeObserver.removeOnGlobalLayoutListener { this }
                    return@addOnGlobalLayoutListener
                }
                box1_x = box1.absX()
                doWork1()
            }
            if (box1_x > -1) doWork1()
        }

        fun doWork1(){
            //log("box0_x = $box0_x")
            //val dx = 1F * (dpToPx(10) - box1_x)
            //log("box0_diff = $box0_diff")
            if (box1_x > dpToPx(10)){
                //move
                val dx = 1F * (dpToPx(10) - box1_x)
                ObjectAnimator.ofFloat(box1, "translationX", dx).apply {
                    duration = 300
                    start()
                }
            }
        }

        fun animateBox2(){
            //move X
            //dx = dpToPx(10) - box2_x

            box2.viewTreeObserver.addOnGlobalLayoutListener {
                if (box2_x > -1) {
                    box2.viewTreeObserver.removeOnGlobalLayoutListener { this }
                    return@addOnGlobalLayoutListener
                }
                box2_x = box2.absX()
                doWork2X()
            }
            if (box2_x > -1) doWork2X()

            //  dy = max(row1.imageView, row1.textView) + margin
            //=> dy = row1.height + margin
            val widthMS = MeasureSpec.makeMeasureSpec(itemView.width, MeasureSpec.EXACTLY)
            val heightMS = MeasureSpec.makeMeasureSpec(itemView.height, MeasureSpec.AT_MOST)
            //row1.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            row1.measure(widthMS, heightMS)
            val row1Height = row1.measuredHeight
            //log("row1height: px = $row1Height")
            //log("row1height: dp = ${pxToDp(row1Height)}")
            val dy = row1Height + dpToPx(10)
            //log("dy:Int = $dy")
            //log("dy:Float = ${dy.toFloat()}")
            doWork2Y(dy.toFloat())
        }

        fun doWork2X(){
            val dx = 1F * (dpToPx(10) - box2_x)
            ObjectAnimator.ofFloat(box2, "translationX", dx).apply {
                duration = 300
                start()
            }
        }

        fun doWork2Y(dy: Float){
            ObjectAnimator.ofFloat(box2, "translationY", dy).apply {
                duration = 300
                start()
            }
        }

        fun animateBox3(){
            //dx = dpToPx(10) - box3.absolutePosition
            val dx = dpToPx(10) - box3.absX()
            ObjectAnimator.ofFloat(box3, "translationX", dx.toFloat()).apply {
                duration = 300
                start()
            }
            //dy = row1.height + row2.height + margin x 2
            val widthMS = MeasureSpec.makeMeasureSpec(itemView.width, MeasureSpec.EXACTLY)
            val heightMS = MeasureSpec.makeMeasureSpec(itemView.height, MeasureSpec.AT_MOST)
            row1.measure(widthMS, heightMS)
            row2.measure(widthMS, heightMS)
            val row1Height = row1.measuredHeight
            val row2Height = row2.measuredHeight
            val dy = row1Height + row2Height + dpToPx(20)
            ObjectAnimator.ofFloat(box3, "translationY", dy.toFloat()).apply {
                duration = 300
                start()
            }
        }

        fun resetBox(view: View){
            ObjectAnimator.ofFloat(view, "translationX", 0F).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(view, "translationY", 0F).apply {
                duration = 300
                start()
            }
        }

        fun bind2(){
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            /*itemView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))*/
            //val root = itemView.findViewById<View>(R.id.container)
            //root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

            originalHeight = itemView.measuredHeight
            //log("Measured height = $height")

            itemView.viewTreeObserver.addOnGlobalLayoutListener {
                if (originalHeight>0) {
                    itemView.viewTreeObserver.removeOnGlobalLayoutListener {this}
                    //return@addOnGlobalLayoutListener
                }
                //height = itemView.measuredHeight
                //log("height@ $adapterPosition = $height")
            }

            //another way
            itemView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

            view.setOnClickListener{
                //expandableLayout.toggle()
                //animate2(itemView, originalHeight, originalHeight+10)

                if (itemList[adapterPosition] == "A")
                {
                    //log("Collapsed state")
                    //itemList[adapterPosition] = "a"
                    //animate2(itemView, 0, 200)
                    //animate(box1)
                }
                else
                {
                    //log("Expanded state")
                    //itemList[adapterPosition] = "A"
                    //animate2(itemView, 200, 0)
                }
            }
        }

        fun f1() = animate(box1)

        fun animate(view: View){
            ObjectAnimator.ofFloat(view, "translationY", 200f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(view, "translationX", 200f).apply {
                duration = 300
                start()
            }
        }

        private fun screenWidth(): Int {
            //return Resources.getSystem().displayMetrics.widthPixels
            val displayMetrics = DisplayMetrics()
            val windowManager = itemView.context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val defaultDisplay = windowManager.defaultDisplay
            defaultDisplay.getRealMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

        fun animate2(view:View, from:Int, to:Int){
            //log("from = $from , to = $to")
            val animator = ValueAnimator.ofInt(from, to).apply {
                duration = 1000
                addUpdateListener {
                    val params = view.layoutParams
                    val inc = it.animatedValue as Int
                    //log("animated value = $inc")
                    //log("Params height = ${params.height}")
                    //log("originalHeight = $originalHeight")
                    params.height = originalHeight + inc
                    //log("onInc: Params height = ${params.height}")
                    view.layoutParams = params

                    //notifyItemChanged(adapterPosition)
                }
                addListener(object: Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        //notifyItemChanged(adapterPosition)
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }

                })
            }
            animator.start()
        }

    }

    fun createRotateAnimator(
        target: View?,
        from: Float,
        to: Float
    ): ObjectAnimator? {
        val animator = ObjectAnimator.ofFloat(target, "rotation", from, to)
        animator.duration = 300
        //animator.interpolator = Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR)
        return animator
    }


    fun dpToPx(dp: Int): Int = ceil(dp * Resources.getSystem().displayMetrics.density).toInt()
    fun pxToDp(px: Int) = ceil(px / Resources.getSystem().displayMetrics.density)


    class ItemHolder2(view: View) : RecyclerView.ViewHolder(view)

    interface Listener{ fun onClick()}

    fun log(msg: String) = Log.d("durga", "RVA: $msg")

    fun getHeight(
        context: Context?,
        text: String?,
        textSize: Int,
        deviceWidth: Int
    ): Int {
        val textView = TextView(context)
        textView.text = text
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth, MeasureSpec.AT_MOST)
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        textView.measure(widthMeasureSpec, heightMeasureSpec)
        return textView.measuredHeight
    }

    fun View.absX(): Int
    {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        return location[0]
        /*//For coordinates location relative to the screen/display
anyView.getGlobalVisibleRect(rectf);

Log.d("WIDTH        :", String.valueOf(rectf.width()));
Log.d("HEIGHT       :", String.valueOf(rectf.height()));
Log.d("left         :", String.valueOf(rectf.left));
Log.d("right        :", String.valueOf(rectf.right));
Log.d("top          :", String.valueOf(rectf.top));
Log.d("bottom       :", String.valueOf(rectf.bottom));*/
    }

    fun View.absY(): Int
    {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        return location[1]
    }

}