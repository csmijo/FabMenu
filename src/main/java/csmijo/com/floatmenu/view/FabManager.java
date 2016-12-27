package csmijo.com.floatmenu.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    public FabManager(Context context) {
        this.mContext = context;
        this.itemSize = mContext.getResources().getDimensionPixelSize(R.dimen.btg_fab_menu_item_size);
        this.mFabWindowManager = new FabWindowManager(context, this);
        createMenuOne();
    }


    // 创建菜单一：fabactionImgView，reportImgView，userImgView
    public void createMenuOne() {

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


        RelativeLayout fabAction = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.btg_view_fab_action, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(itemSize, itemSize);
        fabAction.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "fabAction", Toast.LENGTH_SHORT).show();
                if (!isOpen) {
                    mFabWindowManager.getFabMenu().launch();
                } else {
                    mFabWindowManager.getFabMenu().fold();
                }

                isOpen = (!isOpen);
            }
        });
        this.mFabWindowManager.getFabMenu().addMenuItem(fabAction, layoutParams);

        mode = 0;
    }

    // 创建菜单二：fabaction2，tickImgView，crossImgView
    public void createMenuTwo() {
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


        RelativeLayout fabAction = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.btg_view_fab_action, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(itemSize, itemSize);
        ImageView fabBtnImgView = (ImageView) fabAction.findViewById(R.id.fabBtnImgView);
        fabBtnImgView.setImageResource(R.drawable.btg_btn_publish);
        fabAction.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "fabAction", Toast.LENGTH_SHORT).show();
                if (!isOpen) {
                    mFabWindowManager.getFabMenu().launch();
                } else {
                    mFabWindowManager.getFabMenu().fold();
                }

                isOpen = (!isOpen);
            }
        });
        this.mFabWindowManager.getFabMenu().addMenuItem(fabAction, layoutParams);

        mode = 1;

    }

}
