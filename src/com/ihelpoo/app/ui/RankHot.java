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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.AppException;
import com.ihelpoo.app.R;
import com.ihelpoo.app.adapter.ListViewQuestionAdapter;
import com.ihelpoo.app.adapter.ListViewSoftwareAdapter;
import com.ihelpoo.app.adapter.ListViewSoftwareCatalogAdapter;
import com.ihelpoo.app.bean.Post;
import com.ihelpoo.app.bean.PostList;
import com.ihelpoo.app.bean.SoftwareCatalogList;
import com.ihelpoo.app.bean.SoftwareList;
import com.ihelpoo.app.bean.SoftwareList.Software;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.widget.PullToRefreshListView;
import com.ihelpoo.app.widget.ScrollLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 软件库
 * @version 1.0
 * @created 2012-3-21
 */
public class RankHot extends BaseActivity{
	
	private ImageView mBack;
	private TextView mTitle;
	private ProgressBar mProgressbar;
	private ScrollLayout mScrollLayout;	
	
	private Button software_catalog;
	private Button software_recommend;
	private Button software_lastest;
	private Button software_hot;
	private Button software_china;
	
	private PullToRefreshListView mlvSoftware;
	private ListViewSoftwareAdapter lvSoftwareAdapter;
	private List<Software> lvSoftwareData = new ArrayList<Software>();
	private View lvSoftware_footer;
	private TextView lvSoftware_foot_more;
	private ProgressBar lvSoftware_foot_progress;
    private Handler mSoftwareHandler;
    private int lvSumData;
	
	private ListView mlvSoftwareCatalog;
	private ListViewSoftwareCatalogAdapter lvSoftwareCatalogAdapter;
	private List<SoftwareCatalogList.SoftwareType> lvSoftwareCatalogData = new ArrayList<SoftwareCatalogList.SoftwareType>();
    private Handler mSoftwareCatalogHandler;
    
	private ListView mlvSoftwareTag;
	private ListViewSoftwareCatalogAdapter lvSoftwareTagAdapter;
	private List<SoftwareCatalogList.SoftwareType> lvSoftwareTagData = new ArrayList<SoftwareCatalogList.SoftwareType>();
    private Handler mSoftwareTagHandler;
    
	private int curHeadTag = HEAD_TAG_CATALOG;//默认初始头部标签
	private int curScreen = SCREEN_CATALOG;//默认当前屏幕
	private int curSearchTag;//当前二级分类的Tag
	private int curLvSoftwareDataState;
	private String curTitleLV1;//当前一级分类标题
    
