package csmijo.com.floatmenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;

/**
 * Created by chengqianqian-xy on 2016/12/21.
 */

public class FabMenuApi9 extends FabMenu {
    public FabMenuApi9(Context context) {
        super(context);
    }

    public FabMenuApi9(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 处于折叠状态，进行展开操作
    public void launch() {
        if (!this.mExpandStatus) {
            this.mExpandStatus = true;
        }
    }

    // 处于展开状态，进行折叠操作
    public void fold() {
        if (this.mExpandStatus) {
            this.mExpandStatus = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = menuBaseSize + menuItemOvershot;
        Iterator<View> it = this.mViewList.iterator();
        int measuredHeight = 0;
        int measuredWidth = width;
        while (it.hasNext()) {
            View view = it.next();
            measuredWidth += view.getMeasuredWidth() + menuItemSpacing;
            measuredHeight = Math.max(measuredHeight, view.getMeasuredHeight());
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();

        Iterator<View> it;
        int left;
        if (this.expandDir == 1) {
            measuredWidth -= menuBaseSize;
            it = mViewList.iterator();
            left = measuredWidth;
            while (it.hasNext()) {
                View view = it.next();
                int measuredWith2 = view.getMeasuredWidth();
                int measuredHeight2 = view.getMeasuredHeight();
                int top = (measuredHeight - measuredHeight2) / 2;
                view.layout(left - measuredWith2, top, left, top + measuredHeight2);
                left -= (menuItemSpacing + measuredWith2);
            }
        } else if (this.expandDir == 0) {
            it = mViewList.iterator();
            left = 0;
            while (it.hasNext()) {
                View view = it.next();
                int measuredWidth2 = view.getMeasuredWidth();
                int measuredHeight2 = view.getMeasuredHeight();
                int top = (measuredHeight - measuredHeight2) / 2;
                view.layout(left, top, left + measuredWidth2, top + measuredHeight2);
                left += (menuItemSpacing + measuredWidth2);
            }
        }
    }
}
