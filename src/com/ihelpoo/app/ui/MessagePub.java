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

import com.ihelpoo.app.AppConfig;
import com.ihelpoo.app.AppException;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.R;
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.common.UIHelper;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 发表留言
 * @version 1.0
 * @created 2012-3-21
 */
public class MessagePub extends BaseActivity{
	
	private Button mBack;
	private TextView mReceiver;
	private EditText mContent;
	private Button mPublish;
    private ProgressDialog mProgress;
    private InputMethodManager imm;
    private String tempMessageKey = AppConfig.TEMP_MESSAGE;
	
	private int _uid;
	private int _friendid;
	private String _friendname;
	private String _content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_pub);
		
		this.initView();
	}
	
    //初始化视图控件
    private void initView()
    {
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
		_uid = getIntent().getIntExtra("user_id", 0);
		_friendid = getIntent().getIntExtra("friend_id", 0);
		_friendname = getIntent().getStringExtra("friend_name");
    	
		if(_friendid > 0) tempMessageKey = AppConfig.TEMP_MESSAGE + "_" + _friendid;
		
    	mBack = (Button)findViewById(R.id.message_pub_back);
    	mPublish = (Button)findViewById(R.id.message_pub_publish);
    	mContent = (EditText)findViewById(R.id.message_pub_content);
    	mReceiver = (TextView)findViewById(R.id.message_pub_receiver);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);
    	//编辑器添加文本监听
    	mContent.addTextChangedListener(UIHelper.getTextWatcher(this, tempMessageKey));
    	
    	//显示临时编辑内容
    	UIHelper.showTempEditContent(this, mContent, tempMessageKey);
    	
    	mReceiver.setText("发给 "+_friendname);
    }    
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//隐藏软键盘
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			_content = mContent.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "请输入留言内容");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(MessagePub.this);
				return;
			}
			
			mProgress = ProgressDialog.show(v.getContext(), null, "发送中···",true,true); 
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					
					if(mProgress!=null)mProgress.dismiss();
					
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(MessagePub.this, res.getErrorMessage());
						if(res.OK()){
							//发送通知广播
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(MessagePub.this, res.getNotice());
							}
							//清除之前保存的编辑内容
							ac.removeProperty(tempMessageKey);
							//返回刚刚发表的评论
							Intent intent = new Intent();
							intent.putExtra("COMMENT_SERIALIZABLE", res.getComment());
							setResult(RESULT_OK, intent);
							finish();
						}
					}
					else {
						((AppException)msg.obj).makeToast(MessagePub.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg =new Message();
					try {
						Result res = ac.pubMessage(_uid, _friendid, _content);
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
	};
}
