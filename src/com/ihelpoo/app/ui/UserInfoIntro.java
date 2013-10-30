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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.AppException;
import com.ihelpoo.app.R;
import com.ihelpoo.app.bean.MyInformation;
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.widget.LoadingDialog;

/**
 * 用户资料
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class UserInfoIntro extends BaseActivity {

    private Button back;
    private ImageView refresh;
    private EditText intro;

    private LoadingDialog loading;
    private MyInformation user;
    private Handler mHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_intro);

        //初始化视图控件
        this.initView();
        //初始化视图数据
        this.initData();
    }

    private void initView() {
        back = (Button) findViewById(R.id.user_intro_back);
        refresh = (ImageView) findViewById(R.id.user_intro_refresh);
        back.setOnClickListener(UIHelper.finish(this));
        refresh.setOnClickListener(saveClickListener);


        intro = (EditText) findViewById(R.id.user_intro_intro);
        intro.requestFocus();
    }

    private void initData() {
        intro.setText(getIntent().getStringExtra("intro"));

        loading = new LoadingDialog(this);
    }


    private View.OnClickListener saveClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(!intro.getText().toString().equals(getIntent().getStringExtra("intro"))){
                updateIntro();
            } else {
                finish();
            }

        }
    };


    private void updateIntro() {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (loading != null) loading.dismiss();
                if (msg.what == 1 && msg.obj != null) {
                    Result res = (Result) msg.obj;
                    //提示信息
                    UIHelper.ToastMessage(UserInfoIntro.this, res.getErrorMessage());
                    if (res.OK()) {
                        finishEdit();
                    }
                } else if (msg.what == -1 && msg.obj != null) {
                    ((AppException) msg.obj).makeToast(UserInfoIntro.this);
                }
            }

        };

        if (loading != null) {
            loading.setLoadText("正在提交修改···");
            loading.show();
        }

        final String _intro = intro.getText().toString();

        new Thread() {
            public void run() {
                AppContext appContext = (AppContext) getApplication();
                Message msg = new Message();
                try {
                    Result res = appContext.updateIntro(appContext.getLoginUid(), _intro);
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

    private void finishEdit() {
        Intent intent = new Intent(this, UserInfoEdit.class);
        setResult(UserInfoEdit.REQUEST_CODE_EDIT_INTRO, intent);
        intent.putExtra("intro", intro.getText().toString());
        finish();
    }
}
