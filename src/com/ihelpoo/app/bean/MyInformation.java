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

package com.ihelpoo.app.bean;

import java.io.IOException;
import java.io.InputStream;

import com.ihelpoo.app.AppException;
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 我的个人信息实体类
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class MyInformation extends Entity {


    private int uid;
    private String nickname;
    private String email;
    private int email_verified;
    private int gender;
    private String birthday;
    private String enrol_time;
    private int user_type;
    private String login_time;
    private int last_login;
    private String create_time;
    private int login_days;
    private String online_status;
    private int active_credits;
    private String avatar_type;
    private String avatar_url;
    private String avatar_preview;
    private String web_theme;
    private String self_intro;
    private String real_name;
    private int followers_count;
    private int friends_count;
    private String school_id;
    private String school_name;
    private String academy_name;
    private String major_name;
    private String school_domain;
    private String dorm_name;
    private int level;
    private int relation;

    public static MyInformation parse(InputStream stream) throws IOException, AppException {
        MyInformation user = null;
        // 获得XmlPullParser解析器
        XmlPullParser xmlParser = Xml.newPullParser();
        try {
            xmlParser.setInput(stream, Base.UTF8);
            // 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType = xmlParser.getEventType();
            // 一直循环，直到文档结束
            while (evtType != XmlPullParser.END_DOCUMENT) {
                String tag = xmlParser.getName();
                switch (evtType) {

                    case XmlPullParser.START_TAG:
                        // 如果是标签开始，则说明需要实例化对象了
                        if (tag.equalsIgnoreCase("user")) {
                            user = new MyInformation();
                        } else if (user != null) {
                            if (tag.equalsIgnoreCase("uid")) {
                                user.uid = StringUtils.toInt(xmlParser.nextText(), 0);
                            } else if (tag.equalsIgnoreCase("email")) {
                                user.setEmail(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("nickname")) {
                                user.setNickname(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("gender")) {
                                user.setGender(StringUtils.toInt(xmlParser.nextText()));
                            } else if (tag.equalsIgnoreCase("birthday")) {
                                user.setBirthday(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("enrol_time")) {
                                user.setEnrol_time(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("user_type")) {
                                user.setUser_type(StringUtils.toInt(xmlParser.nextText()));
                            } else if (tag.equalsIgnoreCase("login_days")) {
                                user.setLogin_days(StringUtils.toInt(xmlParser.nextText()));
                            } else if (tag.equalsIgnoreCase("online_status")) {
                                user.setOnline_status(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("active_credits")) {
                                user.setActive_credits(StringUtils.toInt(xmlParser.nextText()));
                            } else if (tag.equalsIgnoreCase("avatar_preview")) {
                                user.setAvatar_preview(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("avatar_url")) {
                                user.setAvatar_url(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("self_intro")) {
                                user.setSelf_intro(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("followers_count")) {
                                user.setFollowers_count(StringUtils.toInt(xmlParser.nextText()));
                            } else if (tag.equalsIgnoreCase("friends_count")) {
                                user.setFriends_count(StringUtils.toInt(xmlParser.nextText()));
                            } else if (tag.equalsIgnoreCase("school_id")) {
                                user.setSchool_id(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("school_name")) {
                                user.setSchool_name(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("academy_name")) {
                                user.setAcademy_name(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("major_name")) {
                                user.setMajor_name(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("school_domain")) {
                                user.setSchool_domain(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("dorm_name")) {
                                user.setDorm_name(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("level")) {
                                user.setLevel(StringUtils.toInt(xmlParser.nextText()));
                            } else if (tag.equalsIgnoreCase("relation")) {
                                user.setRelation(StringUtils.toInt(xmlParser.nextText()));
                            }
                            //通知信息
                            else if (tag.equalsIgnoreCase("notice")) {
                                user.setNotice(new Notice());
                            } else if (user.getNotice() != null) {
                                if(tag.equalsIgnoreCase("systemCount"))
                                {
                                    user.getNotice().setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
                                }
                                else if(tag.equalsIgnoreCase("atmeCount"))
                                {
                                    user.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("commentCount"))
                                {
                                    user.getNotice().setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("activeCount"))
                                {
                                    user.getNotice().setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("chatCount"))
                                {
                                    user.getNotice().setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 如果xml没有结束，则导航到下一个节点
                evtType = xmlParser.next();
            }

        } catch (XmlPullParserException e) {
            throw AppException.xml(e);
        } finally {
            stream.close();
        }
        return user;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(int email_verified) {
        this.email_verified = email_verified;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEnrol_time() {
        return enrol_time;
    }

    public void setEnrol_time(String enrol_time) {
        this.enrol_time = enrol_time;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public int getLast_login() {
        return last_login;
    }

    public void setLast_login(int last_login) {
        this.last_login = last_login;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getLogin_days() {
        return login_days;
    }

    public void setLogin_days(int login_days) {
        this.login_days = login_days;
    }

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public int getActive_credits() {
        return active_credits;
    }

    public void setActive_credits(int active_credits) {
        this.active_credits = active_credits;
    }

    public String getAvatar_type() {
        return avatar_type;
    }

    public void setAvatar_type(String avatar_type) {
        this.avatar_type = avatar_type;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getWeb_theme() {
        return web_theme;
    }

    public void setWeb_theme(String web_theme) {
        this.web_theme = web_theme;
    }

    public String getSelf_intro() {
        return self_intro;
    }

    public void setSelf_intro(String self_intro) {
        this.self_intro = self_intro;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(int friends_count) {
        this.friends_count = friends_count;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getAcademy_name() {
        return academy_name;
    }

    public void setAcademy_name(String academy_name) {
        this.academy_name = academy_name;
    }

    public String getMajor_name() {
        return major_name;
    }

    public void setMajor_name(String major_name) {
        this.major_name = major_name;
    }

    public String getSchool_domain() {
        return school_domain;
    }

    public void setSchool_domain(String school_domain) {
        this.school_domain = school_domain;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public String getDorm_name() {
        return dorm_name;
    }

    public void setDorm_name(String dorm_name) {
        this.dorm_name = dorm_name;
    }

    public String getAvatar_preview() {
        return avatar_preview;
    }

    public void setAvatar_preview(String avatar_preview) {
        this.avatar_preview = avatar_preview;
    }
}