	private final static int HEAD_TAG_CATALOG = 0x001;
	private final static int HEAD_TAG_RECOMMEND = 0x002;
	private final static int HEAD_TAG_LASTEST = 0x003;
	private final static int HEAD_TAG_HOT = 0x004;
	private final static int HEAD_TAG_CHINA = 0x005;
	
	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	
	private final static int SCREEN_CATALOG = 0;
	private final static int SCREEN_TAG = 1;
	private final static int SCREEN_SOFTWARE = 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_software);
        
        this.initView();
        
        this.initData();
	}
	
	//初始化视图控件
    private void initView()
    {
    	mBack = (ImageView)findViewById(R.id.frame_software_head_back);
    	mTitle = (TextView)findViewById(R.id.frame_software_head_title);
    	mProgressbar = (ProgressBar)findViewById(R.id.frame_software_head_progress);
    	mScrollLayout = (ScrollLayout) findViewById(R.id.frame_software_scrolllayout);
    	
    	mBack.setOnClickListener(backClickListener);
    	
    	//禁用滑动
        mScrollLayout.setIsScroll(false);
    	
    	software_catalog = (Button)findViewById(R.id.frame_btn_software_catalog);
    	software_recommend = (Button)findViewById(R.id.frame_btn_software_recommend);
    	software_lastest = (Button)findViewById(R.id.frame_btn_software_lastest);
    	software_hot = (Button)findViewById(R.id.frame_btn_software_hot);
    	software_china = (Button)findViewById(R.id.frame_btn_software_china);
    	
    	software_catalog.setOnClickListener(this.softwareBtnClick(software_catalog,HEAD_TAG_CATALOG,"开源软件库"));
    	software_recommend.setOnClickListener(this.softwareBtnClick(software_recommend,HEAD_TAG_RECOMMEND,"每周推荐软件"));
    	software_lastest.setOnClickListener(this.softwareBtnClick(software_lastest,HEAD_TAG_LASTEST,"最新软件列表"));
    	software_hot.setOnClickListener(this.softwareBtnClick(software_hot,HEAD_TAG_HOT,"热门软件列表"));
    	software_china.setOnClickListener(this.softwareBtnClick(software_china,HEAD_TAG_CHINA,"国产软件列表"));
    	
    	software_catalog.setEnabled(false);

    	this.initSoftwareListView();
    }


    private ListViewQuestionAdapter lvRankAdapter;
    private View lvRank_footer;
    private TextView lvRank_foot_more;
    private ProgressBar lvRank_foot_progress;
    private PullToRefreshListView lvRank;
    private List<Post> lvRankData = new ArrayList<Post>();
    private int curRankCatalog = PostList.CATALOG_ASK;
    private int lvRankSumData;
    private Handler lvRankHandler;
    //初始化软件listview
    private void initSoftwareListView()
    {
        lvRankAdapter = new ListViewQuestionAdapter(this, lvRankData,
                R.layout.question_listitem);
        lvRank_footer = getLayoutInflater().inflate(
                R.layout.listview_footer, null);
        lvRank_foot_more = (TextView) lvRank_footer
                .findViewById(R.id.listview_foot_more);
        lvRank_foot_progress = (ProgressBar) lvRank_footer
                .findViewById(R.id.listview_foot_progress);
        lvRank = (PullToRefreshListView) findViewById(R.id.frame_listview_rank);
        lvRank.addFooterView(lvRank_footer);// 添加底部视图 必须在setAdapter前
        lvRank.setAdapter(lvRankAdapter);
        lvRank
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // 点击头部、底部栏无效
                        if (position == 0 || view == lvRank_footer)
                            return;

                        Post post = null;
                        // 判断是否是TextView
                        if (view instanceof TextView) {
                            post = (Post) view.getTag();
                        } else {
                            TextView tv = (TextView) view
                                    .findViewById(R.id.question_listitem_title);
                            post = (Post) tv.getTag();
                        }
                        if (post == null)
                            return;

                        // 跳转到问答详情
//                        UIHelper.showQuestionDetail(view.getContext(),
//                                post.getId());
                        UIHelper.showTweetDetail(view.getContext(), post.getId(), false);
                    }
                });
        lvRank.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvRank.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvRankData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvRank_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvRank.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvRank.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvRank_foot_more.setText(R.string.load_ing);
                    lvRank_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvRankSumData / AppContext.PAGE_SIZE;
