package csmijo.com.floatmenu.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

import csmijo.com.floatmenu.R;

/**
 * Created by chengqianqian-xy on 2016/12/26.
 */

public class FabWindowManager {
    private Context mContext;
    private WindowManager mWindowManager;
    private FabMenu mFabMenu;
    private FabManager mFabManager;

    private float x1 = 0.0f;
    private float y1 = 0.0f;
    private float x2;
    private float y3;
    private float x3;
    private long startTime;


    private View mFabBgView;  // 透明背景
    private RelativeLayout mFabActionRl;
    private ImageView mFabBtn;
    private ProgressBar mFabPrg;

    private int dir; // {0：靠左；1：靠右}
    private static int TOUCHSLOP;
    private static int FABACTIONSIZE;
    private static int FABACTIONOFFSET;
    private static int CLICKSLOP;


    public FabWindowManager(Context context, FabManager fabManager) {

        this.mContext = context;
        this.mFabManager = fabManager;
        this.mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        TOUCHSLOP = ViewConfiguration.get(mContext).getScaledTouchSlop();
        CLICKSLOP = ViewConfiguration.getTapTimeout();
        FABACTIONSIZE = mContext.getResources().getDimensionPixelOffset(R.dimen.btg_fab_action_size);
        FABACTIONOFFSET = mContext.getResources().getDimensionPixelOffset(R.dimen.btg_fab_action_offset);
    }

    public void load() {
        loadFabBg(true);
        loadFabMenu(true);
        loadFabAction(true);
        updateFabMenuPosition();
    }

