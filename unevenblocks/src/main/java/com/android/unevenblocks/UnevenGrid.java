package com.android.unevenblocks;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

/**
 * Created by yura on 06.05.14.
 */
public class UnevenGrid extends ViewGroup {
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;

    public static final int ALGIMENT_LEFT = 1;
    public static final int ALGIMENT_WRAP_WIDTH = 2;
    private static final String LOG_TAG = "UnevenGrid";
    int orientation = ORIENTATION_HORIZONTAL;
    private Adapter adapter;
    private int algiment;
    private BlockLineManager blockLineManager;

    public UnevenGrid(Context context) {
        this(context,null);
    }

    public UnevenGrid(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public UnevenGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.UnevenGrid, defStyle, 0);
        algiment = a.getInt(R.styleable.UnevenGrid_alignment,1);
        blockLineManager = new BlockLineManager(this);
    }

    public int getAlgiment() {
        return algiment;
    }

    public void setAligment(int alig){
        algiment = alig;
        blockLineManager.UpdateAligment(alig);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        blockLineManager.UpdateView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = getChildCount();

        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);

            if (child.getVisibility() != GONE) {

                child.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                child.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
                measureChild(child, widthMeasureSpec, heightMeasureSpec);

                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }
        blockLineManager.UpdateView();
        int w = blockLineManager.getWidth();
        if (w == 0)
            w = widthMeasureSpec;
        int h = blockLineManager.getHeight();
        if (h == 0)
            h = heightMeasureSpec;
        if(getChildCount() == 0)
            h =0;
        Log.i(LOG_TAG, "measure size = (" + w + ", " + h + ")");
        setMeasuredDimension(w, h);
    }

    public void MeasureChild(View child,int width,int height)
    {
        measureChild(child,width,height);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //adapter
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                UpdateAdapterData();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                UpdateAdapterData();
            }
        });
        UpdateAdapterData();
    }

    public Adapter getAdapter() {
        return adapter;
    }

    private void UpdateAdapterData() {
        int elementCount = adapter.getCount();
        removeAllViews();
        for (int i = 0; i < elementCount; i++) {
            super.addView(adapter.getView(i, null, this));
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //layout params
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public int getOrientation() {
        return orientation;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
