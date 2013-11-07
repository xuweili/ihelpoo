/*
 * Copyright (c) 2013 Wobang Network.
 *
 * Licensed under the GNU General Public License, version 2 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ihelpoo.app.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ihelpoo.app.AppException;
import com.ihelpoo.app.bean.FriendList;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.widget.LoadingDialog;
import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.R;
import com.ihelpoo.app.bean.MyInformation;
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.common.FileUtils;
import com.ihelpoo.app.common.ImageUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 用户资料
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class UserInfo extends BaseActivity {

    private Button back;
    private ImageView refresh;
    private ImageView face;
    private ImageView gender;
    private Button avatarBtn;


    private TextView nickname;
    private TextView account;
    private TextView enrol;
    private TextView school;
    private TextView academy;
    private TextView major;
    private TextView dorm;
    private TextView self;

    private TextView followers;
    private TextView friends;
    private LinearLayout friends_ll;
    private LinearLayout followers_ll;
    private LoadingDialog loading;
    private MyInformation user;
    private Handler mHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        //初始化视图控件
        this.initView();
        //初始化视图数据
        this.initData();
    }

    private void initView() {
        back = (Button) findViewById(R.id.user_info_back);
        refresh = (ImageView) findViewById(R.id.user_info_refresh);
        avatarBtn = (Button) findViewById(R.id.user_info_change_avatar);
        back.setOnClickListener(UIHelper.finish(this));
        refresh.setOnClickListener(refreshClickListener);
        avatarBtn.setOnClickListener(editerClickListener);

        face = (ImageView) findViewById(R.id.user_info_userface);
        nickname = (TextView) findViewById(R.id.user_info_nickname);
        gender = (ImageView) findViewById(R.id.user_info_gender);

        account = (TextView) findViewById(R.id.user_info_account);
        enrol = (TextView) findViewById(R.id.user_info_enrol);
        school = (TextView) findViewById(R.id.user_info_school);
        academy = (TextView) findViewById(R.id.user_info_academy);
        major = (TextView) findViewById(R.id.user_info_major);
        dorm = (TextView) findViewById(R.id.user_info_dorm);
        self = (TextView) findViewById(R.id.user_info_self);
        followers = (TextView) findViewById(R.id.user_info_fans);
        friends = (TextView) findViewById(R.id.user_info_favorites);
        friends_ll = (LinearLayout) findViewById(R.id.user_info_favorites_ll);
        followers_ll = (LinearLayout) findViewById(R.id.user_info_fans_ll);
    }

    private void initData() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (loading != null) loading.dismiss();
                if (msg.what == 1 && msg.obj != null) {
                    user = (MyInformation) msg.obj;

                    //加载用户头像
                    UIHelper.showUserFace(face, user.getAvatar_url());

                    //用户性别
                    if (user.getGender() == 1)
                        gender.setImageResource(R.drawable.widget_gender_man);
                    else
                        gender.setImageResource(R.drawable.widget_gender_woman);

                    nickname.setText(user.getNickname());

                    //其他资料
                    account.setText(user.getEmail());
                    enrol.setText(user.getEnrol_time());
                    school.setText(user.getSchool_name());
                    academy.setText(user.getAcademy_name());
                    major.setText(user.getMajor_name());
                    dorm.setText(user.getDorm_name());
                    self.setText(user.getSelf_intro());

                    followers.setText(user.getFollowers_count() + "");
                    friends.setText(user.getFriends_count() + "");

                    friends_ll.setOnClickListener(followersClickListener);
                    followers_ll.setOnClickListener(fansClickListener);

                } else if (msg.obj != null) {
                    ((AppException) msg.obj).makeToast(UserInfo.this);
                }
            }
        };
        this.loadUserInfoThread(false);
    }

    private void loadUserInfoThread(final boolean isRefresh) {
        loading = new LoadingDialog(this);
        loading.show();

        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    MyInformation user = ((AppContext) getApplication()).getMyInformation(isRefresh);
                    msg.what = 1;
                    msg.obj = user;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private View.OnClickListener editerClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(UserInfo.this, UserInfoEdit.class);
            startActivityForResult(intent, 0);
        }
    };

    private View.OnClickListener refreshClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            loadUserInfoThread(true);
        }
    };

    private View.OnClickListener fansClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int followers = user != null ? user.getFriends_count() : 0;
            int fans = user != null ? user.getFollowers_count() : 0;
            UIHelper.showUserFriend(v.getContext(), FriendList.TYPE_FOLLOWER, followers, fans);
        }
    };

    private View.OnClickListener followersClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int followers = user != null ? user.getFriends_count() : 0;
            int fans = user != null ? user.getFollowers_count() : 0;
            UIHelper.showUserFriend(v.getContext(), FriendList.TYPE_FRIEND, followers, fans);
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(resultCode == 0){
            refresh.performClick();
        }
    }
}
