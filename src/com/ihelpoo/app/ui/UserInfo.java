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

    //	private TextView followers;
    private TextView followers;
    private TextView friends;
    private LinearLayout friends_ll;
    //	private LinearLayout followers_ll;
    private LinearLayout followers_ll;
    private LoadingDialog loading;
    private MyInformation user;
    private Handler mHandler;

    private final static int CROP_X = 500;
    private final static int CROP_Y = 375;
    private final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ihelpoo/avatar/";
    private Uri origUri;
    private Uri cropUri;
    private File protraitFile;
    private Bitmap protraitBitmap;
    private String protraitPath;

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
//		followers = (TextView)findViewById(R.id.user_info_followers);
        followers = (TextView) findViewById(R.id.user_info_fans);
        friends = (TextView) findViewById(R.id.user_info_favorites);
        friends_ll = (LinearLayout) findViewById(R.id.user_info_favorites_ll);
//		followers_ll = (LinearLayout)findViewById(R.id.user_info_followers_ll);
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

//					followers.setText(user.getFollowerscount()+"");
                    followers.setText(user.getFollowers_count() + "");
                    friends.setText(user.getFriends_count() + "");

                    friends_ll.setOnClickListener(followersClickListener);
                    followers_ll.setOnClickListener(fansClickListener);
//					followers_ll.setOnClickListener(followersClickListener);

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
            CharSequence[] items = {
                    getString(R.string.img_from_album),
                    getString(R.string.img_from_camera)
            };
            imageChooseItem(items);
        }
    };

    private View.OnClickListener refreshClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            loadUserInfoThread(true);
        }
    };

    private View.OnClickListener favoritesClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            UIHelper.showUserFavorite(v.getContext());
        }
    };

    private View.OnClickListener fansClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int followers = user != null ? user.getFriends_count() : 0;
            int fans = user != null ? user.getFollowers_count() : 0;
            UIHelper.showUserFriend(v.getContext(), FriendList.TYPE_FANS, followers, fans);
        }
    };

    private View.OnClickListener followersClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int followers = user != null ? user.getFriends_count() : 0;
            int fans = user != null ? user.getFollowers_count() : 0;
            UIHelper.showUserFriend(v.getContext(), FriendList.TYPE_FOLLOWER, followers, fans);
        }
    };

    /**
     * 操作选择
     *
     * @param items
     */
    public void imageChooseItem(CharSequence[] items) {
        AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("上传头像").setIcon(android.R.drawable.btn_star).setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //判断是否挂载了SD卡
                        String storageState = Environment.getExternalStorageState();
                        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                            File savedir = new File(FILE_SAVEPATH);
                            if (!savedir.exists()) {
                                savedir.mkdirs();
                            }
                        } else {
                            UIHelper.ToastMessage(UserInfo.this, "无法保存上传的头像，请检查SD卡是否挂载");
                            return;
                        }

                        //输出裁剪的临时文件
                        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        //照片命名
                        String origFileName = "oih_" + timeStamp + ".jpg";
                        String cropFileName = "oih_crop_" + timeStamp + ".jpg";

                        //裁剪头像的绝对路径
                        protraitPath = FILE_SAVEPATH + cropFileName;
                        protraitFile = new File(protraitPath);

                        origUri = Uri.fromFile(new File(FILE_SAVEPATH, origFileName));
                        cropUri = Uri.fromFile(protraitFile);

                        //相册选图
                        if (item == 0) {
                            startActionPickCrop(cropUri);
                        }
                        //手机拍照
                        else if (item == 1) {
                            startActionCamera(origUri);
                        }
                    }
                }).create();

        imageDialog.show();
    }

    /**
     * 选择图片裁剪
     *
     * @param output
     */
    private void startActionPickCrop(Uri output) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra("output", output);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 4);// 裁剪框比例
        intent.putExtra("aspectY", 3);
        intent.putExtra("outputX", CROP_X);// 输出图片大小
        intent.putExtra("outputY", CROP_Y);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    /**
     * 相机拍照
     *
     * @param output
     */
    private void startActionCamera(Uri output) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    /**
     * 拍照后裁剪
     *
     * @param data   原始图片
     * @param output 裁剪后图片
     */
    private void startActionCrop(Uri data, Uri output) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", output);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 4);// 裁剪框比例
        intent.putExtra("aspectY", 3);
        intent.putExtra("outputX", CROP_X);// 输出图片大小
        intent.putExtra("outputY", CROP_Y);
        startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
    }

    /**
     * 上传新照片
     */
    private void uploadNewPhoto() {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (loading != null) loading.dismiss();
                if (msg.what == 1 && msg.obj != null) {
                    Result res = (Result) msg.obj;
                    //提示信息
                    UIHelper.ToastMessage(UserInfo.this, res.getErrorMessage());
                    if (res.OK()) {
                        //显示新头像
                        face.setImageBitmap(protraitBitmap);
                    }
                } else if (msg.what == -1 && msg.obj != null) {
                    ((AppException) msg.obj).makeToast(UserInfo.this);
                }
            }
        };

        if (loading != null) {
            loading.setLoadText("正在上传头像···");
            loading.show();
        }

        new Thread() {
            public void run() {
                //获取头像缩略图
                if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
                    protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath, 200, 150);
                }

                if (protraitBitmap != null) {
                    Message msg = new Message();
                    try {
                        Result res = ((AppContext) getApplication()).updatePortrait(protraitFile);
                        if (res != null && res.OK()) {
                            //保存新头像到缓存
                            String filename = FileUtils.getFileName(user.getAvatar_url());
                            ImageUtils.saveImage(UserInfo.this, filename, protraitBitmap);
                        }
                        msg.what = 1;
                        msg.obj = res;
                    } catch (AppException e) {
                        e.printStackTrace();
                        msg.what = -1;
                        msg.obj = e;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.sendMessage(msg);
                }
            }

            ;
        }.start();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                startActionCrop(origUri, cropUri);//拍照后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                uploadNewPhoto();//上传新照片
                break;
        }
    }
}
