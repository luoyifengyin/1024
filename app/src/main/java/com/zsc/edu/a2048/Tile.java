package com.zsc.edu.a2048;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

/**
 * Created by 飘 on 2018/6/24.
 */

public class Tile extends android.support.v7.widget.AppCompatTextView {

    private float x = 0;

    private float y = 0;

    public boolean crashed = false;

    public Tile(Context context) {
        super(context);
    }
    public Tile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Tile(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLevel(int level) {
        getBackground().setLevel(level);
        int id = getResources().getIdentifier("t"+level,"style",getContext().getPackageName());
        if (Build.VERSION.SDK_INT < 23) {
            super.setTextAppearance(getContext(), id);
        } else {
            super.setTextAppearance(id);
        }
        setText(String.valueOf(level));
    }

    public int levelUp(){
        int level = getBackground().getLevel() << 1;
        String text = String.valueOf(level);
        Animation animation = new ScaleAnimation(1f,1.1f,1f,1.1f,x+getWidth()/2,y+getHeight()/2);
        animation.setDuration(50);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(1);
        setLevel(level);
        startAnimation(animation);
        return level;
    }

    public ObjectAnimator translateX(int offset){
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,"translationX",x, x + offset*getWidth());
        x += offset*getWidth();
        return animator;
    }

    public ObjectAnimator translateY(int offset){
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,"translationY",y, y + offset*getHeight());
        y += offset*getHeight();
        return animator;
    }
}
