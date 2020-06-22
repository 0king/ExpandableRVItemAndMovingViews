package com.example.expandablerv.anim;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.animation.Interpolator;

public class SlideInDownAnimator extends BaseItemAnimator {

    public SlideInDownAnimator() {

    }

    public SlideInDownAnimator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    @Override protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationY(-holder.itemView.getHeight())
                .alpha(0)
                .setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start();
    }

    @Override protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationY(holder.itemView, -holder.itemView.getHeight());
        ViewCompat.setAlpha(holder.itemView, 0);
    }

    @Override protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationY(0)
                .alpha(1)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start();
    }
}