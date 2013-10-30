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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.AppException;
import com.ihelpoo.app.R;
import com.ihelpoo.app.bean.MyInformation;
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.common.FileUtils;
import com.ihelpoo.app.common.ImageUtils;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.widget.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用户资料
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class UserInfoEdit extends BaseActivity {

    private Button btnBack;
    private ImageView refresh;
    private ImageView avatar;
    private Button avatarBtn;

    private RelativeLayout lytAvatar;
    private LinearLayout lytNickname;
    private LinearLayout lytGender;
    private LinearLayout lytEnrol;
    private Spinner spEnrol;
    private LinearLayout lytSchool;
    private LinearLayout lytAcademy;
    private LinearLayout lytMajor;
    private LinearLayout lytDorm;
    private LinearLayout lytIntro;


    private TextView account;
    private TextView nickname;
    private TextView gender;
    private TextView enrol;
    private TextView school;
    private TextView academy;
    private TextView major;
    private TextView dorm;
    private TextView intro;

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

    private int enrolFlag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_edit);

        //初始化视图控件
        this.initView();
        //初始化视图数据
        this.initData();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.user_info_edit_back);
        refresh = (ImageView) findViewById(R.id.user_info_edit_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            }
        });
        refresh.setOnClickListener(refreshClickListener);

        lytAvatar = (RelativeLayout) findViewById(R.id.user_edit_avatar_lyt);
        avatarBtn = (Button) findViewById(R.id.user_info_edit_change_avatar);
        avatar = (ImageView) findViewById(R.id.user_info_edit_avatar);

        lytNickname = (LinearLayout) findViewById(R.id.user_edit_nickname_lyt);
        lytGender = (LinearLayout) findViewById(R.id.user_edit_gender_lyt);
        lytEnrol = (LinearLayout) findViewById(R.id.user_edit_enrol_lyt);
        spEnrol = (Spinner) findViewById(R.id.user_info_edit_enrol_spinner);
        lytSchool = (LinearLayout) findViewById(R.id.user_edit_school_lyt);
        lytAcademy = (LinearLayout) findViewById(R.id.user_edit_academy_lyt);
        lytMajor = (LinearLayout) findViewById(R.id.user_edit_major_lyt);
        lytDorm = (LinearLayout) findViewById(R.id.user_edit_dorm_lyt);
        lytIntro = (LinearLayout) findViewById(R.id.user_edit_intro_lyt);


        lytAvatar.setOnClickListener(onClickAvatar);
        avatarBtn.setOnClickListener(onClickAvatar);
        avatar.setOnClickListener(onClickAvatar);

        lytNickname.setOnClickListener(onClickNickname);
        lytGender.setOnClickListener(onClickGender);
        lytEnrol.setOnClickListener(onClickEnrol);
        spEnrol.setOnItemSelectedListener(onSelectEnrol);
        lytSchool.setOnClickListener(onClickSchool);
        lytAcademy.setOnClickListener(onClickAcademy);
        lytMajor.setOnClickListener(onClickMajor);
        lytDorm.setOnClickListener(onClickDorm);
        lytIntro.setOnClickListener(onClickIntro);


        account = (TextView) findViewById(R.id.user_info_edit_account);

        nickname = (TextView) findViewById(R.id.user_info_edit_nickname);
        gender = (TextView) findViewById(R.id.user_info_edit_gender);
        enrol = (TextView) findViewById(R.id.user_info_edit_enrol);
        school = (TextView) findViewById(R.id.user_info_edit_school);
        academy = (TextView) findViewById(R.id.user_info_edit_academy);
        major = (TextView) findViewById(R.id.user_info_edit_major);
        dorm = (TextView) findViewById(R.id.user_info_edit_dorm);
        intro = (TextView) findViewById(R.id.user_info_edit_self);
    }

    private void initData() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (loading != null) loading.dismiss();
                if (msg.what == 1 && msg.obj != null) {
                    user = (MyInformation) msg.obj;

                    //加载用户头像
                    UIHelper.showUserFace(avatar, user.getAvatar_url());

                    //其他资料
                    account.setText(user.getEmail());
                    nickname.setText(user.getNickname());
                    gender.setText(user.getGender() == 1 ? "男" : "女");
                    enrol.setText(user.getEnrol_time());
                    school.setText(user.getSchool_name());
                    academy.setText(user.getAcademy_name());
                    major.setText(user.getMajor_name());
                    dorm.setText(user.getDorm_name());
                    intro.setText(user.getSelf_intro());
                    spEnrol.setSelection(((ArrayAdapter) spEnrol.getAdapter()).getPosition(user.getEnrol_time()));

                } else if (msg.obj != null) {
                    ((AppException) msg.obj).makeToast(UserInfoEdit.this);
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

    private View.OnClickListener onClickAvatar = new View.OnClickListener() {
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
                            UIHelper.ToastMessage(UserInfoEdit.this, "无法保存上传的头像，请检查SD卡是否挂载");
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
                    UIHelper.ToastMessage(UserInfoEdit.this, res.getErrorMessage());
                    if (res.OK()) {
                        //显示新头像
                        avatar.setImageBitmap(protraitBitmap);
                    }
                } else if (msg.what == -1 && msg.obj != null) {
                    ((AppException) msg.obj).makeToast(UserInfoEdit.this);
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
                            ImageUtils.saveImage(UserInfoEdit.this, filename, protraitBitmap);
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
        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                startActionCrop(origUri, cropUri);//拍照后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                uploadNewPhoto();//上传新照片
                break;
        }

        switch (resultCode) {
            case REQUEST_CODE_EDIT_NICKNAME:
                nickname.setText(data.getStringExtra("nickname"));
                break;
            case REQUEST_CODE_EDIT_GENDER:
                String genderExtra = data.getStringExtra("gender");
                gender.setText("1".equals(genderExtra) ? "男" : "女");
                break;
            case REQUEST_CODE_EDIT_INTRO:
                intro.setText(data.getStringExtra("intro"));
                break;
            case REQUEST_CODE_EDIT_MAJOR:
                school.setText(data.getStringExtra("school_name"));
                academy.setText(data.getStringExtra("academy_name"));
                major.setText(data.getStringExtra("major_name"));
                dorm.setText(data.getStringExtra("dorm_name"));
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(UserInfoEdit.this, UserInfo.class);
            UserInfoEdit.this.setResult(0, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    public static final int REQUEST_CODE_EDIT_NICKNAME = 10;
    public static final int REQUEST_CODE_EDIT_GENDER = 11;
    public static final int REQUEST_CODE_EDIT_MAJOR = 12;
    public static final int REQUEST_CODE_EDIT_INTRO = 13;
    private View.OnClickListener onClickNickname = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(UserInfoEdit.this, UserInfoNickname.class);
            intent.putExtra("nickname", nickname.getText());
            startActivityForResult(intent, REQUEST_CODE_EDIT_NICKNAME);
        }
    };
    private View.OnClickListener onClickGender = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(UserInfoEdit.this, UserInfoGender.class);
            intent.putExtra("gender", gender.getText().toString());
            startActivityForResult(intent, REQUEST_CODE_EDIT_GENDER);
        }
    };
    private View.OnClickListener onClickEnrol = new View.OnClickListener() {
        public void onClick(View v) {
            spEnrol.performClick();
        }
    };
    private AdapterView.OnItemSelectedListener onSelectEnrol = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            enrolFlag += 1;
            if(enrolFlag<=1) return;
            if(!enrol.getText().toString().equals(spEnrol.getSelectedItem().toString())){
                updateEnrol();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }
    };
    private View.OnClickListener onClickSchool = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(UserInfoEdit.this, UserInfoMajor.class);
            intent.putExtra("school_name", school.getText().toString());
            startActivityForResult(intent, REQUEST_CODE_EDIT_MAJOR);
        }
    };
    private View.OnClickListener onClickAcademy = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(UserInfoEdit.this, UserInfoMajor.class);
            intent.putExtra("academy_name", academy.getText().toString());
            startActivityForResult(intent, REQUEST_CODE_EDIT_MAJOR);
        }
    };
    private View.OnClickListener onClickMajor = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(UserInfoEdit.this, UserInfoMajor.class);
            intent.putExtra("major_name", major.getText().toString());
            startActivityForResult(intent, REQUEST_CODE_EDIT_MAJOR);
        }
    };
    private View.OnClickListener onClickDorm = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(UserInfoEdit.this, UserInfoMajor.class);
            intent.putExtra("dorm_name", dorm.getText().toString());
            startActivityForResult(intent, REQUEST_CODE_EDIT_MAJOR);
        }
    };
    private View.OnClickListener onClickIntro = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(UserInfoEdit.this, UserInfoIntro.class);
            intent.putExtra("intro", intro.getText());
            startActivityForResult(intent, REQUEST_CODE_EDIT_INTRO);
        }
    };

    private void updateEnrol() {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (loading != null) loading.dismiss();
                if (msg.what == 1 && msg.obj != null) {
                    Result res = (Result) msg.obj;
                    //提示信息
                    UIHelper.ToastMessage(UserInfoEdit.this, res.getErrorMessage());
                    if (res.OK()) {
                        enrol.setText(spEnrol.getSelectedItem().toString());
                    }
                } else if (msg.what == -1 && msg.obj != null) {
                    ((AppException) msg.obj).makeToast(UserInfoEdit.this);
                }
            }

        };

        if (loading != null) {
            loading.setLoadText("正在提交修改···");
            loading.show();
        }

        final String newEnrol = spEnrol.getSelectedItem().toString();

        new Thread() {
            public void run() {
                AppContext appContext = (AppContext) getApplication();
                Message msg = new Message();
                try {
                    Result res = appContext.updateEnrol(appContext.getLoginUid(), newEnrol);
                    msg.what = 1;
                    msg.obj = res;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();

    }
}
