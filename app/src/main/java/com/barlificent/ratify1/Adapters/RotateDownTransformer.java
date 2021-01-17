package com.barlificent.ratify1.Adapters;

import android.view.View;

/**
 * Created by 2015 on 1/19/2018.
 */

public class RotateDownTransformer extends BaseTransformer {

    //    @Override
//    protected void onTransform(View view, float position) {
//        view.setTranslationX(position < 0 ? 0f : -view.getWidth() * position);
//    }
//    @Override
//    protected void onTransform(View view, float position) {
//        final float height = view.getHeight();
//        final float width = view.getWidth();
//        final float scale = min(position < 0 ? 1f : Math.abs(1f - position), 0.8f);
//
//        view.setScaleX(scale);
//        view.setScaleY(scale);
//        view.setPivotX(width * 0.5f);
//        view.setPivotY(height * 0.5f);
//        view.setTranslationX(position < 0 ? width * position : -width * position * 0.25f);
//    }
//
//    private static final float min(float val, float min) {
//        return val < min ? min : val;
//    }
    private static final float ROT_MOD = -15f;

    @Override
    protected void onTransform(View view, float position) {
        final float width = view.getWidth();
        final float height = view.getHeight();
        final float rotation = ROT_MOD * position * -1.25f;

        view.setPivotX(width * 0.5f);
        view.setPivotY(height);
        view.setRotation(rotation);
    }

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }

}
