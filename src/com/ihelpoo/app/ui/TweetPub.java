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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ihelpoo.app.AppConfig;
import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.AppException;
import com.ihelpoo.app.R;
import com.ihelpoo.app.adapter.GridViewFaceAdapter;
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.bean.Tweet;
import com.ihelpoo.app.common.FileUtils;
import com.ihelpoo.app.common.ImageUtils;
import com.ihelpoo.app.common.MediaUtils;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;

/**
 * 发表动弹
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class TweetPub extends BaseActivity {

    private FrameLayout mForm;
    private Button mBack;
    private EditText mContent;
    private Button mPublish;
    private ImageView mFace;
    private ImageView mPick;
    private ImageView mAtme;
    private ImageView mSoftware;
    private ImageView mHelp;
    private Spinner reward;
    private ImageView mImage;
    private LinearLayout mClearwords;
    private TextView mNumberwords;

    private GridView mGridView;
    private GridViewFaceAdapter mGVFaceAdapter;

    private Tweet tweet;
    private File imgFile;
    private String theLarge;
    private String theThumbnail;
    private InputMethodManager imm;

    private String tempTweetKey = AppConfig.TEMP_TWEET;
    private String tempTweetImageKey = AppConfig.TEMP_TWEET_IMAGE;

    public static LinearLayout mMessage;
    public static Context mContext;

    public static final int MAX_TEXT_LENGTH = 222;//最大输入字数
    private static final String TEXT_ATME = "@请输入用户名 ";
    private static final String TEXT_SOFTWARE = "#请输入话题#";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweet_pub);

        mContext = this;
        //软键盘管理类
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        //初始化基本视图
        this.initView();
        //初始化表情视图
        this.initGridView();
    }

    @Override
    protected void onDestroy() {
        mContext = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGridView.getVisibility() == View.VISIBLE) {
            //隐藏表情
            hideFace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mGridView.getVisibility() == View.VISIBLE) {
                //隐藏表情
                hideFace();
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return true;
    }

    //初始化视图控件
    private void initView() {
        mForm = (FrameLayout) findViewById(R.id.tweet_pub_form);
        mBack = (Button) findViewById(R.id.tweet_pub_back);
        mMessage = (LinearLayout) findViewById(R.id.tweet_pub_message);
        mImage = (ImageView) findViewById(R.id.tweet_pub_image);
        mPublish = (Button) findViewById(R.id.tweet_pub_publish);
        mContent = (EditText) findViewById(R.id.tweet_pub_content);
        mFace = (ImageView) findViewById(R.id.tweet_pub_footbar_face);
        mPick = (ImageView) findViewById(R.id.tweet_pub_footbar_photo);
        mAtme = (ImageView) findViewById(R.id.tweet_pub_footbar_atme);
        mSoftware = (ImageView) findViewById(R.id.tweet_pub_footbar_software);
        mHelp = (ImageView) findViewById(R.id.tweet_pub_footbar_help);
        reward = (Spinner) findViewById(R.id.tweet_pub_footbar_reward);
        reward.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (id != 0) {
                    final AppContext ac = (AppContext) getApplication();
                    int activeCredits = ac.getUserActiveCredits();
                    int selectedCredits = Integer.parseInt(String.valueOf(reward.getSelectedItem()));
                    if (activeCredits < selectedCredits) {
                        UIHelper.ToastMessage(TweetPub.this, "活跃不够了(" + activeCredits + ")");
                        reward.setSelection(0);
                        return;
                    }
                    mHelp.setImageResource(R.drawable.widget_bar_help_selected);
                } else {
                    mHelp.setImageResource(R.drawable.widget_bar_help);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reward.performClick();
            }
        });


        mClearwords = (LinearLayout) findViewById(R.id.tweet_pub_clearwords);
        mNumberwords = (TextView) findViewById(R.id.tweet_pub_numberwords);

        mBack.setOnClickListener(UIHelper.finish(this));
        mPublish.setOnClickListener(publishClickListener);
        mImage.setOnLongClickListener(imageLongClickListener);
        mFace.setOnClickListener(faceClickListener);
        mPick.setOnClickListener(pickClickListener);
        mAtme.setOnClickListener(atmeClickListener);
        mSoftware.setOnClickListener(softwareClickListener);
        mClearwords.setOnClickListener(clearwordsClickListener);

        //@某人
        String atme = getIntent().getStringExtra("at_me");
        int atuid = getIntent().getIntExtra("at_uid", 0);
        if (atuid > 0) {
            tempTweetKey = AppConfig.TEMP_TWEET + "_" + atuid;
            tempTweetImageKey = AppConfig.TEMP_TWEET_IMAGE + "_" + atuid;
        }

        //编辑器添加文本监听
        mContent.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //保存当前EditText正在编辑的内容
                ((AppContext) getApplication()).setProperty(tempTweetKey, s.toString());
                //显示剩余可输入的字数
                mNumberwords.setText((MAX_TEXT_LENGTH - s.length()) + "");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        //编辑器点击事件
        mContent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //显示软键盘
                showIMM();
            }
        });
        //设置最大输入字数
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(MAX_TEXT_LENGTH);
        mContent.setFilters(filters);

        //显示临时编辑内容
        UIHelper.showTempEditContent(this, mContent, tempTweetKey);
        //显示临时保存图片
        String tempImage = ((AppContext) getApplication()).getProperty(tempTweetImageKey);
        if (!StringUtils.isEmpty(tempImage)) {
            Bitmap bitmap = ImageUtils.loadImgThumbnail(tempImage, 100, 100);
            if (bitmap != null) {
                imgFile = new File(tempImage);
                mImage.setImageBitmap(bitmap);
                mImage.setVisibility(View.VISIBLE);
            }
        }

        if (atuid > 0 && mContent.getText().length() == 0) {
            mContent.setText(atme);
            mContent.setSelection(atme.length());//设置光标位置
        }
    }

    //初始化表情控件
    private void initGridView() {
        mGVFaceAdapter = new GridViewFaceAdapter(this);
        mGridView = (GridView) findViewById(R.id.tweet_pub_faces);
        mGridView.setAdapter(mGVFaceAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //插入的表情
                SpannableString ss = new SpannableString(view.getTag().toString());
                Drawable d = getResources().getDrawable((int) mGVFaceAdapter.getItemId(position));
                d.setBounds(0, 0, 35, 35);//设置表情图片的显示大小
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                ss.setSpan(span, 0, view.getTag().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //在光标所在处插入表情
                mContent.getText().insert(mContent.getSelectionStart(), ss);
            }
        });
    }

    private void showIMM() {
        mFace.setTag(1);
        showOrHideIMM();
    }

    private void showFace() {
        mFace.setImageResource(R.drawable.widget_bar_keyboard);
        mFace.setTag(1);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void hideFace() {
        mFace.setImageResource(R.drawable.widget_bar_face);
        mFace.setTag(null);
        mGridView.setVisibility(View.GONE);
    }

    private void showOrHideIMM() {
        if (mFace.getTag() == null) {
            //隐藏软键盘
            imm.hideSoftInputFromWindow(mContent.getWindowToken(), 0);
            //显示表情
            showFace();
        } else {
            //显示软键盘
            imm.showSoftInput(mContent, 0);
            //隐藏表情
            hideFace();
        }
    }

    private View.OnClickListener faceClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            showOrHideIMM();
        }
    };

    private View.OnClickListener pickClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //隐藏软键盘
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            //隐藏表情
            hideFace();

            CharSequence[] items = {
                    TweetPub.this.getString(R.string.img_from_album),
                    TweetPub.this.getString(R.string.img_from_camera)
            };
            imageChooseItem(items);
        }
    };

    private View.OnClickListener atmeClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //显示软键盘
            showIMM();

            //在光标所在处插入“@用户名”
            int curTextLength = mContent.getText().length();
            if (curTextLength < MAX_TEXT_LENGTH) {
                String atme = TEXT_ATME;
                int start, end;
                if ((MAX_TEXT_LENGTH - curTextLength) >= atme.length()) {
                    start = mContent.getSelectionStart() + 1;
                    end = start + atme.length() - 2;
                } else {
                    int num = MAX_TEXT_LENGTH - curTextLength;
                    if (num < atme.length()) {
                        atme = atme.substring(0, num);
                    }
                    start = mContent.getSelectionStart() + 1;
                    end = start + atme.length() - 1;
                }
                if (start > MAX_TEXT_LENGTH || end > MAX_TEXT_LENGTH) {
                    start = MAX_TEXT_LENGTH;
                    end = MAX_TEXT_LENGTH;
                }
                mContent.getText().insert(mContent.getSelectionStart(), atme);
                mContent.setSelection(start, end);//设置选中文字
            }
        }
    };

    private View.OnClickListener softwareClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //显示软键盘
            showIMM();

            //在光标所在处插入“#软件名#”
            int curTextLength = mContent.getText().length();
            if (curTextLength < MAX_TEXT_LENGTH) {
                String software = TEXT_SOFTWARE;
                int start, end;
                if ((MAX_TEXT_LENGTH - curTextLength) >= software.length()) {
                    start = mContent.getSelectionStart() + 1;
                    end = start + software.length() - 2;
                } else {
                    int num = MAX_TEXT_LENGTH - curTextLength;
                    if (num < software.length()) {
                        software = software.substring(0, num);
                    }
                    start = mContent.getSelectionStart() + 1;
                    end = start + software.length() - 1;
                }
                if (start > MAX_TEXT_LENGTH || end > MAX_TEXT_LENGTH) {
                    start = MAX_TEXT_LENGTH;
                    end = MAX_TEXT_LENGTH;
                }
                mContent.getText().insert(mContent.getSelectionStart(), software);
                mContent.setSelection(start, end);//设置选中文字
            }
        }
    };

    private View.OnClickListener clearwordsClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            String content = mContent.getText().toString();
            if (!StringUtils.isEmpty(content)) {
                UIHelper.showClearWordsDialog(v.getContext(), mContent, mNumberwords);
            }
        }
    };

    private View.OnLongClickListener imageLongClickListener = new View.OnLongClickListener() {
        public boolean onLongClick(View v) {
            //隐藏软键盘
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            new AlertDialog.Builder(v.getContext())
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(getString(R.string.delete_image))
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //清除之前保存的编辑图片
                            ((AppContext) getApplication()).removeProperty(tempTweetImageKey);

                            imgFile = null;
                            mImage.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return true;
        }

    };

    /**
     * 操作选择
     *
     * @param items
     */
    public void imageChooseItem(CharSequence[] items) {
        AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle(R.string.ui_insert_image).setIcon(android.R.drawable.btn_star).setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //手机选图
                        if (item == 0) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                        }
                        //拍照
                        else if (item == 1) {
                        	//判断是否相机可用
                            String savePath = "";
                            //判断是否挂载了SD卡
                            String storageState = Environment.getExternalStorageState();
                            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                                savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ihelpoo/Camera/";//存放照片的文件夹
                                File savedir = new File(savePath);
                                if (!savedir.exists()) {
                                    savedir.mkdirs();
                                }
                            }

                            //没有挂载SD卡，无法保存文件
                            if (StringUtils.isEmpty(savePath)) {
                                UIHelper.ToastMessage(TweetPub.this, "无法保存照片，请检查SD卡是否挂载");
                                return;
                            }

                            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                            String fileName = "osc_" + timeStamp + ".jpg";//照片命名
                            File out = new File(savePath, fileName);
                            Uri uri = Uri.fromFile(out);

                            theLarge = savePath + fileName;//该照片的绝对路径

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                        }
                    }
                }).create();

        imageDialog.show();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) return;

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1 && msg.obj != null) {
                    //显示图片
                    mImage.setImageBitmap((Bitmap) msg.obj);
                    mImage.setVisibility(View.VISIBLE);
                }
            }
        };

        new Thread() {
            public void run() {
                Bitmap bitmap = null;

                if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
                    if (intent == null) return;

                    Uri thisUri = intent.getData();
                    String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(thisUri);

                    if (thisUri.toString().startsWith("content://com.google.android.gallery3d")
                            || thisUri.toString().startsWith("content://com.android.gallery3d.provider")){
                        TweetPub.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(TweetPub.this, getString(R.string.picasa_image_not_support), Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }


                        //如果是标准Uri
                    if (StringUtils.isEmpty(thePath)) {
                        theLarge = ImageUtils.getAbsoluteImagePath(TweetPub.this, thisUri);
                    } else {
                        theLarge = thePath;
                    }

                    String attFormat = FileUtils.getFileFormat(theLarge);
                    if (!"photo".equals(MediaUtils.getContentType(attFormat))) {
                        TweetPub.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(TweetPub.this, getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }

                    //获取图片缩略图 只有Android2.1以上版本支持
                    if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.ECLAIR_MR1)) {
                        String imgName = FileUtils.getFileName(theLarge);
                        bitmap = ImageUtils.loadImgThumbnail(TweetPub.this, imgName, MediaStore.Images.Thumbnails.MICRO_KIND);
                    }

                    if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
                        bitmap = ImageUtils.loadImgThumbnail(theLarge, 100, 100);
                    }
                }
                //拍摄图片
                else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
                    if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
                        bitmap = ImageUtils.loadImgThumbnail(theLarge, 100, 100);
                    }
                }

                if (bitmap != null) {
                    //存放照片的文件夹
                    String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ihelpoo/Camera/";
                    File savedir = new File(savePath);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }

                    String largeFileName = FileUtils.getFileName(theLarge);
                    String largeFilePath = savePath + largeFileName;
                    //判断是否已存在缩略图
                    if (largeFileName.startsWith("thumb_") && new File(largeFilePath).exists()) {
                        theThumbnail = largeFilePath;
                        imgFile = new File(theThumbnail);
                    } else {
                        //生成上传的800宽度图片
                        String thumbFileName = "thumb_" + largeFileName;
                        theThumbnail = savePath + thumbFileName;
                        if (new File(theThumbnail).exists()) {
                            imgFile = new File(theThumbnail);
                        } else {
                            try {
                                //压缩上传的图片
                                ImageUtils.createImageThumbnail(TweetPub.this, theLarge, theThumbnail, 800, 80);
                                imgFile = new File(theThumbnail);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //保存动弹临时图片
                    ((AppContext) getApplication()).setProperty(tempTweetImageKey, theThumbnail);

                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    private View.OnClickListener publishClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //隐藏软键盘
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            String content = mContent.getText().toString();
            if (StringUtils.isEmpty(content)) {
                UIHelper.ToastMessage(v.getContext(), "请输入消息内容");
                return;
            }

            final AppContext ac = (AppContext) getApplication();
            if (!ac.isLogin()) {
                UIHelper.showLoginDialog(TweetPub.this);
                return;
            }

            mMessage.setVisibility(View.VISIBLE);
            mForm.setVisibility(View.GONE);

            tweet = new Tweet();
            tweet.setAuthorId(ac.getLoginUid());
            tweet.setBody(content);
            tweet.setImageFile(imgFile);
            if (0L == reward.getSelectedItemId()) {
                tweet.setReward(-1);
            } else {
                int selectedCredits = Integer.parseInt(String.valueOf(reward.getSelectedItem()));
                tweet.setReward(selectedCredits);
            }

            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        //清除之前保存的编辑内容
                        ac.removeProperty(tempTweetKey, tempTweetImageKey);
                        finish();
                    } else {
                        mMessage.setVisibility(View.GONE);
                        mForm.setVisibility(View.VISIBLE);
                    }
                }
            };

            new Thread() {
                public void run() {
                    Message msg = new Message();
                    Result res = null;
                    int what = 0;
                    try {
                        res = ac.pubTweet(tweet);
                        what = 1;
                        msg.what = 1;
                        msg.obj = res;
                    } catch (AppException e) {
                        e.printStackTrace();
                        msg.what = -1;
                        msg.obj = e;
                    }
                    handler.sendMessage(msg);
                    UIHelper.sendBroadCastTweet(TweetPub.this, what, res, tweet);
                }
            }.start();
        }
    };
}
