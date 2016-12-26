package csmijo.com.floatmenu;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import csmijo.com.floatmenu.view.FabMenu;

public class MainActivity extends AppCompatActivity {

    private FabMenu mFabMenu;
    private int itemSize;
    private int fabActionSize;

    private boolean isClick = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initViews();
        test();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void init() {
        itemSize = getResources().getDimensionPixelSize(R.dimen.btg_fab_menu_item_size);
        fabActionSize = getResources().getDimensionPixelSize(R.dimen.btg_fab_action_size);
    }

    private void initViews() {
//        mFabMenu = (FabMenu) findViewById(R.id.fabMenu);

        mFabMenu = new FabMenu(this);
        //       mFabMenu = new FabMenuApi9(this);
        addContentView(mFabMenu, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


       /* RelativeLayout fabAction = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.btg_view_fab_action, null);
        this.addContentView(fabAction,new ViewGroup.LayoutParams(itemSize,itemSize));
*/

        /*ImageView fabButton = new ImageView(this);
        fabButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        fabButton.setImageResource(R.drawable.btg_btn_fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "fabButtonImagView", Toast.LENGTH_SHORT).show();
            }
        });
        addContentView(fabButton, new ViewGroup.LayoutParams(itemSize, itemSize));*/


    }

    private void test() {
        ImageView reportImg = new ImageView(this);
        reportImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        reportImg.setImageResource(R.drawable.btg_btn_report);
        reportImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "reportImagView", Toast.LENGTH_SHORT).show();
            }
        });
        mFabMenu.addMenuItem(reportImg, new ViewGroup.LayoutParams(itemSize, itemSize));

        ImageView userImg = new ImageView(this);
        userImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        userImg.setImageResource(R.drawable.btg_btn_user);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "userImagView", Toast.LENGTH_SHORT).show();
            }
        });
        mFabMenu.addMenuItem(userImg, new ViewGroup.LayoutParams(itemSize, itemSize));



        RelativeLayout fabAction = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.btg_view_fab_action, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(fabActionSize, fabActionSize);
        fabAction.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "fabAction", Toast.LENGTH_SHORT).show();
                if (!isClick) {
                    mFabMenu.launch();
                }else{
                    mFabMenu.fold();
                }

                isClick = (!isClick);
            }
        });
        mFabMenu.addMenuItem(fabAction, layoutParams);

    }


    public void onLaunch(View view) {
        if (Build.VERSION.SDK_INT >= 11) {
            mFabMenu.launch();
        }
    }

    public void onFold(View view) {
        if (Build.VERSION.SDK_INT >= 11) {
            mFabMenu.fold();
        }
    }

    public void onChangeDir(View view) {
        if (Build.VERSION.SDK_INT >= 11) {
            int dir = mFabMenu.getExpandDir();
            mFabMenu.setExpandDir(dir == 0 ? 1 : 0);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        mClient.disconnect();
    }
}
