package com.zsc.edu.a2048.SquareLayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;

/**
 * Created by 落忆枫音 on 2018/6/24.
 */

public class SquareGridLayout extends GridLayout {
    public SquareGridLayout(@NonNull Context context) {
        super(context);
    }

    public SquareGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
