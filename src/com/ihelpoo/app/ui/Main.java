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

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ihelpoo.app.adapter.ListViewNoticeAdapter;
import com.ihelpoo.app.adapter.ListViewWordAdapter;
import com.ihelpoo.app.bean.WordList;
import com.ihelpoo.app.bean.MessageList;
import com.ihelpoo.app.bean.Post;
import com.ihelpoo.app.bean.TweetList;
import com.ihelpoo.app.widget.NewDataToast;
import com.ihelpoo.app.AppConfig;
import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.AppException;
import com.ihelpoo.app.R;
import com.ihelpoo.app.adapter.ListViewBlogAdapter;
import com.ihelpoo.app.adapter.ListViewMessageAdapter;
import com.ihelpoo.app.adapter.ListViewNewsAdapter;
import com.ihelpoo.app.adapter.ListViewQuestionAdapter;
import com.ihelpoo.app.adapter.ListViewTweetAdapter;
import com.ihelpoo.app.bean.Active;
import com.ihelpoo.app.bean.Blog;
import com.ihelpoo.app.bean.BlogList;
import com.ihelpoo.app.bean.Messages;
import com.ihelpoo.app.bean.News;
import com.ihelpoo.app.bean.NewsList;
import com.ihelpoo.app.bean.Notice;
import com.ihelpoo.app.bean.PostList;
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.bean.Tweet;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.common.UpdateManager;
import com.ihelpoo.app.widget.BadgeView;
import com.ihelpoo.app.widget.PullToRefreshListView;
import com.ihelpoo.app.widget.ScrollLayout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 应用程序首页
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class Main extends BaseActivity {

    public static final int QUICKACTION_LOGIN_OR_LOGOUT = 0;
    public static final int QUICKACTION_USERINFO = 1;
    public static final int QUICKACTION_SOFTWARE = 2;
    public static final int QUICKACTION_SEARCH = 3;
    public static final int QUICKACTION_SETTING = 4;
    public static final int QUICKACTION_EXIT = 5;

    private ScrollLayout mScrollLayout;
    private RadioButton[] mButtons;
    private String[] mHeadTitles;
    private int mViewCount;
    private int mCurSel;

    private ImageView mHeadLogo;
    private TextView mHeadTitle;
    private ProgressBar mHeadProgress;
    private ImageButton mHead_search;
    private ImageButton mHeadPub_post;
    private ImageButton mHeadPub_tweet;

    private int curNestCatalog = NewsList.CATALOG_ALL;
    private int curRankCatalog = PostList.CATALOG_ASK;
    private int curHomeCatalog = TweetList.CATALOG_STREAM;
    private int curWordCatalog = WordList.CATALOG_SYSTEM;

    private PullToRefreshListView lvNest;
    private PullToRefreshListView lvBlog;
    private PullToRefreshListView lvRank;
    private PullToRefreshListView lvHome;
    private PullToRefreshListView lvWord;
    private PullToRefreshListView lvChat;
    private PullToRefreshListView lvNotice;

    private ListViewNewsAdapter lvNestAdapter;
    private ListViewBlogAdapter lvBlogAdapter;
    private ListViewQuestionAdapter lvRankAdapter;
    private ListViewTweetAdapter lvTweetAdapter;
    private ListViewWordAdapter lvWordAdapter;
    private ListViewMessageAdapter lvMsgAdapter;
    private ListViewNoticeAdapter lvNoticeAdapter;

    private List<News> lvNestData = new ArrayList<News>();
    private List<Blog> lvBlogData = new ArrayList<Blog>();
    private List<Post> lvRankData = new ArrayList<Post>();
    private List<Tweet> lvHomeData = new ArrayList<Tweet>();
    private List<Active> lvWordData = new ArrayList<Active>();
    private List<Messages> lvMsgData = new ArrayList<Messages>();
    private List<News> lvNoticeData = new ArrayList<News>();

    private Handler lvNestHandler;
    private Handler lvBlogHandler;
    private Handler lvRankHandler;
    private Handler lvHomeHandler;
    private Handler lvWordHandler;
    private Handler lvMsgHandler;
    private Handler lvNoticeHandler;

    private int lvNestSumData;
    private int lvBlogSumData;
    private int lvRankSumData;
    private int lvTweetSumData;
    private int lvWordSumData;
    private int lvMsgSumData;
    private int lvNoticeSumData;

    private RadioButton fbHome;
    private RadioButton fbNest;
    private RadioButton fbRank;
    private RadioButton fbWord;
    private ImageView fbSetting;

    private Button framebtn_Nest_lastest;
    private Button framebtn_Nest_blog;
    private Button framebtn_Nest_recommend;
    private Button framebtn_Rank_ask;
    private Button framebtn_Rank_share;
    private Button framebtn_Rank_other;
    private Button framebtn_Rank_job;
    private Button framebtn_Rank_site;
    private Button framebtn_Home_stream;
    private Button framebtn_Home_mine;
    private Button framebtn_Home_help;
    private Button framebtn_Word_system;
    private Button framebtn_Word_atme;
    private Button framebtn_Word_comment;
    private Button framebtn_Word_active;
    private Button framebtn_Word_chat;

    private View lvNest_footer;
    private View lvBlog_footer;
    private View lvRank_footer;
    private View lvTweet_footer;
    private View lvWord_footer;
    private View lvMsg_footer;
    private View lvNotice_footer;

    private TextView lvNest_foot_more;
    private TextView lvBlog_foot_more;
    private TextView lvRank_foot_more;
    private TextView lvTweet_foot_more;
    private TextView lvWord_foot_more;
    private TextView lvMsg_foot_more;
    private TextView lvNotice_foot_more;

    private ProgressBar lvNest_foot_progress;
    private ProgressBar lvBlog_foot_progress;
    private ProgressBar lvRank_foot_progress;
    private ProgressBar lvTweet_foot_progress;
    private ProgressBar lvWord_foot_progress;
    private ProgressBar lvMsg_foot_progress;
    private ProgressBar lvNotice_foot_progress;

    public static BadgeView bv_active;
    public static BadgeView bv_chat;
    public static BadgeView bv_atme;
    public static BadgeView bv_comment;

    private QuickActionWidget mGrid;// 快捷栏控件

    private boolean isClearNotice = false;
    private int curClearNoticeType = 0;

    private TweetReceiver tweetReceiver;// 动弹发布接收器
    private AppContext appContext;// 全局Context

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // 注册广播接收器
        tweetReceiver = new TweetReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.ihelpoo.app.action.APP_TWEETPUB");
        registerReceiver(tweetReceiver, filter);

        appContext = (AppContext) getApplication();
        // 网络连接判断
        if (!appContext.isNetworkConnected())
            UIHelper.ToastMessage(this, R.string.network_not_connected);
        // 初始化登录
        appContext.initLoginInfo();

        this.initHeadView();
        this.initFootBar();
        this.initPageScroll();
        this.initFrameButton();
        this.initBadgeView();
        this.initQuickActionGrid();
        this.initFrameListView();

        // 检查新版本
        if (appContext.isCheckUp()) {
            UpdateManager.getUpdateManager().checkAppUpdate(this, false);
        }

        // 启动轮询通知信息
        this.foreachUserNotice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewCount == 0)
            mViewCount = 4;

        if (mCurSel == 0 && !fbHome.isChecked()) {// here it is the default config of first screen!!!
            fbHome.setChecked(true);
            fbRank.setChecked(false);
            fbNest.setChecked(false);
            fbWord.setChecked(false);
        }
        // 读取左右滑动配置
        mScrollLayout.setIsScroll(appContext.isScroll());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(tweetReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getBooleanExtra("LOGIN", false)) {
            // 加载动弹、动态及留言(当前动弹的catalog大于0表示用户的uid)
            if (lvHomeData.isEmpty() && curHomeCatalog > 0 && mCurSel == 2) {
                this.loadLvTweetData(curHomeCatalog, 0, lvHomeHandler, UIHelper.LISTVIEW_ACTION_INIT);
            } else if (mCurSel == 3) {
                if (lvWordData.isEmpty()) {
                    this.loadLvWordData(curWordCatalog, 0, lvWordHandler, UIHelper.LISTVIEW_ACTION_INIT);
                }
                if(lvNoticeData.isEmpty()){
                    this.loadLvNoticeData(curWordCatalog, 0, lvNoticeHandler, UIHelper.LISTVIEW_ACTION_INIT);
                }
                if (lvMsgData.isEmpty()) {
                    this.loadLvMsgData(0, lvMsgHandler, UIHelper.LISTVIEW_ACTION_INIT);
                }
            }
        } else if (intent.getBooleanExtra("NOTICE", false)) {
            // 查看最新信息
            mScrollLayout.scrollToScreen(3);
        }
    }

    public class TweetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            int what = intent.getIntExtra("MSG_WHAT", 0);
            if (what == 1) {
                Result res = (Result) intent.getSerializableExtra("RESULT");
                UIHelper.ToastMessage(context, res.getErrorMessage(), 1000);
                if (res.OK()) {
                    // 发送通知广播
                    if (res.getNotice() != null) {
                        UIHelper.sendBroadCast(context, res.getNotice());
                    }
                    // 发完动弹后-刷新最新动弹、我的动弹&最新动态(当前界面必须是动弹|动态)
                    if (curHomeCatalog >= 0 && mCurSel == 2) {
                        loadLvTweetData(curHomeCatalog, 0, lvHomeHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
                    } else if (curWordCatalog == WordList.CATALOG_SYSTEM && mCurSel == 3) {

                        loadLvWordData(curWordCatalog, 0, lvWordHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
                    }
                }
            } else {
                final Tweet tweet = (Tweet) intent
                        .getSerializableExtra("TWEET");
                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            Result res = (Result) msg.obj;
                            UIHelper.ToastMessage(context, res.getErrorMessage(), 1000);
                            if (res.OK()) {
                                // 发送通知广播
                                if (res.getNotice() != null) {
                                    UIHelper.sendBroadCast(context, res.getNotice());
                                }
                                // 发完动弹后-刷新最新、我的动弹&最新动态
                                if (curHomeCatalog >= 0 && mCurSel == 2) {
                                    loadLvTweetData(curHomeCatalog, 0, lvHomeHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
                                } else if (curWordCatalog == WordList.CATALOG_SYSTEM && mCurSel == 3) {
                                    loadLvWordData(curWordCatalog, 0, lvWordHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
                                }
                                if (TweetPub.mContext != null) {
                                    // 清除动弹保存的临时编辑内容
                                    appContext.removeProperty(AppConfig.TEMP_TWEET, AppConfig.TEMP_TWEET_IMAGE);
                                    ((Activity) TweetPub.mContext).finish();
                                }
                            }
                        } else {
                            ((AppException) msg.obj).makeToast(context);
                            if (TweetPub.mContext != null && TweetPub.mMessage != null)
                                TweetPub.mMessage.setVisibility(View.GONE);
                        }
                    }
                };
                Thread thread = new Thread() {
                    public void run() {
                        Message msg = new Message();
                        try {
                            Result res = appContext.pubTweet(tweet);
                            msg.what = 1;
                            msg.obj = res;
                        } catch (AppException e) {
                            e.printStackTrace();
                            msg.what = -1;
                            msg.obj = e;
                        }
                        handler.sendMessage(msg);
                    }
                };
                if (TweetPub.mContext != null)
                    UIHelper.showResendTweetDialog(TweetPub.mContext, thread);
                else
                    UIHelper.showResendTweetDialog(context, thread);
            }
        }
    }

    /**
     * 初始化快捷栏
     */
    private void initQuickActionGrid() {
        mGrid = new QuickActionGrid(this);
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_login, R.string.main_menu_login));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_myinfo, R.string.main_menu_myinfo));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_software, R.string.main_menu_software));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_search, R.string.main_menu_search));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_setting, R.string.main_menu_setting));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_exit, R.string.main_menu_exit));
        mGrid.setOnQuickActionClickListener(mActionListener);
    }

    /**
     * 快捷栏item点击事件
     */
    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
            switch (position) {
                case QUICKACTION_LOGIN_OR_LOGOUT:// 用户登录-注销
                    UIHelper.loginOrLogout(Main.this);
                    break;
                case QUICKACTION_USERINFO:// 我的资料
                    UIHelper.showUserInfo(Main.this);
                    break;
                case QUICKACTION_SOFTWARE:// 开源软件
                    UIHelper.showSoftware(Main.this);
                    break;
                case QUICKACTION_SEARCH:// 搜索
                    UIHelper.showSearch(Main.this);
                    break;
                case QUICKACTION_SETTING:// 设置
                    UIHelper.showSetting(Main.this);
                    break;
                case QUICKACTION_EXIT:// 退出
                    UIHelper.Exit(Main.this);
                    break;
            }
        }
    };

    /**
     * 初始化所有ListView
     */
    private void initFrameListView() {
        // 初始化listview控件
        this.initQuestionListView();
        this.initNewsListView();
        this.initBlogListView();
        this.initTweetListView();
        this.initWordListView();
        this.initMsgListView();
        this.initNoticeListView();
        // 加载listview数据
        this.initFrameListViewData();
    }

    /**
     * 初始化所有ListView数据
     */
    private void initFrameListViewData() {
        // 初始化Handler
        lvNestHandler = this.getLvHandler(lvNest, lvNestAdapter,
                lvNest_foot_more, lvNest_foot_progress, AppContext.PAGE_SIZE);
        lvBlogHandler = this.getLvHandler(lvBlog, lvBlogAdapter,
                lvBlog_foot_more, lvBlog_foot_progress, AppContext.PAGE_SIZE);
        lvRankHandler = this.getLvHandler(lvRank, lvRankAdapter,
                lvRank_foot_more, lvRank_foot_progress,
                AppContext.PAGE_SIZE);
        lvHomeHandler = this.getLvHandler(lvHome, lvTweetAdapter,
                lvTweet_foot_more, lvTweet_foot_progress, AppContext.PAGE_SIZE);
        lvWordHandler = this.getLvHandler(lvWord, lvWordAdapter,
                lvWord_foot_more, lvWord_foot_progress,
                AppContext.PAGE_SIZE);
        lvMsgHandler = this.getLvHandler(lvChat, lvMsgAdapter, lvMsg_foot_more,
                lvMsg_foot_progress, AppContext.PAGE_SIZE);
        lvNoticeHandler = this.getLvHandler(lvNotice, lvNoticeAdapter,
                lvNotice_foot_more, lvNotice_foot_progress, AppContext.PAGE_SIZE);

        // 加载资讯数据
//		if (lvNestData.isEmpty()) {
//			loadLvNewsData(curNestCatalog, 0, lvNestHandler, UIHelper.LISTVIEW_ACTION_INIT);
//        }

//        if(lvRankData.isEmpty()){
//            loadLvQuestionData(curRankCatalog, 0,lvRankHandler,UIHelper.LISTVIEW_ACTION_INIT);
//        }
        if (lvHomeData.isEmpty()) {
            loadLvTweetData(curHomeCatalog, 0, lvHomeHandler, UIHelper.LISTVIEW_ACTION_INIT);
        }

    }

    /**
     * 初始化通知或活跃列表
     */
    private void initNoticeListView() {
        lvNoticeAdapter = new ListViewNoticeAdapter(this, lvNoticeData, R.layout.notice_listitem);
        lvNotice_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvNotice_foot_more = (TextView) lvNotice_footer.findViewById(R.id.listview_foot_more);
        lvNotice_foot_progress = (ProgressBar) lvNotice_footer.findViewById(R.id.listview_foot_progress);
        lvNotice = (PullToRefreshListView) findViewById(R.id.frame_listview_notice);
        lvNotice.addFooterView(lvNotice_footer);// 添加底部视图 必须在setAdapter前
        lvNotice.setAdapter(lvNoticeAdapter);

        lvNotice.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvNotice.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvNoticeData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvNotice_footer) == view.getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvNotice.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvNotice.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvNotice_foot_more.setText(R.string.load_ing);
                    lvNotice_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvNoticeSumData / AppContext.PAGE_SIZE;
                    loadLvNoticeData(curWordCatalog, pageIndex, lvNoticeHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvNotice.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvNotice.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                loadLvNoticeData(curWordCatalog, 0, lvNoticeHandler,
                        UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }

    /**
     * 初始化新闻列表
     */
    private void initNewsListView() {
        lvNestAdapter = new ListViewNewsAdapter(this, lvNestData,
                R.layout.news_listitem);
        lvNest_footer = getLayoutInflater().inflate(R.layout.listview_footer,
                null);
        lvNest_foot_more = (TextView) lvNest_footer
                .findViewById(R.id.listview_foot_more);
        lvNest_foot_progress = (ProgressBar) lvNest_footer
                .findViewById(R.id.listview_foot_progress);
        lvNest = (PullToRefreshListView) findViewById(R.id.frame_listview_nest);
        lvNest.addFooterView(lvNest_footer);// 添加底部视图 必须在setAdapter前
        lvNest.setAdapter(lvNestAdapter);
        lvNest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvNest_footer)
                    return;

                News news = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    news = (News) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.news_listitem_title);
                    news = (News) tv.getTag();
                }
                if (news == null)
                    return;

                // 跳转到新闻详情
                UIHelper.showNewsRedirect(view.getContext(), news);
            }
        });
        lvNest.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvNest.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvNestData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvNest_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvNest.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvNest.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvNest_foot_more.setText(R.string.load_ing);
                    lvNest_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvNestSumData / AppContext.PAGE_SIZE;
                    loadLvNewsData(curNestCatalog, pageIndex, lvNestHandler,
                            UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvNest.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvNest.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                loadLvNewsData(curNestCatalog, 0, lvNestHandler,
                        UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }

    /**
     * 初始化博客列表
     */
    private void initBlogListView() {
        lvBlogAdapter = new ListViewBlogAdapter(this, BlogList.CATALOG_LATEST,
                lvBlogData, R.layout.blog_listitem);
        lvBlog_footer = getLayoutInflater().inflate(R.layout.listview_footer,
                null);
        lvBlog_foot_more = (TextView) lvBlog_footer
                .findViewById(R.id.listview_foot_more);
        lvBlog_foot_progress = (ProgressBar) lvBlog_footer
                .findViewById(R.id.listview_foot_progress);
        lvBlog = (PullToRefreshListView) findViewById(R.id.frame_listview_blog);
        lvBlog.addFooterView(lvBlog_footer);// 添加底部视图 必须在setAdapter前
        lvBlog.setAdapter(lvBlogAdapter);
        lvBlog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvBlog_footer)
                    return;

                Blog blog = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    blog = (Blog) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.blog_listitem_title);
                    blog = (Blog) tv.getTag();
                }
                if (blog == null)
                    return;

                // 跳转到博客详情
                UIHelper.showUrlRedirect(view.getContext(), blog.getUrl());
            }
        });
        lvBlog.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvBlog.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvBlogData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvBlog_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvBlog.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvBlog.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvBlog_foot_more.setText(R.string.load_ing);
                    lvBlog_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvBlogSumData / AppContext.PAGE_SIZE;
                    loadLvBlogData(curNestCatalog, pageIndex, lvBlogHandler,
                            UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvBlog.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvBlog.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                loadLvBlogData(curNestCatalog, 0, lvBlogHandler,
                        UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }

    /**
     * 初始化帖子列表
     */
    private void initQuestionListView() {
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
                        UIHelper.showTweetDetail(view.getContext(), post.getId());
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
                    loadLvQuestionData(curRankCatalog, pageIndex, lvRankHandler, UIHelper.LISTVIEW_ACTION_SCROLL);

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
                        loadLvQuestionData(curRankCatalog, 0, lvRankHandler, UIHelper.LISTVIEW_ACTION_REFRESH);

                    }
                });
    }

    /**
     * 初始化动弹列表
     */
    private void initTweetListView() {
        lvTweetAdapter = new ListViewTweetAdapter(this, lvHomeData, R.layout.tweet_listitem);
        lvTweet_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvTweet_foot_more = (TextView) lvTweet_footer.findViewById(R.id.listview_foot_more);
        lvTweet_foot_progress = (ProgressBar) lvTweet_footer.findViewById(R.id.listview_foot_progress);
        lvHome = (PullToRefreshListView) findViewById(R.id.frame_listview_tweet);
        lvHome.addFooterView(lvTweet_footer);// 添加底部视图 必须在setAdapter前
        lvHome.setAdapter(lvTweetAdapter);
        lvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvTweet_footer)
                    return;

                Tweet tweet = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    tweet = (Tweet) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.tweet_listitem_username);
                    tweet = (Tweet) tv.getTag();
                }
                if (tweet == null)
                    return;
                // 跳转到动弹详情&评论页面
                UIHelper.showTweetDetail(view.getContext(), tweet.getId());
            }
        });
        lvHome.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvHome.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvHomeData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvTweet_footer) == view.getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvHome.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvHome.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvTweet_foot_more.setText(R.string.load_ing);
                    lvTweet_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvTweetSumData / AppContext.PAGE_SIZE;
                    loadLvTweetData(curHomeCatalog, pageIndex, lvHomeHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvHome.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvHome.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvTweet_footer)
                    return false;

                Tweet _tweet = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    _tweet = (Tweet) view.getTag();
                } else {
                    TextView tv = (TextView) view.findViewById(R.id.tweet_listitem_username);
                    _tweet = (Tweet) tv.getTag();
                }
                if (_tweet == null)
                    return false;

                final Tweet tweet = _tweet;

                // 删除操作
                // if(appContext.getLoginUid() == tweet.getAuthorId()) {
                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            Result res = (Result) msg.obj;
                            if (res.OK()) {
                                lvHomeData.remove(tweet);
                                lvTweetAdapter.notifyDataSetChanged();
                            }
                            UIHelper.ToastMessage(Main.this,
                                    res.getErrorMessage());
                        } else {
                            ((AppException) msg.obj).makeToast(Main.this);
                        }
                    }
                };
                Thread thread = new Thread() {
                    public void run() {
                        Message msg = new Message();
                        try {
                            Result res = appContext.delTweet(
                                    appContext.getLoginUid(), tweet.getId());
                            msg.what = 1;
                            msg.obj = res;
                        } catch (AppException e) {
                            e.printStackTrace();
                            msg.what = -1;
                            msg.obj = e;
                        }
                        handler.sendMessage(msg);
                    }
                };
                UIHelper.showTweetOptionDialog(Main.this, thread);
                // } else {
                // UIHelper.showTweetOptionDialog(Main.this, null);
                // }
                return true;
            }
        });
        lvHome.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                loadLvTweetData(curHomeCatalog, 0, lvHomeHandler,
                        UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }

    /**
     * 初始化动态列表
     */
    private void initWordListView() {
        lvWordAdapter = new ListViewWordAdapter(this, lvWordData,
                R.layout.word_listitem);
        lvWord_footer = getLayoutInflater().inflate(R.layout.listview_footer,
                null);
        lvWord_foot_more = (TextView) lvWord_footer
                .findViewById(R.id.listview_foot_more);
        lvWord_foot_progress = (ProgressBar) lvWord_footer
                .findViewById(R.id.listview_foot_progress);
        lvWord = (PullToRefreshListView) findViewById(R.id.frame_listview_word);
        lvWord.addFooterView(lvWord_footer);// 添加底部视图 必须在setAdapter前
        lvWord.setAdapter(lvWordAdapter);
        lvWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvWord_footer)
                    return;

                Active active = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    active = (Active) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.word_listitem_username);
                    active = (Active) tv.getTag();
                }
                if (active == null)
                    return;

                // 跳转
                UIHelper.showActiveRedirect(view.getContext(), active);
            }
        });
        lvWord.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvWord.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvWordData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvWord_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvWord.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvWord.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvWord_foot_more.setText(R.string.load_ing);
                    lvWord_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvWordSumData / AppContext.PAGE_SIZE;
                    loadLvWordData(curWordCatalog, pageIndex,
                            lvWordHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvWord.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvWord.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                // 处理通知信息
                if (curWordCatalog == WordList.CATALOG_ATME
                        && bv_atme.isShown()) {
                    isClearNotice = true;
                    curClearNoticeType = Notice.TYPE_ATME;
                } else if (curWordCatalog == WordList.CATALOG_COMMENT
                        && bv_comment.isShown()) {
                    isClearNotice = true;
                    curClearNoticeType = Notice.TYPE_COMMENT;
                }
                // 刷新数据
                loadLvWordData(curWordCatalog, 0, lvWordHandler,
                        UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }

    /**
     * 初始化留言列表
     */
    private void initMsgListView() {
        lvMsgAdapter = new ListViewMessageAdapter(this, lvMsgData,
                R.layout.message_listitem);
        lvMsg_footer = getLayoutInflater().inflate(R.layout.listview_footer,
                null);
        lvMsg_foot_more = (TextView) lvMsg_footer
                .findViewById(R.id.listview_foot_more);
        lvMsg_foot_progress = (ProgressBar) lvMsg_footer
                .findViewById(R.id.listview_foot_progress);
        lvChat = (PullToRefreshListView) findViewById(R.id.frame_listview_message);
        lvChat.addFooterView(lvMsg_footer);// 添加底部视图 必须在setAdapter前
        lvChat.setAdapter(lvMsgAdapter);
        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvMsg_footer)
                    return;

                Messages msg = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    msg = (Messages) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.message_listitem_username);
                    msg = (Messages) tv.getTag();
                }
                if (msg == null)
                    return;

                // 跳转到留言详情
                UIHelper.showMessageDetail(view.getContext(),
                        msg.getFriendId(), msg.getFriendName());
            }
        });
        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvChat.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvMsgData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvMsg_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvChat.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvChat.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvMsg_foot_more.setText(R.string.load_ing);
                    lvMsg_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvMsgSumData / AppContext.PAGE_SIZE;
                    loadLvMsgData(pageIndex, lvMsgHandler,
                            UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvChat.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvChat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvMsg_footer)
                    return false;

                Messages _msg = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    _msg = (Messages) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.message_listitem_username);
                    _msg = (Messages) tv.getTag();
                }
                if (_msg == null)
                    return false;

                final Messages message = _msg;

                // 选择操作
                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            Result res = (Result) msg.obj;
                            if (res.OK()) {
                                lvMsgData.remove(message);
                                lvMsgAdapter.notifyDataSetChanged();
                            }
                            UIHelper.ToastMessage(Main.this,
                                    res.getErrorMessage());
                        } else {
                            ((AppException) msg.obj).makeToast(Main.this);
                        }
                    }
                };
                Thread thread = new Thread() {
                    public void run() {
                        Message msg = new Message();
                        try {
                            Result res = appContext.delMessage(
                                    appContext.getLoginUid(),
                                    message.getFriendId());
                            msg.what = 1;
                            msg.obj = res;
                        } catch (AppException e) {
                            e.printStackTrace();
                            msg.what = -1;
                            msg.obj = e;
                        }
                        handler.sendMessage(msg);
                    }
                };
                UIHelper.showMessageListOptionDialog(Main.this, message, thread);
                return true;
            }
        });
        lvChat.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                // 清除通知信息
                if (bv_chat.isShown()) {
                    isClearNotice = true;
                    curClearNoticeType = Notice.TYPE_MESSAGE;
                }
                // 刷新数据
                loadLvMsgData(0, lvMsgHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }

    /**
     * 初始化头部视图
     */
    private void initHeadView() {
        mHeadLogo = (ImageView) findViewById(R.id.main_head_logo);
        mHeadTitle = (TextView) findViewById(R.id.main_head_title);
        mHeadProgress = (ProgressBar) findViewById(R.id.main_head_progress);
        mHead_search = (ImageButton) findViewById(R.id.main_head_search);
        mHeadPub_post = (ImageButton) findViewById(R.id.main_head_pub_post);
        mHeadPub_tweet = (ImageButton) findViewById(R.id.main_head_pub_tweet);

        mHead_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UIHelper.showSearch(v.getContext());
            }
        });
        mHeadPub_post.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UIHelper.showQuestionPub(v.getContext());
            }
        });
        mHeadPub_tweet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UIHelper.showTweetPub(Main.this);
            }
        });
    }

    /**
     * 初始化底部栏
     */
    private void initFootBar() {
        fbHome = (RadioButton) findViewById(R.id.main_footbar_home);
        fbRank = (RadioButton) findViewById(R.id.main_footbar_rank);
        fbNest = (RadioButton) findViewById(R.id.main_footbar_nest);
        fbWord = (RadioButton) findViewById(R.id.main_footbar_word);

        fbSetting = (ImageView) findViewById(R.id.main_footbar_setting);
        fbSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 展示快捷栏&判断是否登录&是否加载文章图片
                UIHelper.showSettingLoginOrLogout(Main.this,
                        mGrid.getQuickAction(0));
                mGrid.show(v);
            }
        });
    }

    /**
     * 初始化通知信息标签控件
     */
    private void initBadgeView() {
        bv_active = new BadgeView(this, fbWord);
        bv_active.setBackgroundResource(R.drawable.widget_count_bg);
        bv_active.setIncludeFontPadding(false);
        bv_active.setGravity(Gravity.CENTER);
        bv_active.setTextSize(8f);
        bv_active.setTextColor(Color.WHITE);

        bv_atme = new BadgeView(this, framebtn_Word_atme);
        bv_atme.setBackgroundResource(R.drawable.widget_count_bg);
        bv_atme.setIncludeFontPadding(false);
        bv_atme.setGravity(Gravity.CENTER);
        bv_atme.setTextSize(8f);
        bv_atme.setTextColor(Color.WHITE);

        bv_comment = new BadgeView(this, framebtn_Word_comment);
        bv_comment.setBackgroundResource(R.drawable.widget_count_bg);
        bv_comment.setIncludeFontPadding(false);
        bv_comment.setGravity(Gravity.CENTER);
        bv_comment.setTextSize(8f);
        bv_comment.setTextColor(Color.WHITE);

        bv_chat = new BadgeView(this, framebtn_Word_chat);
        bv_chat.setBackgroundResource(R.drawable.widget_count_bg);
        bv_chat.setIncludeFontPadding(false);
        bv_chat.setGravity(Gravity.CENTER);
        bv_chat.setTextSize(8f);
        bv_chat.setTextColor(Color.WHITE);
    }

    /**
     * 初始化水平滚动翻页
     */
    private void initPageScroll() {
        mScrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_linearlayout_footer);
        mHeadTitles = getResources().getStringArray(R.array.head_titles);
        mViewCount = mScrollLayout.getChildCount();
        mButtons = new RadioButton[mViewCount];

        for (int i = 0; i < mViewCount; i++) {
            mButtons[i] = (RadioButton) linearLayout.getChildAt(i * 2);
            mButtons[i].setTag(i);
            mButtons[i].setChecked(false);
            mButtons[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int pos = (Integer) (v.getTag());
                    // 点击当前项刷新
                    if (mCurSel == pos) {
                        switch (pos) {
                            case 0:// 首页
                                lvHome.clickRefresh();
                                break;
                            case 1:// 消息
                                if (lvWord.getVisibility() == View.VISIBLE)
                                    lvWord.clickRefresh();
                                else if (lvChat.getVisibility() == View.VISIBLE)
                                    lvChat.clickRefresh();
                                else lvNotice.clickRefresh();
                                break;
                            case 2:// 小窝
                                lvNest.clickRefresh();
                                break;
                            case 3:// 排行
                                lvRank.clickRefresh();
                                break;
                        }
                    }
                    mScrollLayout.snapToScreen(pos);
                }
            });
        }

        // 设置第一显示屏
        mCurSel = 0;
        mButtons[mCurSel].setChecked(true);

        mScrollLayout
                .SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
                    public void OnViewChange(int viewIndex) {
                        // 切换列表视图-如果列表数据为空：加载数据
                        switch (viewIndex) {
                            case 0:// 首页
                                if (lvHomeData.isEmpty()) {
                                    loadLvTweetData(curHomeCatalog, 0,
                                            lvHomeHandler,
                                            UIHelper.LISTVIEW_ACTION_INIT);
                                }
                                break;
                            case 1:// 消息

                                // 判断登录
                                if (!appContext.isLogin()) {
                                    if (lvWord.getVisibility() == View.VISIBLE && lvWordData.isEmpty()) {
                                        lvWord_foot_more.setText(R.string.load_empty);
                                        lvWord_foot_progress.setVisibility(View.GONE);
                                    } else if (lvChat.getVisibility() == View.VISIBLE && lvMsgData.isEmpty()) {
                                        lvMsg_foot_more.setText(R.string.load_empty);
                                        lvMsg_foot_progress.setVisibility(View.GONE);
                                    } else if (lvNotice.getVisibility() == View.VISIBLE && lvNoticeData.isEmpty()) {
                                        lvNotice_foot_more.setText(R.string.load_empty);
                                        lvNotice_foot_progress.setVisibility(View.GONE);
                                    }
                                    UIHelper.showLoginDialog(Main.this);
                                    break;
                                }
                                // 处理通知信息
                                if (bv_atme.isShown())
                                    frameActiveBtnOnClick(framebtn_Word_atme, WordList.CATALOG_ATME, UIHelper.LISTVIEW_ACTION_REFRESH);
                                else if (bv_comment.isShown())
                                    frameActiveBtnOnClick(framebtn_Word_comment, WordList.CATALOG_COMMENT, UIHelper.LISTVIEW_ACTION_REFRESH);
                                else if (bv_chat.isShown())
                                    frameActiveBtnOnClick(framebtn_Word_chat, 0, UIHelper.LISTVIEW_ACTION_REFRESH);
                                else if (lvWord.getVisibility() == View.VISIBLE && lvWordData.isEmpty())
                                    loadLvWordData(curWordCatalog, 0, lvWordHandler, UIHelper.LISTVIEW_ACTION_INIT);
                                else if (lvChat.getVisibility() == View.VISIBLE && lvMsgData.isEmpty())
                                    loadLvMsgData(0, lvMsgHandler, UIHelper.LISTVIEW_ACTION_INIT);
                                else if (lvNotice.getVisibility() == View.VISIBLE && lvNoticeData.isEmpty())
                                    loadLvNoticeData(curWordCatalog, 0, lvNoticeHandler, UIHelper.LISTVIEW_ACTION_INIT);
                                break;
                            case 2:// 小窝
                                if (lvNest.getVisibility() == View.VISIBLE) {
                                    if (lvNestData.isEmpty()) {
                                        loadLvNewsData(curNestCatalog, 0,
                                                lvNestHandler,
                                                UIHelper.LISTVIEW_ACTION_INIT);
                                    }
                                } else {
                                    if (lvBlogData.isEmpty()) {
                                        loadLvBlogData(curNestCatalog, 0,
                                                lvBlogHandler,
                                                UIHelper.LISTVIEW_ACTION_INIT);
                                    }
                                }
                                break;
                            case 3:// 排行
                                if (lvRankData.isEmpty()) {
                                    loadLvQuestionData(curRankCatalog, 0,
                                            lvRankHandler,
                                            UIHelper.LISTVIEW_ACTION_INIT);
                                }
                                break;
                        }
                        setCurPoint(viewIndex);
                    }
                });
    }

    /**
     * 设置底部栏当前焦点
     *
     * @param index
     */
    private void setCurPoint(int index) {
        if (index < 0 || index > mViewCount - 1 || mCurSel == index)
            return;

        mButtons[mCurSel].setChecked(false);
        mButtons[index].setChecked(true);
        mHeadTitle.setText(mHeadTitles[index]);
        mCurSel = index;

        mHead_search.setVisibility(View.GONE);
        mHeadPub_post.setVisibility(View.GONE);
        mHeadPub_tweet.setVisibility(View.GONE);
        // 头部logo、发帖、发动弹按钮显示
        if (index == 1) {
            mHeadLogo.setImageResource(R.drawable.frame_logo_news);
            mHead_search.setVisibility(View.VISIBLE);
        } else if (index == 2) {
            mHeadLogo.setImageResource(R.drawable.frame_logo_post);
//            mHeadPub_post.setVisibility(View.VISIBLE);
        } else if (index == 0) {
            mHeadLogo.setImageResource(R.drawable.frame_logo_tweet);
            mHeadPub_tweet.setVisibility(View.VISIBLE);
        } else if (index == 3) {
            mHeadLogo.setImageResource(R.drawable.frame_logo_active);
            mHeadPub_tweet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化各个主页的按钮(资讯、问答、动弹、动态、留言)
     */
    private void initFrameButton() {
        // 初始化按钮控件
        framebtn_Nest_lastest = (Button) findViewById(R.id.frame_btn_nest_lastest);
        framebtn_Nest_blog = (Button) findViewById(R.id.frame_btn_nest_blog);
        framebtn_Nest_recommend = (Button) findViewById(R.id.frame_btn_nest_recommend);
        framebtn_Rank_ask = (Button) findViewById(R.id.frame_btn_rank_ask);
        framebtn_Rank_share = (Button) findViewById(R.id.frame_btn_rank_share);
        framebtn_Rank_other = (Button) findViewById(R.id.frame_btn_rank_other);
        framebtn_Rank_job = (Button) findViewById(R.id.frame_btn_rank_job);
        framebtn_Rank_site = (Button) findViewById(R.id.frame_btn_rank_site);
        framebtn_Home_stream = (Button) findViewById(R.id.frame_btn_home_stream);
        framebtn_Home_mine = (Button) findViewById(R.id.frame_btn_home_mine);
        framebtn_Home_help = (Button) findViewById(R.id.frame_btn_home_help);
        framebtn_Word_system = (Button) findViewById(R.id.frame_btn_word_system);
        framebtn_Word_atme = (Button) findViewById(R.id.frame_btn_word_atme);
        framebtn_Word_comment = (Button) findViewById(R.id.frame_btn_word_comment);
        framebtn_Word_active = (Button) findViewById(R.id.frame_btn_word_active);
        framebtn_Word_chat = (Button) findViewById(R.id.frame_btn_word_chat);
        // 设置首选择项
        framebtn_Nest_lastest.setEnabled(false);
        framebtn_Rank_ask.setEnabled(false);
        framebtn_Home_stream.setEnabled(false);
        framebtn_Word_system.setEnabled(false);
        // 资讯+博客
        framebtn_Nest_lastest.setOnClickListener(frameNewsBtnClick(
                framebtn_Nest_lastest, NewsList.CATALOG_ALL));
        framebtn_Nest_blog.setOnClickListener(frameNewsBtnClick(
                framebtn_Nest_blog, BlogList.CATALOG_LATEST));
        framebtn_Nest_recommend.setOnClickListener(frameNewsBtnClick(
                framebtn_Nest_recommend, BlogList.CATALOG_RECOMMEND));
        // 问答
        framebtn_Rank_ask.setOnClickListener(frameQuestionBtnClick(
                framebtn_Rank_ask, PostList.CATALOG_ASK));
        framebtn_Rank_share.setOnClickListener(frameQuestionBtnClick(
                framebtn_Rank_share, PostList.CATALOG_SHARE));
        framebtn_Rank_other.setOnClickListener(frameQuestionBtnClick(
                framebtn_Rank_other, PostList.CATALOG_OTHER));
        framebtn_Rank_job.setOnClickListener(frameQuestionBtnClick(
                framebtn_Rank_job, PostList.CATALOG_JOB));
        framebtn_Rank_site.setOnClickListener(frameQuestionBtnClick(
                framebtn_Rank_site, PostList.CATALOG_SITE));
        // 动弹
        framebtn_Home_stream.setOnClickListener(frameTweetBtnClick(framebtn_Home_stream, TweetList.CATALOG_STREAM));
        framebtn_Home_help.setOnClickListener(frameTweetBtnClick(framebtn_Home_help, TweetList.CATALOG_HELP));
        framebtn_Home_mine.setOnClickListener(frameTweetBtnClick(framebtn_Home_mine, TweetList.CATALOG_MINE));

        // 动态+留言
        framebtn_Word_system.setOnClickListener(frameActiveBtnClick(framebtn_Word_system, WordList.CATALOG_SYSTEM));
        framebtn_Word_atme.setOnClickListener(frameActiveBtnClick(framebtn_Word_atme, WordList.CATALOG_ATME));
        framebtn_Word_comment.setOnClickListener(frameActiveBtnClick(framebtn_Word_comment, WordList.CATALOG_COMMENT));
        framebtn_Word_active.setOnClickListener(frameActiveBtnClick(framebtn_Word_active, WordList.CATALOG_ACTIVE));
        framebtn_Word_chat.setOnClickListener(frameActiveBtnClick(framebtn_Word_chat, WordList.CATALOG_CHAT));
    }

    private View.OnClickListener frameNewsBtnClick(final Button btn,
                                                   final int catalog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (btn == framebtn_Nest_lastest) {
                    framebtn_Nest_lastest.setEnabled(false);
                } else {
                    framebtn_Nest_lastest.setEnabled(true);
                }
                if (btn == framebtn_Nest_blog) {
                    framebtn_Nest_blog.setEnabled(false);
                } else {
                    framebtn_Nest_blog.setEnabled(true);
                }
                if (btn == framebtn_Nest_recommend) {
                    framebtn_Nest_recommend.setEnabled(false);
                } else {
                    framebtn_Nest_recommend.setEnabled(true);
                }

                curNestCatalog = catalog;

                // 非新闻列表
                if (btn == framebtn_Nest_lastest) {
                    lvNest.setVisibility(View.VISIBLE);
                    lvBlog.setVisibility(View.GONE);

                    loadLvNewsData(curNestCatalog, 0, lvNestHandler,
                            UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
                } else {
                    lvNest.setVisibility(View.GONE);
                    lvBlog.setVisibility(View.VISIBLE);

                    loadLvBlogData(curNestCatalog, 0, lvBlogHandler,
                            UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
                }
            }
        };
    }

    private View.OnClickListener frameQuestionBtnClick(final Button btn,
                                                       final int catalog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (btn == framebtn_Rank_ask)
                    framebtn_Rank_ask.setEnabled(false);
                else
                    framebtn_Rank_ask.setEnabled(true);
                if (btn == framebtn_Rank_share)
                    framebtn_Rank_share.setEnabled(false);
                else
                    framebtn_Rank_share.setEnabled(true);
                if (btn == framebtn_Rank_other)
                    framebtn_Rank_other.setEnabled(false);
                else
                    framebtn_Rank_other.setEnabled(true);
                if (btn == framebtn_Rank_job)
                    framebtn_Rank_job.setEnabled(false);
                else
                    framebtn_Rank_job.setEnabled(true);
                if (btn == framebtn_Rank_site)
                    framebtn_Rank_site.setEnabled(false);
                else
                    framebtn_Rank_site.setEnabled(true);

                curRankCatalog = catalog;
                loadLvQuestionData(curRankCatalog, 0, lvRankHandler,
                        UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
            }
        };
    }

    private View.OnClickListener frameTweetBtnClick(final Button btn,
                                                    final int catalog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                // 判断登录
                int uid = appContext.getLoginUid();
                if (uid == 0) {
                    UIHelper.showLoginDialog(Main.this);
                    return;
                }
                if (btn == framebtn_Home_stream)
                    framebtn_Home_stream.setEnabled(false);
                else
                    framebtn_Home_stream.setEnabled(true);
                if (btn == framebtn_Home_mine)
                    framebtn_Home_mine.setEnabled(false);
                else
                    framebtn_Home_mine.setEnabled(true);
                if (btn == framebtn_Home_help)
                    framebtn_Home_help.setEnabled(false);
                else
                    framebtn_Home_help.setEnabled(true);

                curHomeCatalog = catalog;
                loadLvTweetData(curHomeCatalog, 0, lvHomeHandler,
                        UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
            }
        };
    }

    private View.OnClickListener frameActiveBtnClick(final Button btn,
                                                     final int catalog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                // 判断登录
                if (!appContext.isLogin()) {
                    if (lvWord.getVisibility() == View.VISIBLE
                            && lvWordData.isEmpty()) {
                        lvWord_foot_more.setText(R.string.load_empty);
                        lvWord_foot_progress.setVisibility(View.GONE);
                    } else if (lvChat.getVisibility() == View.VISIBLE
                            && lvMsgData.isEmpty()) {
                        lvMsg_foot_more.setText(R.string.load_empty);
                        lvMsg_foot_progress.setVisibility(View.GONE);
                    }
                    UIHelper.showLoginDialog(Main.this);
                    return;
                }

                frameActiveBtnOnClick(btn, catalog,
                        UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
            }
        };
    }

    private void frameActiveBtnOnClick(Button btn, int catalog, int action) {
        if (btn == framebtn_Word_system)
            framebtn_Word_system.setEnabled(false);
        else
            framebtn_Word_system.setEnabled(true);
        if (btn == framebtn_Word_atme)
            framebtn_Word_atme.setEnabled(false);
        else
            framebtn_Word_atme.setEnabled(true);
        if (btn == framebtn_Word_comment)
            framebtn_Word_comment.setEnabled(false);
        else
            framebtn_Word_comment.setEnabled(true);
        if (btn == framebtn_Word_active)
            framebtn_Word_active.setEnabled(false);
        else
            framebtn_Word_active.setEnabled(true);
        if (btn == framebtn_Word_chat)
            framebtn_Word_chat.setEnabled(false);
        else
            framebtn_Word_chat.setEnabled(true);

        // 是否处理通知信息
        if (btn == framebtn_Word_atme && bv_atme.isShown()) {
            action = UIHelper.LISTVIEW_ACTION_REFRESH;
            this.isClearNotice = true;
            this.curClearNoticeType = Notice.TYPE_ATME;
        } else if (btn == framebtn_Word_comment && bv_comment.isShown()) {
            action = UIHelper.LISTVIEW_ACTION_REFRESH;
            this.isClearNotice = true;
            this.curClearNoticeType = Notice.TYPE_COMMENT;
        } else if (btn == framebtn_Word_chat && bv_chat.isShown()) {
            action = UIHelper.LISTVIEW_ACTION_REFRESH;
            this.isClearNotice = true;
            this.curClearNoticeType = Notice.TYPE_MESSAGE;
        }

        // 非留言展示动态列表
        if (btn == framebtn_Word_active) {
            lvWord.setVisibility(View.GONE);
            lvChat.setVisibility(View.GONE);
            lvNotice.setVisibility(View.VISIBLE);
            curWordCatalog = catalog;
            loadLvNoticeData(curWordCatalog, 0, lvNoticeHandler, action);
        } else if (btn == framebtn_Word_chat) {
            lvWord.setVisibility(View.GONE);
            lvChat.setVisibility(View.VISIBLE);
            lvNotice.setVisibility(View.GONE);
            loadLvMsgData(0, lvMsgHandler, action);
        } else {
            lvWord.setVisibility(View.VISIBLE);
            lvChat.setVisibility(View.GONE);
            lvNotice.setVisibility(View.GONE);
            curWordCatalog = catalog;
            loadLvWordData(curWordCatalog, 0, lvWordHandler, action);
        }
    }

    /**
     * 获取listview的初始化Handler
     *
     * @param lv
     * @param adapter
     * @return
     */
    private Handler getLvHandler(final PullToRefreshListView lv,
                                 final BaseAdapter adapter, final TextView more,
                                 final ProgressBar progress, final int pageSize) {
        return new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what >= 0) {
                    // listview数据处理
                    Notice notice = handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);

                    if (msg.what < pageSize) {
                        lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
                        adapter.notifyDataSetChanged();
                        more.setText(R.string.load_full);
                    } else if (msg.what == pageSize) {
                        lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
                        adapter.notifyDataSetChanged();
                        more.setText(R.string.load_more);

                        // 特殊处理-热门动弹不能翻页
                        if (lv == lvHome) {
                            TweetList tlist = (TweetList) msg.obj;
                            if (lvHomeData.size() == tlist.getTweetCount()) {
                                lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
                                more.setText(R.string.load_full);
                            }
                        }
                    }
                    // 发送通知广播
                    if (notice != null) {
                        UIHelper.sendBroadCast(lv.getContext(), notice);
                    }
                    // 是否清除通知信息
                    if (isClearNotice) {
                        ClearNotice(curClearNoticeType);
                        isClearNotice = false;// 重置
                        curClearNoticeType = 0;
                    }
                } else if (msg.what == -1) {
                    // 有异常--显示加载出错 & 弹出错误消息
                    lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
                    more.setText(R.string.load_error);
                    ((AppException) msg.obj).makeToast(Main.this);
                }
                if (adapter.getCount() == 0) {
                    lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
                    more.setText(R.string.load_empty);
                }
                progress.setVisibility(ProgressBar.GONE);
                mHeadProgress.setVisibility(ProgressBar.GONE);
                if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
                    lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)
                            + new Date().toLocaleString());
                    lv.setSelection(0);
                } else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
                    lv.onRefreshComplete();
                    lv.setSelection(0);
                }
            }
        };
    }

    /**
     * listview数据处理
     *
     * @param what       数量
     * @param obj        数据
     * @param objtype    数据类型
     * @param actiontype 操作类型
     * @return notice 通知信息
     */
    private Notice handleLvData(int what, Object obj, int objtype,
                                int actiontype) {
        Notice notice = null;
        switch (actiontype) {
            case UIHelper.LISTVIEW_ACTION_INIT:
            case UIHelper.LISTVIEW_ACTION_REFRESH:
            case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
                int newdata = 0;// 新加载数据-只有刷新动作才会使用到
                switch (objtype) {
                    case UIHelper.LISTVIEW_DATATYPE_NOTICE:
                        NewsList noticelist = (NewsList) obj;
                        notice = noticelist.getNotice();
                        lvNoticeSumData = what;
                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            if (lvNoticeData.size() > 0) {
                                for (News news1 : noticelist.getNewslist()) {
                                    boolean b = false;
                                    for (News news2 : lvNoticeData) {
                                        if (news1.getId() == news2.getId()) {
                                            b = true;
                                            break;
                                        }
                                    }
                                    if (!b)
                                        newdata++;
                                }
                            } else {
                                newdata = what;
                            }
                        }
                        lvNoticeData.clear();// 先清除原有数据
                        lvNoticeData.addAll(noticelist.getNewslist());
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_NEWS:
                        NewsList nlist = (NewsList) obj;
                        notice = nlist.getNotice();
                        lvNestSumData = what;
                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            if (lvNestData.size() > 0) {
                                for (News news1 : nlist.getNewslist()) {
                                    boolean b = false;
                                    for (News news2 : lvNestData) {
                                        if (news1.getId() == news2.getId()) {
                                            b = true;
                                            break;
                                        }
                                    }
                                    if (!b)
                                        newdata++;
                                }
                            } else {
                                newdata = what;
                            }
                        }
                        lvNestData.clear();// 先清除原有数据
                        lvNestData.addAll(nlist.getNewslist());
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_BLOG:
                        BlogList blist = (BlogList) obj;
                        notice = blist.getNotice();
                        lvBlogSumData = what;
                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            if (lvBlogData.size() > 0) {
                                for (Blog blog1 : blist.getBloglist()) {
                                    boolean b = false;
                                    for (Blog blog2 : lvBlogData) {
                                        if (blog1.getId() == blog2.getId()) {
                                            b = true;
                                            break;
                                        }
                                    }
                                    if (!b)
                                        newdata++;
                                }
                            } else {
                                newdata = what;
                            }
                        }
                        lvBlogData.clear();// 先清除原有数据
                        lvBlogData.addAll(blist.getBloglist());
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_POST:
                        PostList plist = (PostList) obj;
                        notice = plist.getNotice();
                        lvRankSumData = what;
                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            if (lvRankData.size() > 0) {
                                for (Post post1 : plist.getPostlist()) {
                                    boolean b = false;
                                    for (Post post2 : lvRankData) {
                                        if (post1.getId() == post2.getId()) {
                                            b = true;
                                            break;
                                        }
                                    }
                                    if (!b)
                                        newdata++;
                                }
                            } else {
                                newdata = what;
                            }
                        }
                        lvRankData.clear();// 先清除原有数据
                        lvRankData.addAll(plist.getPostlist());
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_TWEET:
                        TweetList tlist = (TweetList) obj;
                        notice = tlist.getNotice();
                        lvTweetSumData = what;
                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            if (lvHomeData.size() > 0) {
                                for (Tweet tweet1 : tlist.getTweetlist()) {
                                    boolean b = false;
                                    for (Tweet tweet2 : lvHomeData) {
                                        if (tweet1.getId() == tweet2.getId()) {
                                            b = true;
                                            break;
                                        }
                                    }
                                    if (!b)
                                        newdata++;
                                }
                            } else {
                                newdata = what;
                            }
                        }
                        lvHomeData.clear();// 先清除原有数据
                        lvHomeData.addAll(tlist.getTweetlist());
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
                        WordList alist = (WordList) obj;
                        notice = alist.getNotice();
                        lvWordSumData = what;
                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            if (lvWordData.size() > 0) {
                                for (Active active1 : alist.getActivelist()) {
                                    boolean b = false;
                                    for (Active active2 : lvWordData) {
                                        if (active1.getId() == active2.getId()) {
                                            b = true;
                                            break;
                                        }
                                    }
                                    if (!b)
                                        newdata++;
                                }
                            } else {
                                newdata = what;
                            }
                        }
                        lvWordData.clear();// 先清除原有数据
                        lvWordData.addAll(alist.getActivelist());
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
                        MessageList mlist = (MessageList) obj;
                        notice = mlist.getNotice();
                        lvMsgSumData = what;
                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            if (lvMsgData.size() > 0) {
                                for (Messages msg1 : mlist.getMessagelist()) {
                                    boolean b = false;
                                    for (Messages msg2 : lvMsgData) {
                                        if (msg1.getId() == msg2.getId()) {
                                            b = true;
                                            break;
                                        }
                                    }
                                    if (!b)
                                        newdata++;
                                }
                            } else {
                                newdata = what;
                            }
                        }
                        lvMsgData.clear();// 先清除原有数据
                        lvMsgData.addAll(mlist.getMessagelist());
                        break;
                }
                if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                    // 提示新加载数据
                    if (newdata > 0) {
                        NewDataToast
                                .makeText(
                                        this,
                                        getString(R.string.new_data_toast_message,
                                                newdata), appContext.isAppSound())
                                .show();
                    } else {
                        NewDataToast.makeText(this,
                                getString(R.string.new_data_toast_none), false)
                                .show();
                    }
                }
                break;
            case UIHelper.LISTVIEW_ACTION_SCROLL:
                switch (objtype) {
                    case UIHelper.LISTVIEW_DATATYPE_NOTICE:
                        NewsList noticelist = (NewsList) obj;
                        notice = noticelist.getNotice();
                        lvNoticeSumData += what;
                        if (lvNoticeData.size() > 0) {
                            for (News notice1 : noticelist.getNewslist()) {
                                boolean b = false;
                                for (News news2 : lvNoticeData) {
                                    if (notice1.getId() == news2.getId()) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (!b)
                                    lvNoticeData.add(notice1);
                            }
                        } else {
                            lvNoticeData.addAll(noticelist.getNewslist());
                        }
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_NEWS:
                        NewsList list = (NewsList) obj;
                        notice = list.getNotice();
                        lvNestSumData += what;
                        if (lvNestData.size() > 0) {
                            for (News news1 : list.getNewslist()) {
                                boolean b = false;
                                for (News news2 : lvNestData) {
                                    if (news1.getId() == news2.getId()) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (!b)
                                    lvNestData.add(news1);
                            }
                        } else {
                            lvNestData.addAll(list.getNewslist());
                        }
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_BLOG:
                        BlogList blist = (BlogList) obj;
                        notice = blist.getNotice();
                        lvBlogSumData += what;
                        if (lvBlogData.size() > 0) {
                            for (Blog blog1 : blist.getBloglist()) {
                                boolean b = false;
                                for (Blog blog2 : lvBlogData) {
                                    if (blog1.getId() == blog2.getId()) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (!b)
                                    lvBlogData.add(blog1);
                            }
                        } else {
                            lvBlogData.addAll(blist.getBloglist());
                        }
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_POST:
                        PostList plist = (PostList) obj;
                        notice = plist.getNotice();
                        lvRankSumData += what;
                        if (lvRankData.size() > 0) {
                            for (Post post1 : plist.getPostlist()) {
                                boolean b = false;
                                for (Post post2 : lvRankData) {
                                    if (post1.getId() == post2.getId()) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (!b)
                                    lvRankData.add(post1);
                            }
                        } else {
                            lvRankData.addAll(plist.getPostlist());
                        }
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_TWEET:
                        TweetList tlist = (TweetList) obj;
                        notice = tlist.getNotice();
                        lvTweetSumData += what;
                        if (lvHomeData.size() > 0) {
                            for (Tweet tweet1 : tlist.getTweetlist()) {
                                boolean b = false;
                                for (Tweet tweet2 : lvHomeData) {
                                    if (tweet1.getId() == tweet2.getId()) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (!b)
                                    lvHomeData.add(tweet1);
                            }
                        } else {
                            lvHomeData.addAll(tlist.getTweetlist());
                        }
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
                        WordList alist = (WordList) obj;
                        notice = alist.getNotice();
                        lvWordSumData += what;
                        if (lvWordData.size() > 0) {
                            for (Active active1 : alist.getActivelist()) {
                                boolean b = false;
                                for (Active active2 : lvWordData) {
                                    if (active1.getId() == active2.getId()) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (!b)
                                    lvWordData.add(active1);
                            }
                        } else {
                            lvWordData.addAll(alist.getActivelist());
                        }
                        break;
                    case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
                        MessageList mlist = (MessageList) obj;
                        notice = mlist.getNotice();
                        lvMsgSumData += what;
                        if (lvMsgData.size() > 0) {
                            for (Messages msg1 : mlist.getMessagelist()) {
                                boolean b = false;
                                for (Messages msg2 : lvMsgData) {
                                    if (msg1.getId() == msg2.getId()) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (!b)
                                    lvMsgData.add(msg1);
                            }
                        } else {
                            lvMsgData.addAll(mlist.getMessagelist());
                        }
                        break;
                }
                break;
        }
        return notice;
    }


    /**
     * 线程加载通知或活跃数据
     *
     * @param catalog   分类
     * @param pageIndex 当前页数
     * @param handler   处理器
     * @param action    动作标识
     */
    private void loadLvNoticeData(final int catalog, final int pageIndex,
                                  final Handler handler, final int action) {
        mHeadProgress.setVisibility(ProgressBar.VISIBLE);
        new Thread() {
            public void run() {
                Message msg = new Message();
                boolean isRefresh = false;
                if (action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
                    isRefresh = true;
                try {
                    NewsList list = appContext.getNoticeList(catalog, pageIndex, isRefresh);
                    msg.what = list.getPageSize();
                    msg.obj = list;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NOTICE;
                if (curWordCatalog == catalog)
                    handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 线程加载新闻数据
     *
     * @param catalog   分类
     * @param pageIndex 当前页数
     * @param handler   处理器
     * @param action    动作标识
     */
    private void loadLvNewsData(final int catalog, final int pageIndex,
                                final Handler handler, final int action) {
        mHeadProgress.setVisibility(ProgressBar.VISIBLE);
        new Thread() {
            public void run() {
                Message msg = new Message();
                boolean isRefresh = false;
                if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                        || action == UIHelper.LISTVIEW_ACTION_SCROLL)
                    isRefresh = true;
                try {
                    NewsList list = appContext.getNewsList(catalog, pageIndex,
                            isRefresh);
                    msg.what = list.getPageSize();
                    msg.obj = list;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
                if (curNestCatalog == catalog)
                    handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 线程加载博客数据
     *
     * @param catalog   分类
     * @param pageIndex 当前页数
     * @param handler   处理器
     * @param action    动作标识
     */
    private void loadLvBlogData(final int catalog, final int pageIndex,
                                final Handler handler, final int action) {
        mHeadProgress.setVisibility(ProgressBar.VISIBLE);
        new Thread() {
            public void run() {
                Message msg = new Message();
                boolean isRefresh = false;
                if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                        || action == UIHelper.LISTVIEW_ACTION_SCROLL)
                    isRefresh = true;
                String type = "";
                switch (catalog) {
                    case BlogList.CATALOG_LATEST:
                        type = BlogList.TYPE_LATEST;
                        break;
                    case BlogList.CATALOG_RECOMMEND:
                        type = BlogList.TYPE_RECOMMEND;
                        break;
                }
                try {
                    BlogList list = appContext.getBlogList(type, pageIndex,
                            isRefresh);
                    msg.what = list.getPageSize();
                    msg.obj = list;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_BLOG;
                if (curNestCatalog == catalog)
                    handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 线程加载帖子数据
     *
     * @param catalog   分类
     * @param pageIndex 当前页数
     * @param handler   处理器
     * @param action    动作标识
     */
    private void loadLvQuestionData(final int catalog, final int pageIndex,
                                    final Handler handler, final int action) {
        mHeadProgress.setVisibility(ProgressBar.VISIBLE);
        new Thread() {
            public void run() {
                Message msg = new Message();
                boolean isRefresh = false;
                if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                        || action == UIHelper.LISTVIEW_ACTION_SCROLL)
                    isRefresh = true;
                try {
                    PostList list = appContext.getPostList(catalog, pageIndex,
                            isRefresh);
                    msg.what = list.getPageSize();
                    msg.obj = list;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_POST;
                if (curRankCatalog == catalog)
                    handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 线程加载动弹数据
     *
     * @param catalog   -1 热门，0 最新，大于0 某用户的动弹(uid)
     * @param pageIndex 当前页数
     * @param handler   处理器
     * @param action    动作标识
     */
    private void loadLvTweetData(final int catalog, final int pageIndex,
                                 final Handler handler, final int action) {
        mHeadProgress.setVisibility(ProgressBar.VISIBLE);
        new Thread() {
            public void run() {
                Message msg = new Message();
                boolean isRefresh = false;
                if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                        || action == UIHelper.LISTVIEW_ACTION_SCROLL)
                    isRefresh = true;
                try {
                    TweetList list = appContext.getTweetList(catalog,
                            pageIndex, isRefresh);
                    msg.what = list.getPageSize();
                    msg.obj = list;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_TWEET;
                if (curHomeCatalog == catalog)
                    handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 线程加载动态数据
     *
     * @param catalog
     * @param pageIndex 当前页数
     * @param handler
     * @param action
     */
    private void loadLvWordData(final int catalog, final int pageIndex,
                                final Handler handler, final int action) {
        mHeadProgress.setVisibility(ProgressBar.VISIBLE);
        new Thread() {
            public void run() {
                Message msg = new Message();
                boolean isRefresh = false;
                if (action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
                    isRefresh = true;
                try {
                    WordList list = appContext.getWordList(catalog, pageIndex, isRefresh);
                    msg.what = list.getPageSize();
                    msg.obj = list;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_ACTIVE;
                if (curWordCatalog == catalog)
                    handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 线程加载留言数据
     *
     * @param pageIndex 当前页数
     * @param handler
     * @param action
     */
    private void loadLvMsgData(final int pageIndex, final Handler handler,
                               final int action) {
        mHeadProgress.setVisibility(ProgressBar.VISIBLE);
        new Thread() {
            public void run() {
                Message msg = new Message();
                boolean isRefresh = false;
                if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                        || action == UIHelper.LISTVIEW_ACTION_SCROLL)
                    isRefresh = true;
                try {
                    MessageList list = appContext.getMessageList(pageIndex,
                            isRefresh);
                    msg.what = list.getPageSize();
                    msg.obj = list;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = UIHelper.LISTVIEW_DATATYPE_MESSAGE;
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 轮询通知信息
     */
    private void foreachUserNotice() {
        final int uid = appContext.getLoginUid();
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    UIHelper.sendBroadCast(Main.this, (Notice) msg.obj);
                }
                foreachUserNotice();// 回调
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    sleep(60 * 1000);
                    if (uid > 0) {
                        Notice notice = appContext.getUserNotice(uid);
                        msg.what = 1;
                        msg.obj = notice;
                    } else {
                        msg.what = 0;
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 通知信息处理
     *
     * @param type 1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
     */
    private void ClearNotice(final int type) {
        final int uid = appContext.getLoginUid();
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1 && msg.obj != null) {
                    Result res = (Result) msg.obj;
                    if (res.OK() && res.getNotice() != null) {
                        UIHelper.sendBroadCast(Main.this, res.getNotice());
                    }
                } else {
                    ((AppException) msg.obj).makeToast(Main.this);
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    Result res = appContext.noticeClear(uid, type);
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

    /**
     * 创建menu TODO 停用原生菜单
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
        // MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.main_menu, menu);
        // return true;
    }

    /**
     * 菜单被显示之前的事件
     */
    public boolean onPrepareOptionsMenu(Menu menu) {
        UIHelper.showMenuLoginOrLogout(this, menu);
        return true;
    }

    /**
     * 处理menu的事件
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.main_menu_user:
                UIHelper.loginOrLogout(this);
                break;
            case R.id.main_menu_about:
                UIHelper.showAbout(this);
                break;
            case R.id.main_menu_setting:
                UIHelper.showSetting(this);
                break;
            case R.id.main_menu_exit:
                UIHelper.Exit(this);
                break;
        }
        return true;
    }

    /**
     * 监听返回--是否退出程序
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否退出应用
            UIHelper.Exit(this);
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            // 展示快捷栏&判断是否登录
            UIHelper.showSettingLoginOrLogout(Main.this,
                    mGrid.getQuickAction(0));
            mGrid.show(fbSetting, true);
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            // 展示搜索页
            UIHelper.showSearch(Main.this);
        } else {
            flag = super.onKeyDown(keyCode, event);
        }
        return flag;
    }
}
