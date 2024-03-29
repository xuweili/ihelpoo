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

package com.ihelpoo.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.webkit.CacheManager;

import com.ihelpoo.app.bean.AcademyList;
import com.ihelpoo.app.bean.DormList;
import com.ihelpoo.app.bean.MajorList;
import com.ihelpoo.app.bean.MobileCodeResult;
import com.ihelpoo.app.bean.MobileRegisterResult;
import com.ihelpoo.app.bean.SchoolList;
import com.ihelpoo.app.bean.WordList;
import com.ihelpoo.app.bean.BlogCommentList;
import com.ihelpoo.app.bean.FriendList;
import com.ihelpoo.app.bean.MessageList;
import com.ihelpoo.app.bean.MyInformation;
import com.ihelpoo.app.bean.News;
import com.ihelpoo.app.bean.NewsList;
import com.ihelpoo.app.bean.Post;
import com.ihelpoo.app.bean.PostList;
import com.ihelpoo.app.bean.SearchList;
import com.ihelpoo.app.bean.Software;
import com.ihelpoo.app.bean.SoftwareCatalogList;
import com.ihelpoo.app.bean.SoftwareList;
import com.ihelpoo.app.bean.Tweet;
import com.ihelpoo.app.bean.TweetList;
import com.ihelpoo.app.bean.User;
import com.ihelpoo.app.common.CyptoUtils;
import com.ihelpoo.app.common.MethodsCompat;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.bean.Blog;
import com.ihelpoo.app.bean.BlogList;
import com.ihelpoo.app.bean.CommentList;
import com.ihelpoo.app.bean.FavoriteList;
import com.ihelpoo.app.bean.Notice;
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.bean.UserInformation;
import com.ihelpoo.app.common.ImageUtils;

import com.ihelpoo.app.api.ApiClient;

