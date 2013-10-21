package com.ihelpoo.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.R;
import com.ihelpoo.app.common.StringUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 导航过后的动画效果界面
 */
public class NavWhatsnewAnimation extends Activity {

    private ImageView img_left, img_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(NavWelcome.GLOBAL_CONFIG, this.getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("isFirstIn", false);
        // 提交修改
        editor.commit();
        setContentView(R.layout.nav_whatnew_animation);
        img_left = (ImageView) findViewById(R.id.doorpage_left);
//        img_right = (ImageView) findViewById(R.id.doorpage_right);
        Bitmap bmp=getBitmapFromURL("http://static.ihelpoo.cn/img/mobile/start/school_1.jpg");
        img_left.setImageBitmap(bmp);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectTo();
            }
        }, 1000);


        //兼容低版本cookie（1.5版本以下，包括1.5.0,1.5.1）
        AppContext appContext = (AppContext) getApplication();
        String cookie = appContext.getProperty("cookie");
        if (StringUtils.isEmpty(cookie)) {
            String cookie_name = appContext.getProperty("cookie_name");
            String cookie_value = appContext.getProperty("cookie_value");
            if (!StringUtils.isEmpty(cookie_name) && !StringUtils.isEmpty(cookie_value)) {
                cookie = cookie_name + "=" + cookie_value;
                appContext.setProperty("cookie", cookie);
                appContext.removeProperty("cookie_domain", "cookie_name", "cookie_value", "cookie_version", "cookie_path");
            }
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {

            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap mybitmap = BitmapFactory.decodeStream(input);

            return mybitmap;

        } catch (Exception ex) {

            return null;
        }
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {

//        SharedPreferences preferences = getSharedPreferences(NavWelcome.GLOBAL_CONFIG, this.getApplicationContext().MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();

        SharedPreferences preferences = getSharedPreferences(NavWelcome.GLOBAL_CONFIG, MODE_PRIVATE);
        int mySchool = preferences.getInt(NavWelcome.CHOOSE_SCHOOL, NavWelcome.DEFAULT_SCHOOL);
        if (mySchool == NavWelcome.DEFAULT_SCHOOL) {
            Intent intent = new Intent(this, SchoolListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
    }

}
