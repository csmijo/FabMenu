package csmijo.com.floatmenu.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import csmijo.com.floatmenu.R;

/**
 * Created by chengqianqian-xy on 2016/12/26.
 */

public class FabManager {


    private int itemSize;
    private Context mContext;
    private int mode;
    private FabWindowManager mFabWindowManager;
    private boolean isOpen;  // menu是否展开

    private Handler mHandler;

    public FabManager(Context context) {
        this.mContext = context;
        this.itemSize = mContext.getResources().getDimensionPixelSize(R.dimen.btg_fab_menu_item_size);
        this.mFabWindowManager = new FabWindowManager(context, this);
        this.mHandler = new Handler();

    }


    // 创建菜单一：fabactionImgView，reportImgView，userImgView
    public void createMenuOne() {
        if (this.mFabWindowManager.getFabBtn() != null) {
            this.mFabWindowManager.getFabBtn().setImageResource(R.drawable.btg_btn_fab);
        }

        this.mFabWindowManager.getFabMenu().removeAllItems();

        ImageView reportImgView = new ImageView(mContext);
        reportImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        reportImgView.setImageResource(R.drawable.btg_btn_report);
        reportImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "reportImagView", Toast.LENGTH_SHORT).show();
                //切换
                createMenuTwo();
            }
        });
        this.mFabWindowManager.getFabMenu().addMenuItem(reportImgView, new ViewGroup.LayoutParams(itemSize, itemSize));

        ImageView userImgView = new ImageView(mContext);
        userImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        userImgView.setImageResource(R.drawable.btg_btn_user);
        userImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "userImagView", Toast.LENGTH_SHORT).show();
            }
        });
        this.mFabWindowManager.getFabMenu().addMenuItem(userImgView, new ViewGroup.LayoutParams(itemSize, itemSize));
        mode = 0;
    }

    // 创建菜单二：fabaction2，tickImgView，crossImgView
    public void createMenuTwo() {
        if (this.mFabWindowManager.getFabBtn() != null) {
            this.mFabWindowManager.getFabBtn().setImageResource(R.drawable.btg_btn_publish);
        }

        this.mFabWindowManager.getFabMenu().removeAllItems();

        ImageView tickImgView = new ImageView(mContext);
        tickImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        tickImgView.setImageResource(R.drawable.btg_btn_tick);
        tickImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "tickImgView", Toast.LENGTH_SHORT).show();
            }
        });
        this.mFabWindowManager.getFabMenu().addMenuItem(tickImgView, new ViewGroup.LayoutParams(itemSize, itemSize));

        ImageView crossImgView = new ImageView(mContext);
        crossImgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        crossImgView.setImageResource(R.drawable.btg_btn_cross);
        crossImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "crossImgView", Toast.LENGTH_SHORT).show();
            }
        });
        this.mFabWindowManager.getFabMenu().addMenuItem(crossImgView, new ViewGroup.LayoutParams(itemSize, itemSize));
        mode = 1;
    }

    // 点击FabWindowManager的透明背景触发，折叠FabMenu
    public void onClose() {
        foldRelated();
    }

    //折叠相关操作
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void foldRelated() {
        if (this.isOpen) {
            this.mFabWindowManager.getFabBgView().setVisibility(View.GONE);  // 透明背景消失
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFabWindowManager.getFabMenu().setVisibility(View.GONE); // fabMenu消失
                }
            }, 200);

            mFabWindowManager.getFabMenu().fold();  // 折叠fabmenu

            ObjectAnimator rotationAni = ObjectAnimator.ofFloat(this.mFabWindowManager.getFabBtn(),
                    "rotation", 0.0f);
            rotationAni.setDuration(100);
            rotationAni.start();
            this.isOpen = false;
        }
    }

    //展开相关操作
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void openRelated() {
        if (!this.isOpen) {
            this.mFabWindowManager.getFabBgView().setVisibility(View.VISIBLE);  // 透明背景显示
            this.mHandler.removeCallbacksAndMessages(null);
            this.mFabWindowManager.getFabMenu().setVisibility(View.VISIBLE);    // fabmenu显示
            this.mFabWindowManager.getFabMenu().launch();

            float rotaAngle = this.mFabWindowManager.getDir() == 0 ? 45.0f : -135.0f;
            ObjectAnimator rotationAni = ObjectAnimator.ofFloat(this.mFabWindowManager.getFabBtn(),
                    "rotation", rotaAngle);
            rotationAni.setDuration(300);
            rotationAni.start();
            this.isOpen = true;
        }
    }

    public void startFabMenu(){
        this.mFabWindowManager.load();  //启动创建悬浮窗
    }

    public void stopFabMenu() {
        this.mFabWindowManager.clearViews();     //关闭悬浮窗
    }


    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void toggle() {
        if (isOpen) {
            foldRelated();
        } else {
            openRelated();
        }
    }
}
