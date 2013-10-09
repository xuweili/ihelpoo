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

import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.AppException;
import com.ihelpoo.app.R;
import com.ihelpoo.app.api.ApiClient;
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.bean.User;
import com.ihelpoo.app.common.QQWeiboHelper;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.AccessTokenKeeper;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;

/**
 * 用户登录对话框
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class LoginDialog extends BaseActivity {

    private ViewSwitcher mViewSwitcher;
    private ImageButton btn_close;
    private Button btn_login;
    private Button btn_login_wb;
    private Button btn_login_qq;
    private AutoCompleteTextView mAccount;
    private EditText mPwd;
    private AnimationDrawable loadingAnimation;
    private View loginLoading;
    private CheckBox chb_remember;
    private CheckBox chb_status;
    private int curLoginType;
    private InputMethodManager imm;

    public final static int LOGIN_OTHER = 0x00;
    public final static int LOGIN_MAIN = 0x01;
    public final static int LOGIN_SETTING = 0x02;


    /**
     * 微博API接口类，提供登陆等功能
     */
    private Weibo mWeibo;

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    /**
     * 注意：SsoHandler 仅当sdk支持sso时有效
     */
    private SsoHandler mSsoHandler;


    private static Tencent mTencent;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        curLoginType = getIntent().getIntExtra("LOGINTYPE", LOGIN_OTHER);

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.logindialog_view_switcher);
        loginLoading = (View) findViewById(R.id.login_loading);
        mAccount = (AutoCompleteTextView) findViewById(R.id.login_account);
        mPwd = (EditText) findViewById(R.id.login_password);
        chb_remember = (CheckBox) findViewById(R.id.login_checkbox_remember);
        chb_status = (CheckBox) findViewById(R.id.login_checkbox_status);

        btn_close = (ImageButton) findViewById(R.id.login_close_button);
        btn_close.setOnClickListener(UIHelper.finish(this));

        btn_login = (Button) findViewById(R.id.login_btn_login);


        mWeibo = Weibo.getInstance(QQWeiboHelper.APP_KEY_WB, QQWeiboHelper.REDIRECT_URL_WB, QQWeiboHelper.SCOPE_WB);
        mTencent = Tencent.createInstance(QQWeiboHelper.APP_ID_QQ, this.getApplicationContext());

        mHandler = new Handler();
        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            String date = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
                    .format(new java.util.Date(mAccessToken.getExpiresTime()));
            Toast.makeText(LoginDialog.this, "access_token 仍在有效期内,无需再次登录: \naccess_token:"
                    + mAccessToken.getToken() + "\n有效期：" + date, Toast.LENGTH_SHORT).show();
        }
        btn_login_wb = (Button) findViewById(R.id.login_btn_login_wb);
        btn_login_qq = (Button) findViewById(R.id.login_btn_login_qq);

        btn_login_wb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSsoHandler = new SsoHandler(LoginDialog.this, mWeibo);
                mSsoHandler.authorize(new AuthDialogListener(), "com.ihelpoo.app");
            }
        });

        btn_login_qq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickLogin();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //隐藏软键盘
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                String account = mAccount.getText().toString();
                String pwd = mPwd.getText().toString();
                boolean isRemember = chb_remember.isChecked();
                boolean status = chb_status.isChecked();
                //判断输入
                if (StringUtils.isEmpty(account)) {
                    UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_email_null));
                    return;
                }
                if (StringUtils.isEmpty(pwd)) {
                    UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_pwd_null));
                    return;
                }

                btn_close.setVisibility(View.GONE);
                loadingAnimation = (AnimationDrawable) loginLoading.getBackground();
                loadingAnimation.start();
                mViewSwitcher.showNext();

                login(account, pwd, isRemember, status ? "1" : "0");
            }
        });

        //是否显示登录信息
        AppContext ac = (AppContext) getApplication();
        User user = ac.getLoginInfo();
        if (user == null || !user.isRemember()) return;
        if (!StringUtils.isEmpty(user.getAccount())) {
            mAccount.setText(user.getAccount());
            mAccount.selectAll();
            chb_remember.setChecked(user.isRemember());
            chb_status.setChecked(Boolean.valueOf(user.getStatus()));
        }
        if (!StringUtils.isEmpty(user.getPwd())) {
            mPwd.setText(user.getPwd());
        }
    }

    //登录验证
    private void login(final String account, final String pwd, final boolean isRemember, final String status) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    User user = (User) msg.obj;
                    if (user != null) {
                        //清空原先cookie
                        ApiClient.cleanCookie();
                        //发送通知广播
                        UIHelper.sendBroadCast(LoginDialog.this, user.getNotice());
                        //提示登陆成功
                        UIHelper.ToastMessage(LoginDialog.this, R.string.msg_login_success);
                        if (curLoginType == LOGIN_MAIN) {
                            //跳转--加载用户动态
                            Intent intent = new Intent(LoginDialog.this, Main.class);
                            intent.putExtra("LOGIN", true);
                            startActivity(intent);
                        } else if (curLoginType == LOGIN_SETTING) {
                            //跳转--用户设置页面
                            Intent intent = new Intent(LoginDialog.this, Setting.class);
                            intent.putExtra("LOGIN", true);
                            startActivity(intent);
                        }
                        finish();
                    }
                } else if (msg.what == 0) {
                    mViewSwitcher.showPrevious();
                    btn_close.setVisibility(View.VISIBLE);
                    UIHelper.ToastMessage(LoginDialog.this, getString(R.string.msg_login_fail) + msg.obj);
                } else if (msg.what == -1) {
                    mViewSwitcher.showPrevious();
                    btn_close.setVisibility(View.VISIBLE);
                    ((AppException) msg.obj).makeToast(LoginDialog.this);
                }
            }
        };
        new Thread() {
            public void run() {
                /*
                 * While the constructor of Message is public, the best way to get one of these is
                 * to call Message.obtain() or one of the Handler.obtainMessage() methods, which
                 * will pull them from a pool of recycled objects.
                 * TODO, http://developer.android.com/reference/android/os/Message.html
                 *
                 */
                Message msg = new Message();
                try {
                    AppContext ac = (AppContext) getApplication();
                    User user = ac.loginVerify(account, pwd, status);
                    user.setAccount(account);
                    user.setPwd(pwd);
                    user.setRemember(isRemember);
                    user.setStatus(status);
                    Result res = user.getValidate();
                    if (res.OK()) {
                        ac.saveLoginInfo(user);//保存登录信息
                        msg.what = 1;//成功
                        msg.obj = user;
                    } else {
                        ac.cleanLoginInfo();//清除登录信息
                        msg.what = 0;//失败
                        msg.obj = res.getErrorMessage();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onDestroy();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 微博认证授权回调类。
     * 1. SSO登陆时，需要在{@link #onActivityResult}中调用mSsoHandler.authorizeCallBack后，
     * 该回调才会被执行。
     * 2. 非SSO登陆时，当授权后，就会被执行。
     * 当授权成功后，请保存该access_token、expires_in等信息到SharedPreferences中。
     */
    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {

            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            String userName = values.getString("userName");
            mAccessToken = new Oauth2AccessToken(token, expires_in);
            if (mAccessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new java.util.Date(mAccessToken.getExpiresTime()));

                AccessTokenKeeper.keepAccessToken(LoginDialog.this, mAccessToken);
                Toast.makeText(LoginDialog.this, "欢迎 "+userName, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(getApplicationContext(),
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(),
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的Activity必须重写onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        mTencent.onActivityResult(requestCode, resultCode, data);
    }

    private void onClickLogin() {
        if (!mTencent.isSessionValid()) {
            IUiListener listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
                            Constants.HTTP_GET, new BaseApiListener("get_simple_userinfo", false), null);
                }
            };
            mTencent.login(this, "all", listener);
        } else {
            mTencent.logout(this);
        }
    }


    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(JSONObject response) {
            doComplete(response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            showResult("onError:", "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            showResult("onCancel", "");
        }



    }

    private class BaseApiListener implements IRequestListener {
        private String mScope = "all";
        private Boolean mNeedReAuth = false;

        public BaseApiListener(String scope, boolean needReAuth) {
            mScope = scope;
            mNeedReAuth = needReAuth;
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
            try {
                showResult("IRequestListener.onComplete:", response.getString("nickname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            doComplete(response, state);
        }

        protected void doComplete(JSONObject response, Object state) {
            try {
                int ret = response.getInt("ret");
                if (ret == 100030) {
                    if (mNeedReAuth) {
                        Runnable r = new Runnable() {
                            public void run() {
                                mTencent.reAuth(LoginDialog.this, mScope, new BaseUiListener());
                            }
                        };
                        LoginDialog.this.runOnUiThread(r);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("toddtest", response.toString());
            }

        }

        @Override
        public void onIOException(final IOException e, Object state) {
            showResult("IRequestListener.onIOException:", e.getMessage());
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e,
                                            Object state) {
            showResult("IRequestListener.onMalformedURLException", e.toString());
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
            showResult("IRequestListener.onJSONException:", e.getMessage());
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException arg0,
                                              Object arg1) {
            showResult("IRequestListener.onConnectTimeoutException:", arg0.getMessage());

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException arg0,
                                             Object arg1) {
            showResult("IRequestListener.SocketTimeoutException:", arg0.getMessage());
        }

        @Override
        public void onUnknowException(Exception arg0, Object arg1) {
            showResult("IRequestListener.onUnknowException:", arg0.getMessage());
        }

        @Override
        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
            showResult("IRequestListener.HttpStatusException:", arg0.getMessage());
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException arg0, Object arg1) {
            showResult("IRequestListener.onNetworkUnavailableException:", arg0.getMessage());
        }
    }

    private void showResult(final String base, final String msg) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(LoginDialog.this, "欢迎 " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