    // 加载透明背景
    private void loadFabBg(boolean flag) {
        if (flag || mFabBgView == null) {
            this.mFabBgView = ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.btg_view_fab_bg, null);
            this.mFabBgView.findViewById(R.id.closeTrigger).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFabManager.onClose();
                }
            });

            final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    40, PixelFormat.TRANSLUCENT);
            this.mFabBgView.setVisibility(View.GONE);
            mWindowManager.addView(mFabBgView, layoutParams);
        }
    }

    // 加载fabAction
    private void loadFabAction(boolean flag) {
        if (flag || this.mFabActionRl == null) {
            this.mFabActionRl = (RelativeLayout) ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.btg_view_fab_action, null);
            this.mFabActionRl.setVisibility(View.VISIBLE);
            this.mFabActionRl.setOnTouchListener(getOnTouchListener());

            this.mFabBtn = (ImageView) this.mFabActionRl.findViewById(R.id.fabBtnImgView);
            this.mFabPrg = (ProgressBar) this.mFabActionRl.findViewById(R.id.progressBar);


            int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            Log.i("FabWindowManager", "loadFabAction: flags = " + flags);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, flags,
                    PixelFormat.TRANSLUCENT);
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            layoutParams.x = 0;
            layoutParams.y = 100;
            this.dir = 0;

            this.mWindowManager.addView(mFabActionRl, layoutParams);
        }
    }

    // 加载FabMenu
    private void loadFabMenu(boolean flag) {
        if (flag || this.mFabMenu == null) {
            if (Build.VERSION.SDK_INT <= 11) {
                this.mFabMenu = new FabMenuApi9(mContext);
            } else {
                this.mFabMenu = new FabMenu(mContext);
            }

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 40, PixelFormat.TRANSLUCENT);

            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            this.mFabMenu.setVisibility(View.GONE);
            this.mWindowManager.addView(this.mFabMenu, layoutParams);
            this.mFabManager.createMenuOne();
        }
    }


    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFabActionRl.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x2 = event.getRawX();
                        float y2 = event.getRawY() - getStatusBarHeight();
                        x3 = event.getX();
                        y3 = event.getY();

                        Log.i("FabWindowManager", "onTouch: x2 = " + x2 + "; y2 = " + y2);
                        // 改变位置
                        if (!mFabManager.isOpen()) {
                            updatePosition((int) (x2 - x1), (int) (y2 - y1), layoutParams);
                            break;
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        if (!mFabManager.isOpen()) {
                            updatePositionAndDir((int) x2, layoutParams);
                        }

                        if (Math.abs(x3 - x1) < TOUCHSLOP && Math.abs(y3 - y1) < TOUCHSLOP
                                && (System.currentTimeMillis() - startTime) < CLICKSLOP) {
                            //点击
                            mFabManager.toggle();
                        }
                        break;
                }

                return true;
            }
        };
    }

    private void updatePositionAndDir(int x, WindowManager.LayoutParams params) {
        Log.i("FabWindowManager", "updatePositionAndDir: x = " + x + "; screenWidth = " + getScreenWidth());
        if (x < (getScreenWidth() / 2)) {
            // 靠左
         /*   params.x = 0;
            mWindowManager.updateViewLayout(mFabActionRl, params);
            dir = 0;
            updateFabMenuPosition();*/

            new MoveLeftTrail(params.x, 5, params).start();
        } else {
           /* params.x = getScreenWidth() - FABACTIONSIZE;
            mWindowManager.updateViewLayout(mFabActionRl, params);
            dir = 1;
            updateFabMenuPosition();*/
            // bug!!!
            new MoveRightTrail(params.x - (getScreenWidth() - FABACTIONSIZE), 5, params).start();
        }
    }


    // 更新fabAction，FabMenu的位置
    private void updatePosition(int dx, int dy, WindowManager.LayoutParams params) {
        params.x = dx;
        params.y = dy;
        this.mWindowManager.updateViewLayout(this.mFabActionRl, params);  // 更新fabAction的位置
        updateFabMenuPosition();
    }

    // 更新FabMenu的位置
    private void updateFabMenuPosition() {
        WindowManager.LayoutParams fabActionRl_lp = (WindowManager.LayoutParams) this.mFabActionRl.getLayoutParams();
        WindowManager.LayoutParams fabMenu_lp = (WindowManager.LayoutParams) this.mFabMenu.getLayoutParams();

        int x1 = fabActionRl_lp.x;
        int y1 = fabActionRl_lp.y;
        int measuredWidth = this.mFabMenu.getMeasuredWidth() != 0 ? this.mFabMenu.getMeasuredWidth() :
                this.mFabMenu.getExpectedWidth();
        int measuredHeight = this.mFabMenu.getMeasuredHeight() != 0 ? this.mFabMenu.getMeasuredHeight() :
                this.mFabMenu.getExpectedHeight();

        fabMenu_lp.x = this.dir == 0 ? -FABACTIONOFFSET : (x1 - measuredWidth) + FABACTIONSIZE;
        fabMenu_lp.y = y1 + (FABACTIONSIZE - measuredHeight) / 2;

        this.mFabMenu.setExpandDir(this.dir);
        this.mWindowManager.updateViewLayout(this.mFabMenu, fabMenu_lp);
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

    private int getScreenWidth() {
        return mWindowManager.getDefaultDisplay().getWidth();
    }


    public FabMenu getFabMenu() {
        return mFabMenu;
    }

    public View getFabBgView() {
        return mFabBgView;
    }

    public ImageView getFabBtn() {
        return mFabBtn;
    }

    public ProgressBar getFabPrg() {
        return mFabPrg;
    }

    public int getDir() {
        return dir;
    }


    private class MoveLeftTrail extends CountDownTimer {

        WindowManager.LayoutParams mLayoutParams;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MoveLeftTrail(long millisInFuture, long countDownInterval, WindowManager.LayoutParams layoutParams) {
            super(millisInFuture, countDownInterval);
            this.mLayoutParams = layoutParams;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            this.mLayoutParams.x = (int) millisUntilFinished;
            mWindowManager.updateViewLayout(mFabActionRl, this.mLayoutParams);
        }

        @Override
        public void onFinish() {
            this.mLayoutParams.x = 0;
            mWindowManager.updateViewLayout(mFabActionRl, this.mLayoutParams);
            dir = 0;
            updateFabMenuPosition();  //
        }
    }

    private class MoveRightTrail extends CountDownTimer {

        WindowManager.LayoutParams mLayoutParams;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MoveRightTrail(long millisInFuture, long countDownInterval, WindowManager.LayoutParams layoutParams) {
            super(millisInFuture, countDownInterval);
            this.mLayoutParams = layoutParams;
        }


        @Override
        public void onTick(long millisUntilFinished) {
            this.mLayoutParams.x = (int) millisUntilFinished;
            mWindowManager.updateViewLayout(mFabActionRl, mLayoutParams);
        }

        @Override
        public void onFinish() {
            this.mLayoutParams.x = getScreenWidth() - FABACTIONSIZE;
            mWindowManager.updateViewLayout(mFabActionRl, mLayoutParams);
            dir = 1;
            updateFabMenuPosition();
        }
    }
}
