package com.qsa.metallurgy_material_engineering_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        transparentToolbar();
/*

        AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.avLoader);
        avLoadingIndicatorView.show();
*/

        new android.os.Handler().postDelayed(() -> {
            finish();
//            avLoadingIndicatorView.hide();

            startActivity(new Intent(getBaseContext(), MainActivity.class));

        }, 3000); // 3000 means 3 seconds
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void transparentToolbar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        win.setAttributes(winParams);
    }
}