import java.io.*;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext extends Application {
	
	private static AppContext instance;  //定义context实例
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static final int PAGE_SIZE = 20;//默认分页大小
    private static final int CACHE_TIME = 60 * 60000;//缓存失效时间

    private boolean login = false;    //登录状态
    private int loginUid = 0;    //登录用户的id
    private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();

    private String saveImagePath;//保存图片路径

    private Handler unLoginHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
                UIHelper.showLoginDialog(AppContext.this);
            }
        }
    };
    
    public static AppContext getInstance() {
        return instance;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this; //取得context
        //注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        //设置保存图片的路径
        saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
        if (StringUtils.isEmpty(saveImagePath)) {
            setProperty(AppConfig.SAVE_IMAGE_PATH, AppConfig.DEFAULT_SAVE_IMAGE_PATH);
            saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
        }
    }

    /**
     * 检测当前系统声音是否为正常模式
     *
     * @return
     */
    public boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * 应用程序是否发出提示音
     *
     * @return
     */
    public boolean isAppSound() {
        return isAudioNormal() && isVoice();
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */
    public int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 用户是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return login;
    }

    /**
     * 获取登录用户id
     *
     * @return
     */
    public int getLoginUid() {
        return this.loginUid;
    }

    /**
     * 用户注销
     */
    public void logout() {
        User loginUser = getLoginInfo();
        if (!loginUser.isRemember()) {
            cleanAccountInfo();
        }
        cleanLoginInfo();//清除登录信息
        cleanThirdLoginInfo();
        ApiClient.cleanCookie();
        this.cleanCookie();
        this.login = false;
        this.loginUid = 0;
    }

    /**
     * 未登录或修改密码后的处理
     */
    public Handler getUnLoginHandler() {
        return this.unLoginHandler;
    }

    /**
     * 初始化用户登录信息
     */
    public void initLoginInfo() {
        User loginUser = getLoginInfo();
        if (loginUser.getUid() > 0) {
//            if (loginUser.getUid() > 0 && loginUser.isRemember()) {
            this.loginUid = loginUser.getUid();
            this.login = true;
        } else {
            this.logout();
        }
    }

    /**
     * 用户登录验证
     *
     * @param account
     * @param pwd
     * @return
     * @throws AppException
     */
    public User loginVerify(String account, String pwd, String status) throws AppException {
        return ApiClient.login(this, account, pwd, status);
    }

    /**
     * 我的个人资料
     *
     * @param isRefresh 是否主动刷新
     * @return
     * @throws AppException
     */
    public MyInformation getMyInformation(boolean isRefresh) throws AppException {
        MyInformation myinfo = null;
        String key = "myinfo_" + loginUid;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                myinfo = ApiClient.myInformation(this, loginUid);
                if (myinfo != null && myinfo.getAcademy_name().length() > 0) {
                    Notice notice = myinfo.getNotice();
                    myinfo.setNotice(null);
                    myinfo.setCacheKey(key);
                    saveObject(myinfo, key);
                    myinfo.setNotice(notice);
                }
            } catch (AppException e) {
                myinfo = (MyInformation) readObject(key);
                if (myinfo == null)
                    throw e;
            }
        } else {
            myinfo = (MyInformation) readObject(key);
            if (myinfo == null)
                myinfo = new MyInformation();
        }
        return myinfo;
    }

    /**
     * 获取用户信息个人专页（包含该用户的动态信息以及个人信息）
     *
     * @param hisUid
     * @param uid       自己的uid
     * @param pageIndex 页面索引  @return
     * @throws AppException
     */
    public UserInformation getInformation(int hisUid, String hisName, int uid, int pageIndex, boolean isRefresh) throws AppException {
        UserInformation userinfo = null;
        String key = "userinfo_" + hisUid + "_" + hisName + "_" + uid + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                userinfo = ApiClient.information(this, hisUid, hisName, uid, pageIndex, PAGE_SIZE);
                if (userinfo != null && pageIndex == 0) {
                    Notice notice = userinfo.getNotice();
                    userinfo.setNotice(null);
                    userinfo.setCacheKey(key);
                    saveObject(userinfo, key);
                    userinfo.setNotice(notice);
                }
            } catch (AppException e) {
                userinfo = (UserInformation) readObject(key);
                if (userinfo == null)
                    throw e;
            }
        } else {
            userinfo = (UserInformation) readObject(key);
            if (userinfo == null)
                userinfo = new UserInformation();
        }
        return userinfo;
    }

    /**
     * 更新用户之间关系（加关注、取消关注）
     *
     * @param uid         自己的uid
     * @param hisuid      对方用户的uid
     * @param newrelation 0:取消对他的关注 1:关注他
     * @return
     * @throws AppException
     */
    public Result updateRelation(int uid, int hisuid, int newrelation) throws AppException {
        return ApiClient.updateRelation(this, uid, hisuid, newrelation);
    }

    /**
     * 更新用户头像
     *
     * @param portrait 新上传的头像
     * @return
     * @throws AppException
     */
    public Result updatePortrait(File portrait) throws AppException {
        return ApiClient.updatePortrait(this, loginUid, portrait);
    }

    /**
     * 清空通知消息
     *
     * @param uid
     * @param type    1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
     * @param fromUid
     * @return
     * @throws AppException
     */
    public Result noticeClear(int uid, int type, int fromUid) throws AppException {
        return ApiClient.noticeClear(this, uid, type, fromUid);
    }

    /**
     * 获取用户通知信息
     *
     * @param uid
     * @return
     * @throws AppException
     */
    public Notice getUserNotice(int uid) throws AppException {
        return ApiClient.getUserNotice(this, uid);
    }

    /**
     * 用户收藏列表
     *
     * @param type      0:全部收藏 1:软件 2:话题 3:博客 4:新闻 5:代码
     * @param pageIndex 页面索引 0表示第一页
     * @return
     * @throws AppException
     */
    public FavoriteList getFavoriteList(int type, int pageIndex, boolean isRefresh) throws AppException {
        FavoriteList list = null;
        String key = "favoritelist_" + loginUid + "_" + type + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getFavoriteList(this, loginUid, type, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (FavoriteList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (FavoriteList) readObject(key);
            if (list == null)
                list = new FavoriteList();
        }
        return list;
    }

    /**
     * 用户粉丝、关注人列表
     *
     * @param relation  0:显示自己的粉丝 1:显示自己的关注者
     * @param pageIndex
     * @return
     * @throws AppException
     */
    public FriendList getFriendList(int relation, int pageIndex, boolean isRefresh) throws AppException {
        FriendList list = null;
        String key = "friendlist_" + loginUid + "_" + relation + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getFriendList(this, loginUid, relation, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (FriendList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (FriendList) readObject(key);
            if (list == null)
                list = new FriendList();
        }
        return list;
    }

    /**
     * 活跃列表
     *
     * @param catalog
     * @param pageIndex
     * @param isRefresh
     * @return
     * @throws AppException
     */
    public NewsList getNoticeList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
        NewsList list = null;
        String key = "noticelist_" + loginUid + "_" + catalog + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getNoticeList(this, loginUid, catalog, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (NewsList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (NewsList) readObject(key);
            if (list == null)
                list = new NewsList();
        }
        return list;
    }

    /**
     * 新闻列表
     *
     * @param catalog
     * @param pageIndex
     * @param isRefresh
     * @return
     * @throws AppException
     */
    public NewsList getNewsList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
        NewsList list = null;
        String key = "newslist_" + loginUid + "_" + catalog + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getNewsList(this, loginUid, catalog, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (NewsList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (NewsList) readObject(key);
            if (list == null)
                list = new NewsList();
        }
        return list;
    }

    /**
     * 新闻详情
     *
     * @param news_id
     * @param isRefresh
     * @return
     * @throws AppException
     */
    public News getNews(int news_id, boolean isRefresh) throws AppException {
        News news = null;
        String key = "news_" + news_id;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                news = ApiClient.getNewsDetail(this, news_id);
                if (news != null) {
                    Notice notice = news.getNotice();
                    news.setNotice(null);
                    news.setCacheKey(key);
                    saveObject(news, key);
                    news.setNotice(notice);
                }
            } catch (AppException e) {
                news = (News) readObject(key);
                if (news == null)
                    throw e;
            }
        } else {
            news = (News) readObject(key);
            if (news == null)
                news = new News();
        }
        return news;
    }

    /**
     * 用户博客列表
     *
     * @param authoruid
     * @param pageIndex
     * @return
     * @throws AppException
     */
    public BlogList getUserBlogList(int authoruid, String authorname, int pageIndex, boolean isRefresh) throws AppException {
        BlogList list = null;
        String key = "userbloglist_" + authoruid + "_" + (URLEncoder.encode(authorname)) + "_" + loginUid + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getUserBlogList(this, authoruid, authorname, loginUid, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (BlogList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (BlogList) readObject(key);
            if (list == null)
                list = new BlogList();
        }
        return list;
    }

    /**
     * 博客列表
     *
     * @param type      推荐：recommend 最新：latest
     * @param pageIndex
     * @return
     * @throws AppException
     */
    public BlogList getBlogList(String type, int pageIndex, boolean isRefresh) throws AppException {
        BlogList list = null;
        String key = "bloglist_" + type + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getBlogList(this, type, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (BlogList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (BlogList) readObject(key);
            if (list == null)
                list = new BlogList();
        }
        return list;
    }

    /**
     * 博客详情
     *
     * @param blog_id
     * @return
     * @throws AppException
     */
    public Blog getBlog(int blog_id, boolean isRefresh) throws AppException {
        Blog blog = null;
        String key = "blog_" + blog_id;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                blog = ApiClient.getBlogDetail(this, blog_id);
                if (blog != null) {
                    Notice notice = blog.getNotice();
                    blog.setNotice(null);
                    blog.setCacheKey(key);
                    saveObject(blog, key);
                    blog.setNotice(notice);
                }
            } catch (AppException e) {
                blog = (Blog) readObject(key);
                if (blog == null)
                    throw e;
            }
        } else {
            blog = (Blog) readObject(key);
            if (blog == null)
                blog = new Blog();
        }
        return blog;
    }

    /**
     * 软件列表
     *
     * @param searchTag 软件分类  推荐:recommend 最新:time 热门:view 国产:list_cn
     * @param pageIndex
     * @return
     * @throws AppException
     */
    public SoftwareList getSoftwareList(String searchTag, int pageIndex, boolean isRefresh) throws AppException {
        SoftwareList list = null;
        String key = "softwarelist_" + searchTag + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getSoftwareList(this, searchTag, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (SoftwareList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (SoftwareList) readObject(key);
            if (list == null)
                list = new SoftwareList();
        }
        return list;
    }

    /**
     * 软件分类的软件列表
     *
     * @param searchTag 从softwarecatalog_list获取的tag
     * @param pageIndex
     * @return
     * @throws AppException
     */
    public SoftwareList getSoftwareTagList(int searchTag, int pageIndex, boolean isRefresh) throws AppException {
        SoftwareList list = null;
        String key = "softwaretaglist_" + searchTag + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
            try {
                list = ApiClient.getSoftwareTagList(this, searchTag, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (SoftwareList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (SoftwareList) readObject(key);
            if (list == null)
                list = new SoftwareList();
        }
        return list;
    }

    /**
     * 软件分类列表
     *
     * @param tag 第一级:0  第二级:tag
     * @return
     * @throws AppException
     */
    public SoftwareCatalogList getSoftwareCatalogList(int tag) throws AppException {
        SoftwareCatalogList list = null;
        String key = "softwarecataloglist_" + tag;
        if (isNetworkConnected() && isCacheDataFailure(key)) {
            try {
                list = ApiClient.getSoftwareCatalogList(this, tag);
                if (list != null) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (SoftwareCatalogList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (SoftwareCatalogList) readObject(key);
            if (list == null)
                list = new SoftwareCatalogList();
        }
        return list;
    }

    /**
     * 软件详情
     *
     * @param ident
     * @param isRefresh
     * @return
     * @throws AppException
     */
    public Software getSoftware(String ident, boolean isRefresh) throws AppException {
        Software soft = null;
        String key = "software_" + (URLEncoder.encode(ident));
        if (isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
            try {
                soft = ApiClient.getSoftwareDetail(this, ident);
                if (soft != null) {
                    Notice notice = soft.getNotice();
                    soft.setNotice(null);
                    soft.setCacheKey(key);
                    saveObject(soft, key);
                    soft.setNotice(notice);
                }
            } catch (AppException e) {
                soft = (Software) readObject(key);
                if (soft == null)
                    throw e;
            }
        } else {
            soft = (Software) readObject(key);
            if (soft == null)
                soft = new Software();
        }
        return soft;
    }

    /**
     * 帖子列表
     *
     * @param schoolId
     * @param catalog
     * @param pageIndex
     * @param isRefresh
     * @return
     * @throws AppException
     */
    public PostList getPostList(String schoolId, int catalog, int pageIndex, boolean isRefresh) throws AppException {
        PostList list = null;
        String key = "postlist_" + catalog + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getPostList(this, schoolId, catalog, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (PostList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (PostList) readObject(key);
            if (list == null)
                list = new PostList();
        }
        return list;
    }

    /**
     * Tag相关帖子列表
     *
     * @param tag
     * @param pageIndex
     * @param isRefresh
     * @return
     * @throws AppException
     */
    public PostList getPostListByTag(String tag, int pageIndex, boolean isRefresh) throws AppException {
        PostList list = null;
        String key = "postlist_" + (URLEncoder.encode(tag)) + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getPostListByTag(this, tag, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (PostList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (PostList) readObject(key);
            if (list == null)
                list = new PostList();
        }
        return list;
    }

    /**
     * 读取帖子详情
     *
     * @param post_id
     * @param isRefresh
     * @return
     * @throws AppException
     */
    public Post getPost(int post_id, boolean isRefresh) throws AppException {
        Post post = null;
        String key = "post_" + post_id;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                post = ApiClient.getPostDetail(this, post_id);
                if (post != null) {
                    Notice notice = post.getNotice();
                    post.setNotice(null);
                    post.setCacheKey(key);
                    saveObject(post, key);
                    post.setNotice(notice);
                }
            } catch (AppException e) {
                post = (Post) readObject(key);
                if (post == null)
                    throw e;
            }
        } else {
            post = (Post) readObject(key);
            if (post == null)
                post = new Post();
        }
        return post;
    }

    /**
     * 动弹列表
     *
     * @param catalog   -1 help，0 stream，Min_value, mine
     * @param schoolId
     * @param pageIndex @return  @throws AppException
     */
    public TweetList getTweetList(int catalog, String schoolId, int pageIndex, boolean isRefresh) throws AppException {
        TweetList list = null;
        String key = "tweetlist_" + loginUid + "_" + catalog + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getTweetList(this, catalog, loginUid, schoolId, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (TweetList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (TweetList) readObject(key);
            if (list == null)
                list = new TweetList();
        }
        return list;
    }

    /**
     * 获取动弹详情
     *
     * @param tweet_id
     * @param uid
     * @return
     * @throws AppException
     */
    public Tweet getTweet(int tweet_id, int uid, boolean isRefresh) throws AppException {
        Tweet tweet = null;
        String key = "tweet_" + tweet_id;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                tweet = ApiClient.getTweetDetail(this, tweet_id, uid);
                if (tweet != null) {
                    Notice notice = tweet.getNotice();
                    tweet.setNotice(null);
                    tweet.setCacheKey(key);
                    saveObject(tweet, key);
                    tweet.setNotice(notice);
                }
            } catch (AppException e) {
                tweet = (Tweet) readObject(key);
                if (tweet == null)
                    throw e;
            }
        } else {
            tweet = (Tweet) readObject(key);
            if (tweet == null)
                tweet = new Tweet();
        }
        return tweet;
    }

    /**
     * 动态列表
     *
     * @param catalog   1最新动态 2@我 3评论 4我自己
     * @param pageIndex
     * @param isRefresh
     * @return
     * @throws AppException
     */
    public WordList getWordList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
        WordList list = null;
        String key = "activelist_" + loginUid + "_" + catalog + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getWordList(this, loginUid, catalog, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (WordList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (WordList) readObject(key);
            if (list == null)
                list = new WordList();
        }
        return list;
    }

    /**
     * 留言列表
     *
     * @param pageIndex
     * @return
     * @throws AppException
     */
    public MessageList getMessageList(int pageIndex, boolean isRefresh) throws AppException {
        MessageList list = null;
        String key = "messagelist_" + loginUid + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getMessageList(this, loginUid, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (MessageList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (MessageList) readObject(key);
            if (list == null)
                list = new MessageList();
        }
        return list;
    }

    /**
     * 博客评论列表
     *
     * @param id        博客Id
     * @param pageIndex
     * @return
     * @throws AppException
     */
    public BlogCommentList getBlogCommentList(int id, int pageIndex, boolean isRefresh) throws AppException {
        BlogCommentList list = null;
        String key = "blogcommentlist_" + id + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getBlogCommentList(this, id, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (BlogCommentList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (BlogCommentList) readObject(key);
            if (list == null)
                list = new BlogCommentList();
        }
        return list;
    }

    /**
     * 评论列表
     *
     * @param isHelp
     * @param uid
     * @param catalog   1新闻 2帖子 3动弹 4动态
     * @param id        某条新闻，帖子，动弹的id 或者某条留言的friendid
     * @param pageIndex
     * @return
     * @throws AppException
     */
    public CommentList getCommentList(boolean isHelp, int uid, int catalog, int id, int pageIndex, boolean isRefresh) throws AppException {
        CommentList list = null;
        String key = "commentlist_" + catalog + "_" + id + "_" + pageIndex + "_" + PAGE_SIZE;
        if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
            try {
                list = ApiClient.getCommentList(this, isHelp, catalog, uid, id, pageIndex, PAGE_SIZE);
                if (list != null && pageIndex == 0) {
                    Notice notice = list.getNotice();
                    list.setNotice(null);
                    list.setCacheKey(key);
                    saveObject(list, key);
                    list.setNotice(notice);
                }
            } catch (AppException e) {
                list = (CommentList) readObject(key);
                if (list == null)
                    throw e;
            }
        } else {
            list = (CommentList) readObject(key);
            if (list == null)
                list = new CommentList();
        }
        return list;
    }

    /**
     * 获取搜索列表
     *
     * @param catalog   全部:all 新闻:news  问答:post 软件:software 博客:blog 代码:code
     * @param content   搜索的内容
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws AppException
     */
    public SearchList getSearchList(String catalog, String content, int pageIndex, int pageSize) throws AppException {
        return ApiClient.getSearchList(this, catalog, content, pageIndex, pageSize);
    }

    /**
     * 发帖子
     *
     * @param post （uid、title、catalog、content、isNoticeMe）
     * @return
     * @throws AppException
     */
    public Result pubPost(Post post) throws AppException {
        return ApiClient.pubPost(this, post);
    }

    /**
     * 发动弹
     *
     * @param tweet - uid & msg & image
     * @return
     * @throws AppException
     */
    public Result pubTweet(Tweet tweet) throws AppException {
        return ApiClient.pubTweet(this, tweet);
    }

    /**
     * 删除动弹
     *
     * @param uid
     * @param tweetid
     * @return
     * @throws AppException
     */
    public Result delTweet(int uid, int tweetid) throws AppException {
        return ApiClient.delTweet(this, uid, tweetid);
    }

    /**
     * 发送留言
     *
     * @param uid      登录用户uid
     * @param receiver 接受者的用户id
     * @param content  消息内容，注意不能超过250个字符
     * @return
     * @throws AppException
     */
    public Result pubMessage(int uid, int receiver, String content) throws AppException {
        return ApiClient.pubMessage(this, uid, receiver, content);
    }

    /**
     * 转发留言
     *
     * @param uid      登录用户uid
     * @param receiver 接受者的用户名
     * @param content  消息内容，注意不能超过250个字符
     * @return
     * @throws AppException
     */
    public Result forwardMessage(int uid, String receiver, String content) throws AppException {
        return ApiClient.forwardMessage(this, uid, receiver, content);
    }

    /**
     * 删除留言
     *
     * @param uid      登录用户uid
     * @param friendid 留言者id
     * @return
     * @throws AppException
     */
    public Result delMessage(int uid, int friendid) throws AppException {
        return ApiClient.delMessage(this, uid, friendid);
    }

    /**
     * 发表评论
     *
     * @param catalog 1新闻  2帖子  3动弹  4动态
     * @param id      某条新闻，帖子，动弹的id
     * @param uid     用户uid
     * @param content 发表评论的内容
     * @param isHelp
     * @return
     * @throws AppException
     */
    public Result pubComment(int catalog, int id, int uid, String content, Boolean isHelp) throws AppException {
        return ApiClient.pubComment(this, catalog, id, uid, content, isHelp);
    }


    public Result plus(int id, int uid) throws AppException {
        return ApiClient.plus(this, id, uid);
    }


    public Result diffuse(int id, int uid, String content) throws AppException {
        return ApiClient.diffuse(this, id, uid, content);
    }

    /**
     * @param id       表示被评论的某条新闻，帖子，动弹的id 或者某条消息的 friendid
     * @param catalog  表示该评论所属什么类型：1新闻  2帖子  3动弹  4动态
     * @param replyid  表示被回复的单个评论id
     * @param authorid 表示该评论的原始作者id
     * @param author
     * @param uid      用户uid 一般都是当前登录用户uid
     * @param content  发表评论的内容
     * @param isHelp   @return
     * @throws AppException
     */
    public Result replyComment(int id, int catalog, int replyid, int authorid, String author, int uid, String content, boolean isHelp) throws AppException {
        return ApiClient.replyComment(this, id, catalog, replyid, authorid, author, uid, content, isHelp);
    }

    /**
     * 删除评论
     *
     * @param id       表示被评论对应的某条新闻,帖子,动弹的id 或者某条消息的 friendid
     * @param catalog  表示该评论所属什么类型：1新闻  2帖子  3动弹  4动态&留言
     * @param replyid  表示被回复的单个评论id
     * @param authorid 表示该评论的原始作者id
     * @param isHelp
     * @return
     * @throws AppException
     */
    public Result delComment(int id, int catalog, int replyid, int authorid, Boolean isHelp) throws AppException {
        return ApiClient.delComment(this, id, catalog, replyid, authorid, isHelp);
    }

    /**
     * 发表博客评论
     *
     * @param blog    博客id
     * @param uid     登陆用户的uid
     * @param content 评论内容
     * @return
     * @throws AppException
     */
    public Result pubBlogComment(int blog, int uid, String content) throws AppException {
        return ApiClient.pubBlogComment(this, blog, uid, content);
    }

    /**
     * 发表博客评论
     *
     * @param blog     博客id
     * @param uid      登陆用户的uid
     * @param content  评论内容
     * @param reply_id 评论id
     * @param objuid   被评论的评论发表者的uid
     * @return
     * @throws AppException
     */
    public Result replyBlogComment(int blog, int uid, String content, int reply_id, int objuid) throws AppException {
        return ApiClient.replyBlogComment(this, blog, uid, content, reply_id, objuid);
    }

    /**
     * 删除博客评论
     *
     * @param uid      登录用户的uid
     * @param blogid   博客id
     * @param replyid  评论id
     * @param authorid 评论发表者的uid
     * @param owneruid 博客作者uid
     * @return
     * @throws AppException
     */
    public Result delBlogComment(int uid, int blogid, int replyid, int authorid, int owneruid) throws AppException {
        return ApiClient.delBlogComment(this, uid, blogid, replyid, authorid, owneruid);
    }

    /**
     * 删除博客
     *
     * @param uid       登录用户的uid
     * @param authoruid 博客作者uid
     * @param id        博客id
     * @return
     * @throws AppException
     */
    public Result delBlog(int uid, int authoruid, int id) throws AppException {
        return ApiClient.delBlog(this, uid, authoruid, id);
    }

    /**
     * 用户添加收藏
     *
     * @param uid   用户UID
     * @param objid 比如是新闻ID 或者问答ID 或者动弹ID
     * @param type  1:软件 2:话题 3:博客 4:新闻 5:代码
     * @return
     * @throws AppException
     */
    public Result addFavorite(int uid, int objid, int type) throws AppException {
        return ApiClient.addFavorite(this, uid, objid, type);
    }

    /**
     * 用户删除收藏
     *
     * @param uid   用户UID
     * @param objid 比如是新闻ID 或者问答ID 或者动弹ID
     * @param type  1:软件 2:话题 3:博客 4:新闻 5:代码
     * @return
     * @throws AppException
     */
    public Result delFavorite(int uid, int objid, int type) throws AppException {
        return ApiClient.delFavorite(this, uid, objid, type);
    }



    /**
     * 清除登录信息
     */
    public void cleanThirdLoginInfo() {
        this.loginUid = 0;
        this.login = false;
        removeProperty(
                "user.uid",
                "user.nickname",
                "user.account",
                "user.avatar_url",
                "user.school_id",
                "user.friends_count",
                "user.followers_count",
                "user.active_credits"
        );
    }
    /**
     * 保存第三方登录信息
     *
     * @param user
     */
    public void saveThridLoginInfo(final User user) {
        cleanLoginInfo();
        cleanAccountInfo();
        saveLoginUserInfo(user);
    }

    /**
     * 保存登录信息
     *
     * @param user
     */
    public void saveLoginInfo(final User user) {
        cleanThirdLoginInfo();
        saveLoginUserInfo(user);
        setProperty("user.is_remember", String.valueOf(user.isRemember()));
        setProperty("user.password", CyptoUtils.encode("ihelpooApp", user.getPwd()));
    }

    private void saveLoginUserInfo(final User user) {
        this.loginUid = user.getUid();
        this.login = true;
        setProperties(new Properties() {{
            setProperty("user.uid", String.valueOf(user.getUid()));
            setProperty("user.nickname", user.getNickname());
            setProperty("user.avatar_url", user.getAvatar_url());
            setProperty("user.account", user.getEmail());
            setProperty("user.school_id", user.getSchool_id());
            setProperty("user.friends_count", String.valueOf(user.getFriends_count()));
            setProperty("user.followers_count", String.valueOf(user.getFollowers_count()));
            setProperty("user.active_credits", String.valueOf(user.getActive_credits()));
        }});
    }

    public int getUserActiveCredits(){
        int activeCredits = 0;
        String activeCreditsStr = getProperty("user.active_credits");
        if(activeCreditsStr != null && activeCreditsStr.matches("\\d+")){
            activeCredits = Integer.parseInt(activeCreditsStr);
        }
        return activeCredits;
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = 0;
        this.login = false;
        removeProperty(
                "user.uid",
                "user.nickname",
                "user.avatar_url",
                "user.school_id",
                "user.friends_count",
                "user.followers_count",
                "user.active_credits"
        );
    }


    /**
     * 清除登录信息
     */
    public void cleanAccountInfo() {
        this.loginUid = 0;
        this.login = false;
        removeProperty(
                "user.account",
                "user.password"
        );
    }

    /**
     * 获取登录信息
     *
     * @return
     */
    public User getLoginInfo() {
        User lu = new User();
        if(getProperty("user.account") == null){
            return getThirdLoginInfo();
        }
        makeUserInfo(lu);
        lu.setPwd(CyptoUtils.decode("ihelpooApp", getProperty("user.password")));
        lu.setRemember(StringUtils.toBool(getProperty("user.is_remember")));
        return lu;
    }


    /**
     * 获取登录信息
     *
     * @return
     */
    public User getThirdLoginInfo() {
        User lu = new User();
        makeUserInfo(lu);
        return lu;
    }

    private void makeUserInfo(User lu) {
        lu.setUid(StringUtils.toInt(getProperty("user.uid"), 0));
        lu.setNickname(getProperty("user.nickname"));
        lu.setAvatar_url(getProperty("user.avatar_url"));
        lu.setEmail(getProperty("user.account"));
        lu.setSchool_id(getProperty("user.school_id"));
        lu.setFriends_count(StringUtils.toInt(getProperty("user.friends_count"), 0));
        lu.setFollowers_count(StringUtils.toInt(getProperty("user.followers_count"), 0));
        lu.setActive_credits(StringUtils.toInt(getProperty("user.active_credits"), 0));
    }

    /**
     * 保存用户头像
     *
     * @param fileName
     * @param bitmap
     */
    public void saveUserFace(String fileName, Bitmap bitmap) {
        try {
            ImageUtils.saveImage(this, fileName, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户头像
     *
     * @param key
     * @return
     * @throws AppException
     */
    public Bitmap getUserFace(String key) throws AppException {
        FileInputStream fis = null;
        try {
            fis = openFileInput(key);
            return BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            throw AppException.run(e);
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 是否加载显示文章图片
     *
     * @return
     */
    public boolean isLoadImage() {
        String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
        //默认是加载的
        if (StringUtils.isEmpty(perf_loadimage))
            return true;
        else
            return StringUtils.toBool(perf_loadimage);
    }

    /**
     * 设置是否加载文章图片
     *
     * @param b
     */
    public void setConfigLoadimage(boolean b) {
        setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
    }

    /**
     * 是否发出提示音
     *
     * @return
     */
    public boolean isVoice() {
        String perf_voice = getProperty(AppConfig.CONF_VOICE);
        //默认是开启提示声音
        if (StringUtils.isEmpty(perf_voice))
            return true;
        else
            return StringUtils.toBool(perf_voice);
    }

    /**
     * 设置是否发出提示音
     *
     * @param b
     */
    public void setConfigVoice(boolean b) {
        setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
    }

    /**
     * 是否启动检查更新
     *
     * @return
     */
    public boolean isCheckUp() {
        String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
        //默认是开启
        if (StringUtils.isEmpty(perf_checkup))
            return true;
        else
            return StringUtils.toBool(perf_checkup);
    }

    /**
     * 设置启动检查更新
     *
     * @param b
     */
    public void setConfigCheckUp(boolean b) {
        setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
    }

    /**
     * 是否左右滑动
     *
     * @return
     */
    public boolean isScroll() {
        String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
        //默认是关闭左右滑动
        if (StringUtils.isEmpty(perf_scroll))
            return false;
        else
            return StringUtils.toBool(perf_scroll);
    }

    /**
     * 设置是否左右滑动
     *
     * @param b
     */
    public void setConfigScroll(boolean b) {
        setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
    }

    /**
     * 是否Https登录
     *
     * @return
     */
    public boolean isHttpsLogin() {
        String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
        //默认是http
        if (StringUtils.isEmpty(perf_httpslogin))
            return false;
        else
            return StringUtils.toBool(perf_httpslogin);
    }

    /**
     * 设置是是否Https登录
     *
     * @param b
     */
    public void setConfigHttpsLogin(boolean b) {
        setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
    }

    /**
     * 清除保存的缓存
     */
    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }

    /**
     * 判断缓存数据是否可读
     *
     * @param cachefile
     * @return
     */
    private boolean isReadDataCache(String cachefile) {
        return readObject(cachefile) != null;
    }

    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @return
     */
    private boolean isExistDataCache(String cachefile) {
        boolean exist = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 判断缓存是否失效
     *
     * @param cachefile
     * @return
     */
    public boolean isCacheDataFailure(String cachefile) {
        boolean failure = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
            failure = true;
        else if (!data.exists())
            failure = true;
        return failure;
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        //清除webview缓存
        File file = CacheManager.getCacheFileBaseDir();
        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();
            }
            file.delete();
        }
        deleteDatabase("webview.db");
        deleteDatabase("webview.db-shm");
        deleteDatabase("webview.db-wal");
        deleteDatabase("webviewCache.db");
        deleteDatabase("webviewCache.db-shm");
        deleteDatabase("webviewCache.db-wal");
        //清除数据缓存
        clearCacheFolder(getFilesDir(), System.currentTimeMillis());
        clearCacheFolder(getCacheDir(), System.currentTimeMillis());
        //2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            clearCacheFolder(MethodsCompat.getExternalCacheDir(this), System.currentTimeMillis());
        }
        //清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }

    /**
     * 清除缓存目录
     *
     * @param dir     目录
     * @param curTime 当前系统时间
     * @return
     */
    private int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    /**
     * 将对象保存到内存缓存中
     *
     * @param key
     * @param value
     */
    public void setMemCache(String key, Object value) {
        memCacheRegion.put(key, value);
    }

    /**
     * 从内存缓存中获取对象
     *
     * @param key
     * @return
     */
    public Object getMemCache(String key) {
        return memCacheRegion.get(key);
    }

    /**
     * 保存磁盘缓存
     *
     * @param key
     * @param value
     * @throws IOException
     */
    public void setDiskCache(String key, String value) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
            fos.write(value.getBytes());
            fos.flush();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取磁盘缓存数据
     *
     * @param key
     * @return
     * @throws IOException
     */
    public String getDiskCache(String key) throws IOException {
        FileInputStream fis = null;
        try {
            fis = openFileInput("cache_" + key + ".data");
            byte[] datas = new byte[fis.available()];
            fis.read(datas);
            return new String(datas);
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     * @throws IOException
     */
    public boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(file, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     * @throws IOException
     */
    public Serializable readObject(String file) {
        if (!isExistDataCache(file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            //反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取内存中保存图片的路径
     *
     * @return
     */
    public String getSaveImagePath() {
        return saveImagePath;
    }

    /**
     * 设置内存中保存图片的路径
     *
     * @return
     */
    public void setSaveImagePath(String saveImagePath) {
        this.saveImagePath = saveImagePath;
    }

    public MobileRegisterResult mobileRegister(String mobileCode, String mobileNo, String pwd, String schoolId) throws AppException {
        return ApiClient.mobileRegister(this, mobileCode, mobileNo, pwd, schoolId);
    }


    public MobileCodeResult mobileCode(String mobileNo) throws AppException {
        return ApiClient.mobileCode(this, mobileNo);
    }

    //TODO cache and flush
    public SchoolList getSchoolList() throws AppException {
        return ApiClient.getSchoolList(this);
    }

    public Result updateNickname(int loginUid, String nickname) throws AppException {
        return ApiClient.updateNickname(this, loginUid, nickname);
    }

    public Result updateGender(int loginUid, String gender) throws AppException {
        return ApiClient.updateGender(this, loginUid, gender);
    }

    public Result updateEnrol(int loginUid, String newEnrol) throws AppException {
        return ApiClient.updateEnrol(this, loginUid, newEnrol);
    }

    public Result updateIntro(int loginUid, String intro) throws AppException {
        return ApiClient.updateIntro(this, loginUid, intro);
    }

    public AcademyList getAcademyList(int schoolId) throws AppException {
        return ApiClient.getAcademyList(this, schoolId);
    }

    public MajorList getMajorList(int schoolId, Integer academyId) throws AppException {
        return ApiClient.getMajorList(this, schoolId, academyId);
    }

    public DormList getDormList(int schoolId) throws AppException {
        return ApiClient.getDormList(this, schoolId);
    }

    public Result updateMajor(int loginUid, String schoolId, String academyId, String majorId, String dormId) throws AppException {
        return ApiClient.updateMajor(this, loginUid, schoolId, academyId, majorId, dormId);
    }

    public MobileRegisterResult thirdLogin(String thirdUid, String thirdType, Integer schoolId, String nickname, String status) throws AppException {
        return ApiClient.thirdLogin(this, thirdUid, thirdType, schoolId, nickname, status);
    }

    public void saveLoginSchool(String schoolId) {
        setProperty("user.school_id", schoolId);
    }

    public void saveLoginNickname(String nickname) {
        setProperty("user.nickname", nickname);
    }
}
