package com.android.unevenblocks;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.util.ArrayList;

/**
 * Created by yura on 06.05.14.
 */
public class UnevenGrid extends ViewGroup {
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;

    public static final int ALGIMENT_LEFT = 1;
    public static final int ALGIMENT_WRAP_WIDTH = 2;
    int orientation = ORIENTATION_HORIZONTAL;
    private Adapter adapter;
    private int algiment;

    public UnevenGrid(Context context) {
        super(context);
    }

    public UnevenGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        ShowAttribute(attrs);
    }

    public UnevenGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ShowAttribute(attrs);
    }

    private void ShowAttribute(AttributeSet attrs) {
        this.algiment = attrs.getAttributeIntValue(R.styleable.UnevenGrid_alignment, 1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ArrayList<View> viewLine = new ArrayList<View>();
        Size parentSize = getParentSize();
        int StartPos = 0;//start position of the line
        int childrenCount = getChildCount();
        int elementsLength = 0;
        for (int i = 0; i < childrenCount; i++) {
            View v = getChildAt(i);
            if (v.getVisibility() == GONE)
                continue;
            Size viewSize = GetVieWSize(v);
            if (viewLine.size() == 0) {
                viewLine.add(v);
                elementsLength = viewSize.DynamicSideSize;
                continue;
            }
            if (viewSize.DynamicSideSize + elementsLength < parentSize.StaticSideSize) {
                viewLine.add(v);
                elementsLength += viewSize.DynamicSideSize;
            } else {
                ShowLine(elementsLength, StartPos, viewLine);
                StartPos += getMaxStaticSize(viewLine);
                viewLine.clear();
                elementsLength = 0;
                i--;
            }
        }
        if (viewLine.size() != 0) {
            ShowLine(elementsLength, StartPos, viewLine);
        }

    }

    private int getMaxStaticSize(ArrayList<View> viewLine) {
        if (viewLine.size() == 0)
            return 0;
        int result = GetVieWSize(viewLine.get(0)).StaticSideSize;
        for (int i = 1; i < viewLine.size(); i++) {
            int staticSize = GetVieWSize(viewLine.get(i)).StaticSideSize;
            if (staticSize > result)
                result = staticSize;
        }
        return result;
    }

    private void ShowLine(int elementsLength, int startPos, ArrayList<View> viewLine) {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        Size parentSize = getParentSize();
        int fullLinePut = 0;
        int additionalSize = 0;
        if (algiment == ALGIMENT_WRAP_WIDTH)
            additionalSize = (parentSize.StaticSideSize - elementsLength) / viewLine.size();
        for (int i = 0; i < viewLine.size(); i++) {
            View v = viewLine.get(i);
            if (orientation == ORIENTATION_HORIZONTAL) {
                left = fullLinePut;
                right = left + additionalSize + v.getMeasuredWidth();
                fullLinePut = right;
                top = startPos;
                bottom = startPos + v.getMeasuredHeight();
                v.getLayoutParams().width = LayoutParams.MATCH_PARENT;
            } else {
                left = startPos;
                right = left + v.getMeasuredWidth();
                top = fullLinePut;
                bottom = top + additionalSize + v.getMeasuredHeight();
                fullLinePut = bottom;
                v.getLayoutParams().height = LayoutParams.MATCH_PARENT;
            }
            v.layout(left, top, right, bottom);
        }
    }

    private Size getParentSize() {
        Size result = new Size();
        if (orientation == ORIENTATION_VERTICAL) {
            result.DynamicSideSize = getWidth();
            result.StaticSideSize = getHeight();
        } else {
            result.DynamicSideSize = getHeight();
            result.StaticSideSize = getWidth();
        }
        return result;
    }

    private Size GetVieWSize(View v) {
        Size result = new Size();
        if (orientation == ORIENTATION_HORIZONTAL) {
            result.DynamicSideSize = v.getMeasuredWidth();
            result.StaticSideSize = v.getMeasuredHeight();
        } else {
            result.DynamicSideSize = v.getMeasuredHeight();
            result.StaticSideSize = v.getMeasuredWidth();
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // Measure the child.
                child.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                child.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
