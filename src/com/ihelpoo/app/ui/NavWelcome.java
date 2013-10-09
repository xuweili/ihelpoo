package com.ihelpoo.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.ihelpoo.app.R;

/**
 * 开场欢迎动画
 */
public class NavWelcome extends Activity {
    public static String SHAREDPREFERENCES_NAME = "qq_nav";
    private boolean isFirstIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //读取SharedPreFerences中需要的数据,使用SharedPreFerences来记录程序启动的使用次数
        SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        //取得相应的值,如果没有该值,说明还未写入,用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);
        //判断程序第几次启动
        if (!isFirstIn) {
            Intent intent = new Intent(NavWelcome.this, NavWhatsnewAnimation.class);
            startActivity(intent);
            NavWelcome.this.finish();
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(NavWelcome.this, NavWhatsnewPages.class);
                    startActivity(intent);
                    NavWelcome.this.finish();
                }
            }, 2000);
        }
        setContentView(R.layout.nav_strat);
    }
}