//                    loadLvQuestionData(curRankCatalog, pageIndex, lvRankHandler, UIHelper.LISTVIEW_ACTION_SCROLL);

                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvRank.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

            }
        });
        lvRank
                .setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                    public void onRefresh() {
//                        loadLvQuestionData(curRankCatalog, 0, lvRankHandler, UIHelper.LISTVIEW_ACTION_REFRESH);

                    }
                });
    }
    
    //初始化控件数据
  	private void initData()
  	{
  		this.loadLvSoftwareCatalogData(0, mSoftwareCatalogHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
  	}
  	
  	/**
     * 头部按钮展示
     * @param type
     */
    private void headButtonSwitch(int type) {
    	switch (type) {
    	case DATA_LOAD_ING:
			mProgressbar.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_COMPLETE:
			mProgressbar.setVisibility(View.GONE);
			break;
		}
    }
  	
  	private View.OnClickListener softwareBtnClick(final Button btn,final int tag,final String title){
    	return new View.OnClickListener() {
			public void onClick(View v) {
		    	if(btn == software_catalog)
		    		software_catalog.setEnabled(false);
		    	else
		    		software_catalog.setEnabled(true);
		    	if(btn == software_recommend)
		    		software_recommend.setEnabled(false);
		    	else
		    		software_recommend.setEnabled(true);
		    	if(btn == software_lastest)
		    		software_lastest.setEnabled(false);
		    	else
		    		software_lastest.setEnabled(true);	
		    	if(btn == software_hot)
		    		software_hot.setEnabled(false);
		    	else
		    		software_hot.setEnabled(true);
		    	if(btn == software_china)
		    		software_china.setEnabled(false);
		    	else
		    		software_china.setEnabled(true);	    	
		    	
				curHeadTag = tag;	
		    	
		    	if(btn == software_catalog)
		    	{			    		
		    		curScreen = SCREEN_CATALOG;
		    		if(lvSoftwareCatalogData.size() == 0)
		    			loadLvSoftwareCatalogData(0, mSoftwareCatalogHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);		
		    	}
		    	else
		    	{		    		
		    		curScreen = SCREEN_SOFTWARE;
		    		loadLvSoftwareData(tag, 0, mSoftwareHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);	
		    	}			
	    	
		    	mTitle.setText(title);
		    	mScrollLayout.setToScreen(curScreen);
			}
		};
    }
	
  	/**
     * 线程加载软件分类列表数据
     * @param tag 第一级:0 第二级:tag
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvSoftwareCatalogData(final int tag,final Handler handler,final int action){  
		headButtonSwitch(DATA_LOAD_ING);
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					SoftwareCatalogList softwareCatalogList = ((AppContext)getApplication()).getSoftwareCatalogList(tag);
					msg.what = softwareCatalogList.getSoftwareTypelist().size();
					msg.obj = softwareCatalogList;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//告知handler当前action
                handler.sendMessage(msg);
			}
		}.start();
	}
	
  	/**
     * 线程加载软件分类二级列表数据
     * @param tag 第一级:0 第二级:tag
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvSoftwareTagData(final int searchTag,final int pageIndex,final Handler handler,final int action){  
		headButtonSwitch(DATA_LOAD_ING);
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					SoftwareList softwareList = ((AppContext)getApplication()).getSoftwareTagList(searchTag, pageIndex, isRefresh);
					msg.what = softwareList.getSoftwarelist().size();
					msg.obj = softwareList;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//告知handler当前action
                handler.sendMessage(msg);
			}
		}.start();
	}
	
  	/**
     * 线程加载软件列表数据
     * @param searchTag 软件分类 推荐:recommend 最新:time 热门:view 国产:list_cn
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvSoftwareData(final int tag,final int pageIndex,final Handler handler,final int action){  
		
		String _searchTag = "";
		
		switch (tag) {
		case HEAD_TAG_RECOMMEND: 
			_searchTag = SoftwareList.TAG_RECOMMEND;
			break;
		case HEAD_TAG_LASTEST: 
			_searchTag = SoftwareList.TAG_LASTEST;
			break;
		case HEAD_TAG_HOT: 
			_searchTag = SoftwareList.TAG_HOT;
			break;
		case HEAD_TAG_CHINA: 
			_searchTag = SoftwareList.TAG_CHINA;
			break;
		}
		
		if(StringUtils.isEmpty(_searchTag)) return;
		
		final String searchTag = _searchTag;
		
		headButtonSwitch(DATA_LOAD_ING);
		
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					SoftwareList softwareList = ((AppContext)getApplication()).getSoftwareList(searchTag, pageIndex, isRefresh);
					msg.what = softwareList.getPageSize();
					msg.obj = softwareList;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//告知handler当前action
				if(curHeadTag == tag)
					handler.sendMessage(msg);
			}
		}.start();
	} 
	
	/**
	 * 返回事件
	 */
	private void back() {
		if(curHeadTag == HEAD_TAG_CATALOG) {
			switch (curScreen) {
			case SCREEN_SOFTWARE:
    			mTitle.setText(curTitleLV1);
				curScreen = SCREEN_TAG;
				mScrollLayout.scrollToScreen(SCREEN_TAG);
				break;
			case SCREEN_TAG:
				mTitle.setText("开源软件库");
				curScreen = SCREEN_CATALOG;
				mScrollLayout.scrollToScreen(SCREEN_CATALOG);
				break;
			case SCREEN_CATALOG:
				finish();
				break;
			}
			
		}else{
			finish();
		}
	}

	private View.OnClickListener backClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			back();
		}		
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		}
		return false;
	}
	
	
}
