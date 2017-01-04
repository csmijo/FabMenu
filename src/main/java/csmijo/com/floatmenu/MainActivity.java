package csmijo.com.floatmenu;

import android.app.Activity;
import android.os.Bundle;

import csmijo.com.floatmenu.view.FabManager;

public class MainActivity extends Activity {

    private FabManager mFabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFabManager = new FabManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFabManager.startFabMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFabManager.stopFabMenu();
    }
}
