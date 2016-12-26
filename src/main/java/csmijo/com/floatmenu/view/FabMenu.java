package csmijo.com.floatmenu.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.Iterator;

import csmijo.com.floatmenu.R;

/**
 * Created by chengqianqian-xy on 2016/12/21.
 */

public class FabMenu extends ViewGroup {

    protected int menuBaseSize;
    protected int menuItemSpacing;
    protected int menuItemOvershot;

    protected int expandDir;  // {1:悬浮窗靠桌面右边,0:悬浮窗靠桌面左边}
    protected ArrayList<View> mViewList;

    private int expectedWidth;    // 期望的width
    private int expectedHeight;    // 期望的height

    protected boolean mExpandStatus;     // true:launch;false:fold


    public FabMenu(Context context) {
        this(context, null);
    }

    public FabMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.expandDir = 0;
        this.mViewList = new ArrayList<>();
        if (!isInEditMode()) {
            menuBaseSize = getResources().getDimensionPixelSize(R.dimen.btg_fab_menu_base_size);
            menuItemSpacing = getResources().getDimensionPixelOffset(R.dimen.btg_fab_menu_item_spacing);
            menuItemOvershot = getResources().getDimensionPixelOffset(R.dimen.btg_fab_menu_item_overshoot);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = menuBaseSize + menuItemOvershot;
        Iterator<View> it = mViewList.iterator();
        int measuredHeight = 0;
        int measuredWidth = width;
        while (it.hasNext()) {
            View view = it.next();
            measuredWidth += (view.getMeasuredWidth() + menuItemSpacing);
            measuredHeight = Math.max(measuredHeight, view.getMeasuredHeight());
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        if (this.expandDir == 1) {
            //悬浮窗靠桌面的右边
            int left = measuredWidth - menuBaseSize;
            Iterator<View> it = this.mViewList.iterator();
            while (it.hasNext()) {
                View view = it.next();
                int measuredWidth2 = view.getMeasuredWidth();
                int measuredHeight2 = view.getMeasuredHeight();
                int top = (measuredHeight - measuredHeight2) / 2;  //上下间距相同
                view.layout(left, top, left + measuredWidth2, top + measuredHeight2);
            }
        } else if (this.expandDir == 0) {
            //悬浮窗靠桌面的左边
            Iterator<View> it = this.mViewList.iterator();
            while (it.hasNext()) {
                View view = it.next();
                int measuredWidth2 = view.getMeasuredWidth();
                int measuredHeight2 = view.getMeasuredHeight();
                int top = (measuredHeight - measuredHeight2) / 2;
                view.layout(0, top, measuredWidth2, top + measuredHeight2);
            }
        }

    }


    public int getExpandDir() {
        return expandDir;
    }

    // 设置展开方向
    public void setExpandDir(int i) {
        this.expandDir = i;
        requestLayout();
    }

    // 添加view
    public void addMenuItem(View view, LayoutParams layoutParams) {
        this.mViewList.add(view);
        addView(view, layoutParams);
        reEstimateSize();
    }

    // 获取第i个View
    public View getMenuItem(int index) {
        if (index < 0 || index >= this.mViewList.size()) {
            return null;
        }
        return this.mViewList.get(index);
    }

    public void removeAllItems() {
        this.mViewList.clear();
        removeAllViews();
    }

    // 处于折叠状态，进行展开操作
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void launch() {
        if (!this.mExpandStatus) {
            int i;
            View view;
            if (this.expandDir == 1) {
                i = 0;
                int size = mViewList.size();
                for (int index = 0; index < size - 1; index++) {
                    view = mViewList.get(index);
                    i -= (menuItemSpacing + view.getMeasuredWidth());
                    // 平移动画
                    ObjectAnimator transAnim = ObjectAnimator.ofFloat(view, "translationX", 0.0f, (float) i);
                    transAnim.setDuration(200);
                    transAnim.setInterpolator(new OvershootInterpolator());
                    transAnim.start();

                    // 旋转动画
                    ObjectAnimator rotaAnim = ObjectAnimator.ofFloat(view, "rotation", 0.0f, -360.0f);
                    rotaAnim.setDuration(100);
                    rotaAnim.start();
                }

                //最后一个view只有旋转动画
                view = mViewList.get(size - 1);
                ObjectAnimator rotaAnim = ObjectAnimator.ofFloat(view, "rotation", 0.0f, -45.0f);
                rotaAnim.setDuration(100);
                rotaAnim.start();

            } else if (this.expandDir == 0) {
                i = menuBaseSize + menuItemSpacing;
                int size = mViewList.size();
                for (int index = 0; index < size - 1; index++) {
                    view = mViewList.get(index);
                    // 平移动画
                    ObjectAnimator transAnim = ObjectAnimator.ofFloat(view, "translationX", 0.0f, (float) i);
                    transAnim.setDuration(200);
                    transAnim.setInterpolator(new OvershootInterpolator());
                    transAnim.start();

                    // 旋转动画
                    ObjectAnimator rotaAnim = ObjectAnimator.ofFloat(view, "rotation", 0.0f, 360.0f);
                    rotaAnim.setDuration(100);
                    rotaAnim.start();

                    i += (view.getMeasuredWidth() + menuItemSpacing);
                }

                //最后一个view只有旋转动画
                view = mViewList.get(size - 1);
                ObjectAnimator rotaAnim = ObjectAnimator.ofFloat(view, "rotation", 0.0f, 45.0f);
                rotaAnim.setDuration(100);
                rotaAnim.start();
            }
            this.mExpandStatus = true;
        }
    }


    // 处于展开状态，进行折叠操作
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void fold() {
        if (this.mExpandStatus) {
            Iterator<View> it = this.mViewList.iterator();
            while (it.hasNext()) {
                View view = it.next();
                // 旋转动画
                ObjectAnimator rotaAnim = ObjectAnimator.ofFloat(view, "rotation", 0.0f);
                rotaAnim.setDuration(100);
                rotaAnim.start();

                // 平移动画
                ObjectAnimator transAnim = ObjectAnimator.ofFloat(view, "translationX", 0.0f);
                transAnim.setDuration(100);
                transAnim.setInterpolator(new OvershootInterpolator());
                transAnim.setStartDelay(100);
                transAnim.start();
            }
            this.mExpandStatus = false;
        }
    }


    // 计算此时ViewGroup的expectedWidth，expectedHeight
    private void reEstimateSize() {
        this.expectedWidth = this.menuBaseSize + this.menuItemOvershot;
        int makeWidthMeasuerSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int makeHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        Iterator<View> it = this.mViewList.iterator();
        while (it.hasNext()) {
            View view = it.next();
            measureChild(view, makeWidthMeasuerSpec, makeHeightMeasureSpec);
            this.expectedWidth += view.getMeasuredWidth() + this.menuItemSpacing;
            this.expectedHeight = Math.max(this.expectedHeight, view.getMeasuredHeight());
        }
    }

    public int getExpectedHeight() {
        return this.expectedHeight;
    }

    public int getExpectedWidth() {
        return this.expectedWidth;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
