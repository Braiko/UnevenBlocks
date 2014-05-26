package com.android.unevenblocks;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by yura on 14.05.14.
 */
public class BlockLineManager {
    private final UnevenGrid parent;
    private int algiment;
    private int orientation;
    private int height;
    private int width;
//    private ArrayList<ArrayList<View>> viewLinesList = new ArrayList<ArrayList<View>>();

    public BlockLineManager(UnevenGrid parent) {
        this.parent = parent;
        this.algiment = parent.getAlgiment();
        this.orientation = parent.getOrientation();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void UpdateView() {
        ArrayList<View> viewLine = new ArrayList<View>();
        Size parentSize = getParentSize();
        int startPos = 0;//start position of the line
        int childrenCount = parent.getChildCount();
        int elementsLength = 0;
        for (int i = 0; i < childrenCount; i++) {
            View v = parent.getChildAt(i);
            if (v.getVisibility() == View.GONE)
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
                ShowLine(elementsLength, startPos, viewLine);
                startPos += getMaxStaticSize(viewLine);
                viewLine.clear();
                elementsLength = 0;
                i--;
            }
        }
        if (viewLine.size() != 0) {
            ShowLine(elementsLength, startPos, viewLine);
            startPos += getMaxStaticSize(viewLine);
            UpdateSizeOfFieldForBlock(startPos);
        }
    }

    private void UpdateSizeOfFieldForBlock(int endOfLines) {
        if (orientation == UnevenGrid.ORIENTATION_HORIZONTAL) {
            this.height = endOfLines;
            this.width = parent.getMeasuredWidth();
        } else {
            this.width = endOfLines;
            this.height = parent.getMeasuredHeight();
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
        if (algiment == UnevenGrid.ALGIMENT_WRAP_WIDTH)
            additionalSize = (parentSize.StaticSideSize - elementsLength) / viewLine.size();
        for (int i = 0; i < viewLine.size(); i++) {
            View v = viewLine.get(i);
            if (orientation == UnevenGrid.ORIENTATION_HORIZONTAL) {
                left = fullLinePut;
                right = left + additionalSize + v.getMeasuredWidth();
                fullLinePut = right;
                top = startPos;
                bottom = startPos + v.getMeasuredHeight();
                v.getLayoutParams().width = UnevenGrid.LayoutParams.MATCH_PARENT;
            } else {
                left = startPos;
                right = left + v.getMeasuredWidth();
                top = fullLinePut;
                bottom = top + additionalSize + v.getMeasuredHeight();
                fullLinePut = bottom;
                v.getLayoutParams().height = UnevenGrid.LayoutParams.MATCH_PARENT;
            }
            v.layout(left, top, right, bottom);
            parent.MeasureChild(v,right-left,bottom-top);
        }
    }

    private Size getParentSize() {
        Size result = new Size();
        if (orientation == UnevenGrid.ORIENTATION_VERTICAL) {
            result.DynamicSideSize = parent.getMeasuredWidth();
            result.StaticSideSize = parent.getMeasuredHeight();
        } else {
            result.DynamicSideSize = parent.getMeasuredHeight();
            result.StaticSideSize = parent.getMeasuredWidth();

        }
        return result;
    }

    private Size GetVieWSize(View v) {
        Size result = new Size();
        if (orientation == UnevenGrid.ORIENTATION_HORIZONTAL) {
            result.DynamicSideSize = v.getMeasuredWidth();
            result.StaticSideSize = v.getMeasuredHeight();
        } else {
            result.DynamicSideSize = v.getMeasuredHeight();
            result.StaticSideSize = v.getMeasuredWidth();
        }
        return result;
    }

    public void UpdateAligment(int alig) {
        this.algiment = alig;
    }
}
