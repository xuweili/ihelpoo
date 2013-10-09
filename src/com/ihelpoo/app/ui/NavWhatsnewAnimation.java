package com.ihelpoo.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.AppStart;
import com.ihelpoo.app.R;
import com.ihelpoo.app.common.StringUtils;

/**
 * 导航过后的动画效果界面
 */
public class NavWhatsnewAnimation extends Activity {

    private ImageView img_left, img_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(NavWelcome.SHAREDPREFERENCES_NAME, this.getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("isFirstIn", false);
        // 提交修改
        editor.commit();
        setContentView(R.layout.nav_whatnew_animation);
        img_left = (ImageView) findViewById(R.id.doorpage_left);
        img_right = (ImageView) findViewById(R.id.doorpage_right);

        //创建一个AnimationSet对象
        AnimationSet animLeft = new AnimationSet(true);
        TranslateAnimation transLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                -1f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        //设置动画效果持续的时间
        transLeft.setDuration(2000);
        //将anim对象添加到AnimationSet对象中
        animLeft.addAnimation(transLeft);
        animLeft.setFillAfter(true);
        img_left.startAnimation(transLeft);
        transLeft.startNow();


        //创建一个AnimationSet对象
        AnimationSet animRight = new AnimationSet(true);
        TranslateAnimation transRight = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                1f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        //设置动画效果持续的时间
        transRight.setDuration(2000);
        //将anim对象添加到AnimationSet对象中
        animRight.addAnimation(transRight);
        animRight.setFillAfter(true);
        img_right.startAnimation(transRight);
        transRight.startNow();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectTo();
            }
        }, 1000);



        //兼容低版本cookie（1.5版本以下，包括1.5.0,1.5.1）
        AppContext appContext = (AppContext)getApplication();
        String cookie = appContext.getProperty("cookie");
        if(StringUtils.isEmpty(cookie)) {
            String cookie_name = appContext.getProperty("cookie_name");
            String cookie_value = appContext.getProperty("cookie_value");
            if(!StringUtils.isEmpty(cookie_name) && !StringUtils.isEmpty(cookie_value)) {
                cookie = cookie_name + "=" + cookie_value;
                appContext.setProperty("cookie", cookie);
                appContext.removeProperty("cookie_domain","cookie_name","cookie_value","cookie_version","cookie_path");
            }
        }
    }

    /**
     * 跳转到...
     */
    private void redirectTo(){
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
    }

}
