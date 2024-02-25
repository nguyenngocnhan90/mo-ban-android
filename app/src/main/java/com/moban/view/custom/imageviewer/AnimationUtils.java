package com.moban.view.custom.imageviewer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by LenVo on 6/12/18.
 */

final class AnimationUtils {
    private AnimationUtils() {
        throw new AssertionError();
    }

    static void animateVisibility(final View view) {
        final boolean isVisible = view.getVisibility() == View.VISIBLE;
        float from = isVisible ? 1.0f : 0.0f,
                to = isVisible ? 0.0f : 1.0f;

        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "alpha", from, to);
        animation.setDuration(ViewConfiguration.getDoubleTapTimeout());

        if (isVisible) {
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });
        } else view.setVisibility(View.VISIBLE);

        animation.start();
    }

}

