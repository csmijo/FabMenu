package csmijo.com.floatmenu.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import java.lang.reflect.Field;
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
    protected int TOUCHSLOP; // 判断移动的最小距离
    private Context mContext;

    private float x1 = 0.0f;
    private float y1 = 0.0f;


    public FabMenu(Context context) {
        this(context, null);
    }

    public FabMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.expandDir = 0;
        this.mViewList = new ArrayList<>();
        this.TOUCHSLOP = ViewConfiguration.get(context).getScaledTouchSlop();
        if (!isInEditMode()) {
            menuBaseSize = getResources().getDimensionPixelSize(R.dimen.btg_fab_menu_base_size);
            menuItemSpacing = getResources().getDimensionPixelOffset(R.dimen.btg_fab_menu_item_spacing);
            menuItemOvershot = getResources().getDimensionPixelOffset(R.dimen.btg_fab_menu_item_overshoot);
        }
        this.setBackgroundColor(Color.RED);

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
        int action = ev.getAction();
        boolean consumeValue = false;
       // Log.i("FabMenu", "onInterceptTouchEvent: switch outer "+x1+";"+y1);

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getRawX();
                y1 = ev.getRawY() - getStatusBarHeight();
               // Log.i("FabMenu", "onInterceptTouchEvent: action_down "+x1+";"+y1);
                break;
            case MotionEvent.ACTION_MOVE:
                float x2 = ev.getRawX();
                float y2 = ev.getRawY() - getStatusBarHeight();
//                Log.i("FabMenu", "onInterceptTouchEvent: action_move "+x1+";"+y1);
//                Log.i("FabMenu", "onInterceptTouchEvent: action_move "+x2+";"+y2);
                if (Math.abs(x2 - x1) > TOUCHSLOP || Math.abs(y2 - y1) > TOUCHSLOP) {
                    consumeValue = true;        // 滑动距离足够大，则截获touch事件，不让子view处理
                    //Log.i("FabMenu", "onInterceptTouchEvent: action_move consume "+TOUCHSLOP);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return consumeValue;
    }

    private int getStatusBarHeight() {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = mContext.getResources().getDimensionPixelSize(x);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return statusBarHeight;
    }
}
