package com.infikaa.indibubble.utility;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Sohan on 23-Apr-17.
 */

public class RecyclerAnimationUtil {
    public static void animate(RecyclerView.ViewHolder holder, boolean goesDown) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView, "translationY", goesDown ? 200 : -200, 0);
        animator.setDuration(500);
        animatorSet.playSequentially(animator);
        animator.start();
    }
}
