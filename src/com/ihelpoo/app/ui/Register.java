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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.AppException;
import com.ihelpoo.app.R;
import com.ihelpoo.app.bean.MobileCodeResult;
import com.ihelpoo.app.bean.MobileRegisterResult;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.common.VerifyUtil;

/**
 * 关于我们
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class Register extends BaseActivity implements android.view.View.OnClickListener {

    public static final int MOBILE_CODE_LENGHT = 4;
    private int REGION_SELECT = 1;
    private TextView tv_top_title;
    private Button btn_title_left, btn_title_right;
    private TextView register_step_1, register_step_2, register_step_3;
    private Button btn_fetch_code, btn_input_code, btn_input_pwd;
    private EditText txt_mobile_no, txt_mobile_code, txt_pwd;

    private Integer code = null;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);
        initView();
    }

    private void initView() {
        tv_top_title = (TextView) findViewById(R.id.tv_top_title);
        tv_top_title.setText("注册");

        btn_title_right = (Button) findViewById(R.id.btn_title_right);
        btn_title_right.setVisibility(View.GONE);

        btn_title_left = (Button) findViewById(R.id.btn_title_left);
        btn_title_left.setOnClickListener(this);


        txt_mobile_no = (EditText) findViewById(R.id.register_mobile_no_text);
        txt_mobile_no.addTextChangedListener(toEnableButtonTextWatcher);

        txt_mobile_code = (EditText) findViewById(R.id.register_mobile_code_text);
        txt_pwd = (EditText) findViewById(R.id.register_password_text);


        btn_fetch_code = (Button) findViewById(R.id.btn_register_fetch_code);
        btn_fetch_code.setOnClickListener(this);
        btn_input_code = (Button) findViewById(R.id.btn_register_input_code);
        btn_input_code.setOnClickListener(this);
        btn_input_pwd = (Button) findViewById(R.id.btn_register_input_pwd);
        btn_input_pwd.setOnClickListener(this);


        register_step_1 = (TextView) findViewById(R.id.register_step_1);
        register_step_2 = (TextView) findViewById(R.id.register_step_2);
        register_step_3 = (TextView) findViewById(R.id.register_step_3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_left:
                Register.this.finish();
                break;
            case R.id.btn_register_fetch_code:
                final String mobiles = txt_mobile_no.getText().toString();
                if (VerifyUtil.isMobileNO(mobiles) == false) {
                    Toast.makeText(Register.this, "正确填写手机号，我们将向您发送一条验证码短信", Toast.LENGTH_LONG).show();
                } else {
                    final AppContext ac = (AppContext) getApplication();
                    mProgress = ProgressDialog.show(v.getContext(), null, "获取中···", true, true);

                    final Handler handler = new Handler() {
                        public void handleMessage(Message msg) {

                            if (mProgress != null) mProgress.dismiss();

                            if (msg.what == 1 && msg.obj != null) {
                                MobileCodeResult res = (MobileCodeResult) msg.obj;
                                if (res.OK()) {
                                    code = res.getRegisterCode().getCode();
                                    UIHelper.ToastMessage(Register.this, "已经向您手机发送验证码短信，请查收并继续验证");

                                    toStep(2);
                                } else {
                                    UIHelper.ToastMessage(Register.this, res.getErrorMessage());
                                }
                            } else {
                                ((AppException) msg.obj).makeToast(Register.this);
                            }
                        }
                    };
                    new Thread() {
                        public void run() {
                            Message msg = new Message();
                            MobileCodeResult res;
                            try {
                                //发表评论
                                res = ac.mobileCode(mobiles);
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
                break;
            case R.id.btn_register_input_code:
                if (txt_mobile_code.getText().toString().length() != MOBILE_CODE_LENGHT) {
                    UIHelper.ToastMessage(Register.this, "请输入" + MOBILE_CODE_LENGHT + "位验证码");
                } else {
                    if(code != null && txt_mobile_code.getText().toString().equals(String.valueOf(code))){
                        toStep(3);
                    } else {
                        UIHelper.ToastMessage(Register.this, "验证码输入错误，请重新输入");
                        txt_mobile_code.setText("");
                    }

                }

                break;
            case R.id.btn_register_input_pwd:

                closeSoftKeyBoard();

                final String mobileNo = txt_mobile_no.getText().toString();
                final String mobileCode = txt_mobile_code.getText().toString();
                final String pwd = txt_pwd.getText().toString();

                final AppContext ac = (AppContext) getApplication();
                mProgress = ProgressDialog.show(v.getContext(), null, "注册中···", true, true);


                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {

                        if (mProgress != null) mProgress.dismiss();

                        if (msg.what == 1 && msg.obj != null) {
                            MobileRegisterResult res = (MobileRegisterResult) msg.obj;
                            if (res.OK()) {
                                UIHelper.ToastMessage(Register.this, "注册成功" );

                            } else {
                                UIHelper.ToastMessage(Register.this, res.getErrorMessage());
                            }
                        } else {
                            ((AppException) msg.obj).makeToast(Register.this);
                        }
                    }
                };
                new Thread() {
                    public void run() {
                        Message msg = new Message();
                        MobileRegisterResult res;
                        try {
                            SharedPreferences preferences = getSharedPreferences(NavWelcome.GLOBAL_CONFIG, MODE_PRIVATE);
                            int mySchool = preferences.getInt(NavWelcome.CHOOSE_SCHOOL, NavWelcome.DEFAULT_SCHOOL);
                            res = ac.mobileRegister(mobileCode, mobileNo, pwd, mySchool + "");
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
                break;
        }

    }

    private void closeSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt_pwd.getWindowToken(), 0);
    }

    private void toStep(int step) {
        switch (step){
            case 2:

                txt_mobile_no.setVisibility(View.GONE);
                txt_mobile_code.setVisibility(View.VISIBLE);
                txt_pwd.setVisibility(View.GONE);
                txt_mobile_code.requestFocus();

                btn_fetch_code.setVisibility(View.GONE);
                btn_input_code.setVisibility(View.VISIBLE);
                btn_input_pwd.setVisibility(View.GONE);

                register_step_1.setTextColor(Color.LTGRAY);
                register_step_2.setTextColor(Color.BLACK);
                register_step_3.setTextColor(Color.LTGRAY);
                break;
            case 3:

                txt_mobile_no.setVisibility(View.GONE);
                txt_mobile_code.setVisibility(View.GONE);
                txt_pwd.setVisibility(View.VISIBLE);
                txt_pwd.requestFocus();

                btn_fetch_code.setVisibility(View.GONE);
                btn_input_code.setVisibility(View.GONE);
                btn_input_pwd.setVisibility(View.VISIBLE);

                register_step_1.setTextColor(Color.LTGRAY);
                register_step_2.setTextColor(Color.LTGRAY);
                register_step_3.setTextColor(Color.BLACK);
                break;
            default:

                txt_mobile_no.setVisibility(View.VISIBLE);
                txt_mobile_code.setVisibility(View.GONE);
                txt_pwd.setVisibility(View.GONE);
                txt_mobile_no.requestFocus();

                btn_fetch_code.setVisibility(View.VISIBLE);
                btn_input_code.setVisibility(View.GONE);
                btn_input_pwd.setVisibility(View.GONE);

                register_step_1.setTextColor(Color.BLACK);
                register_step_2.setTextColor(Color.LTGRAY);
                register_step_3.setTextColor(Color.LTGRAY);


        }
    }

    TextWatcher toEnableButtonTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (VerifyUtil.isMobileNO(s.toString())) {
                btn_fetch_code.setEnabled(true);
            } else {
                btn_fetch_code.setEnabled(false);
            }
        }
    };


}
