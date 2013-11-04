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
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.widget.LinkView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * 发表评论
 * @version 1.0
 * @created 2012-3-21
 */
public class CommentPub extends BaseActivity{

	public final static int CATALOG_NEWS = 1;
	public final static int CATALOG_POST = 2;
	public final static int CATALOG_TWEET = 3;
	public final static int CATALOG_ACTIVE = 4;
	public final static int CATALOG_MESSAGE = 4;//动态与留言都属于消息中心
	public final static int CATALOG_BLOG = 5;
	
	private Button mBack;
	private EditText mContent;
	private CheckBox mZone;
	private Button mPublish;
	private LinkView mQuote;
    private ProgressDialog mProgress;
	
	private int _catalog;
	private int _id;
	private int _uid;
	private String _content;
	private int _isPostToMyZone;
	
	//-------对评论回复还需加2变量------
	private int _replyid;//被回复的单个评论id
	private int _authorid;//该评论的原始作者id
    private String _author;//原始作者昵称
    private boolean _isHelp;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_pub);
		
		this.initView();
		
	}
	
    //初始化视图控件
    private void initView()
    {
		_id = getIntent().getIntExtra("id", 0);
		_catalog = getIntent().getIntExtra("catalog", 0);
		_replyid = getIntent().getIntExtra("reply_id", 0);
		_authorid = getIntent().getIntExtra("author_id", 0);
        _author =  getIntent().getStringExtra("author");
        _isHelp = getIntent().getBooleanExtra("is_help", false);
    	
    	mBack = (Button)findViewById(R.id.comment_list_back);
    	mPublish = (Button)findViewById(R.id.comment_pub_publish);
    	mContent = (EditText)findViewById(R.id.comment_pub_content);
    	mZone = (CheckBox)findViewById(R.id.comment_pub_zone);
//    	if(_catalog == CommentList.CATALOG_TWEET){
//    		mZone.setVisibility(View.VISIBLE);
//    	}
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);    	
    	
    	mQuote = (LinkView)findViewById(R.id.comment_pub_quote);
    	mQuote.setText(UIHelper.parseQuoteSpan(getIntent().getStringExtra("author"),getIntent().getStringExtra("content")));
    	mQuote.parseLinkText();
    }
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			_content = mContent.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(CommentPub.this);
				return;
			}
			
			if(mZone.isChecked())
				_isPostToMyZone = 1;
				
			_uid = ac.getLoginUid();
			
	    	mProgress = ProgressDialog.show(v.getContext(), null, "发表中···",true,true); 			
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(mProgress!=null)mProgress.dismiss();
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(CommentPub.this, res.getErrorMessage());
						if(res.OK()){
							//发送通知广播
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(CommentPub.this, res.getNotice());
							}
							//返回刚刚发表的评论
							Intent intent = new Intent();
							intent.putExtra("COMMENT_SERIALIZABLE", res.getComment());
							setResult(RESULT_OK, intent);
							//跳转到文章详情
							finish();
						}
					}
					else {
						((AppException)msg.obj).makeToast(CommentPub.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg = new Message();
					Result res = new Result();
					try {
						//发表评论
						if(_replyid == 0){
							res = ac.pubComment(_catalog, _id, _uid, _content, false);
						}
						//对评论进行回复
						else if(_replyid > 0){
							if(_catalog == CATALOG_BLOG)
								res = ac.replyBlogComment(_id, _uid, _content, _replyid, _authorid);
							else
								res = ac.replyComment(_id, _catalog, _replyid, _authorid, _author, _uid, _content, _isHelp);
						}
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
