package com.barlificent.ratify1.Adapters;

/**
 * Created by 2015 on 1/12/2018.
 */

import android.support.v4.view.ViewPager;
import android.view.View;

public class FlipHorizontalTransformer implements ViewPager.PageTransformer {


    @Override
    public void transformPage(View view, float position) {
        final float rotation = 180f * position;

        view.setVisibility(rotation > 90f || rotation < -90f ? View.INVISIBLE : View.VISIBLE);
        view.setPivotX(view.getWidth() * 0.5f);
        view.setPivotY(view.getHeight() * 0.5f);
        view.setRotationY(rotation);
    }
}
