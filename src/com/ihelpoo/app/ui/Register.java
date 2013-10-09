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
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ihelpoo.app.R;
import com.ihelpoo.app.common.VerifyUtil;

/**
 * 关于我们
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class Register extends BaseActivity implements android.view.View.OnClickListener {

    public static final int REGION_SELECT = 1;
    private TextView tv_QQ_Server, tv_region_modify, tv_region_show, tv_top_title;
    private Button btn_title_left, btn_title_right, btn_send_code;
    private CheckBox chk_agree;
    private EditText et_mobileNo;

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

        btn_send_code = (Button) findViewById(R.id.btn_send_code);
        btn_send_code.setOnClickListener(this);

        tv_QQ_Server = (TextView) findViewById(R.id.tv_QQ_Server);
        tv_QQ_Server.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        tv_region_show = (TextView) findViewById(R.id.tv_region_show);

        tv_region_modify = (TextView) findViewById(R.id.tv_region_modify);
        tv_region_modify.setOnClickListener(this);

        chk_agree = (CheckBox) findViewById(R.id.chk_agree);
        et_mobileNo = (EditText) findViewById(R.id.et_mobileNo);
    }

    /**
     * 重写了onCreateDialog方法来创建一个列表对话框
     */
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        // TODO Auto-generated method stub
        switch (id) {
            case REGION_SELECT:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请选择所在地");
                builder.setSingleChoiceItems(//第一个参数是要显示的列表，用数组展示；第二个参数是默认选中的项的位置；
                        //第三个参数是一个事件点击监听器
                        new String[]{"+86中国大陆", "+853中国澳门", "+852中国香港", "+886中国台湾"},
                        0,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                switch (which) {
                                    case 0:
                                        tv_region_show.setText("+86中国大陆");

                                        break;
                                    case 1:
                                        tv_region_show.setText("+853中国澳门");
                                        break;
                                    case 2:
                                        tv_region_show.setText("+852中国香港");
                                        break;
                                    case 3:
                                        tv_region_show.setText("+886中国台湾");
                                        break;
                                }
                                dismissDialog(REGION_SELECT);//想对话框关闭
                            }
                        });
                return builder.create();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_region_modify:
                showDialog(REGION_SELECT);//显示列表对话框的方法
                break;
            case R.id.btn_title_left:
                Register.this.finish();
                break;
            case R.id.btn_send_code:
                String mobiles = et_mobileNo.getText().toString();
                if (chk_agree.isChecked() == false)//若没勾选checkbox无法后续操作
                    Toast.makeText(this, "请确认是否已经阅读《我帮圈圈服务条款》", Toast.LENGTH_LONG).show();
                if (VerifyUtil.isMobileNO(mobiles) == false)//对手机号码严格验证，参见工具类中的正则表达式
                    Toast.makeText(this, "正确填写手机号，我们将向您发送一条验证码短信", Toast.LENGTH_LONG).show();
                if (VerifyUtil.isMobileNO(mobiles) == true && chk_agree.isChecked() == true) {
                    //当勾选中且号码正确，点击进行下一步操作
                    Toast.makeText(this, "已经向您手机发送验证码，请查看", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Register.this, Register.class);//TODO
                    startActivity(intent);
                }
        }

    }


}
