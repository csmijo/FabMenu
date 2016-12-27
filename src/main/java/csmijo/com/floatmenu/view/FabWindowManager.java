package csmijo.com.floatmenu.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * Created by chengqianqian-xy on 2016/12/26.
 */

public class FabWindowManager {

    private WindowManager.LayoutParams mParams;
    private Context mContext;
    private WindowManager mWindowManager;
    private FabMenu mFabMenu;
    private FabManager mFabManager;


    public FabWindowManager(Context context, FabManager fabManager) {

        this.mContext = context;
        this.mFabManager = fabManager;
        this.mFabMenu = new FabMenu(context);
        this.mFabMenu.setOnTouchListener(getOnTouchListener());
        init();
        this.mWindowManager.addView(mFabMenu, mParams);
    }

    private void init() {
        this.mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;  // 类型

        // 设置flag
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        mParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 透明，如果不设置这个，弹出框的透明遮罩显示为黑色
        mParams.format = PixelFormat.TRANSLUCENT;

        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.x = 0;
        mParams.y = 100;
    }

    public WindowManager.LayoutParams getParams() {
        return mParams;
    }

    public FabMenu getFabMenu() {
        return mFabMenu;
    }

    public FabManager getFabManager() {
        return mFabManager;
    }
    float x1 = 0.0f;
    float y1 = 0.0f;
    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("FabWindowManager", "onTouch: switch outer "+x1+";"+y1);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        Log.i("FabWindowManager", "onTouch: action_down "+x1+";"+y1);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x2 = event.getRawX();
                        float y2 = event.getRawY() - getStatusBarHeight();

                        Log.i("FabWindowManager", "onTouch: action_move "+x1+";"+y1);
                        Log.i("FabWindowManager", "onTouch: action_move "+x2+";"+y2);
                        // 改变位置
                        mParams.x = (int) (x2 - x1);
                        mParams.y = (int) (y2 - y1);
                        mWindowManager.updateViewLayout(mFabMenu, mParams);

                        break;
                    case MotionEvent.ACTION_UP:
                        // menu靠边
                        float x3 = event.getRawX();
                        Log.i("FabWindowManager", "onTouch: action_up "+x3);
                        if (x3 > getScreenWidth() / 2) {
                            // 靠右
                            mFabMenu.setExpandDir(1);
                            mParams.x = getScreenWidth() - mFabMenu.getWidth();
                        } else {
                            mFabMenu.setExpandDir(0);
                            mParams.x = 0;
                        }
                        mWindowManager.updateViewLayout(mFabMenu,mParams);
                        break;
                }

                return true;
            }
        };
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
}
