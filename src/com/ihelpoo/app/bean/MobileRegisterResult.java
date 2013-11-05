package com.ihelpoo.app.bean;

import android.util.Xml;

import com.ihelpoo.app.AppException;
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wdx on 10/18/13.
 */
public class MobileRegisterResult {

    public final static String NODE_UID = "uid";
    public final static String NODE_SCHOOL_ID = "schoolId";
    public final static String NODE_NICKNAME = "name";

    private int errorCode;
    private String errorMessage;
    private User user;


    public boolean OK() {
        return errorCode == 1;
    }

    /**
     * 解析调用结果
     *
     * @param stream
     * @return
     * @throws java.io.IOException
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    public static MobileRegisterResult parse(InputStream stream) throws IOException, AppException {
        MobileRegisterResult res = null;
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
                        if (tag.equalsIgnoreCase("result"))
                        {
                            res = new MobileRegisterResult();
                        }
                        else if (res != null)
                        {
                            if (tag.equalsIgnoreCase("errorCode"))
                            {
                                res.errorCode = StringUtils.toInt(xmlParser.nextText(), -1);
                            }
                            else if (tag.equalsIgnoreCase("errorMessage"))
                            {
                                res.errorMessage = xmlParser.nextText().trim();
                            }
                            else if(tag.equalsIgnoreCase("user"))
                            {
                                res.user = new User();
                            }
                            else if(res.user != null)
                            {

                                if (tag.equalsIgnoreCase("uid")) {
                                    res.user.setUid(StringUtils.toInt(xmlParser.nextText(), 0));
                                } else if (tag.equalsIgnoreCase("email")) {
                                    res.user.setEmail(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("nickname")) {
                                    res.user.setNickname(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("gender")) {
                                    res.user.setGender(StringUtils.toInt(xmlParser.nextText()));
                                } else if (tag.equalsIgnoreCase("birthday")) {
                                    res.user.setBirthday(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("enrol_time")) {
                                    res.user.setEnrol_time(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("user_type")) {
                                    res.user.setUser_type(StringUtils.toInt(xmlParser.nextText()));
                                } else if (tag.equalsIgnoreCase("login_days")) {
                                    res.user.setLogin_days(StringUtils.toInt(xmlParser.nextText()));
                                } else if (tag.equalsIgnoreCase("online_status")) {
                                    res.user.setOnline_status(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("active_credits")) {
                                    res.user.setActive_credits(StringUtils.toInt(xmlParser.nextText()));
                                } else if (tag.equalsIgnoreCase("avatar_url")) {
                                    res.user.setAvatar_url(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("self_intro")) {
                                    res.user.setSelf_intro(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("followers_count")) {
                                    res.user.setFollowers_count(StringUtils.toInt(xmlParser.nextText()));
                                } else if (tag.equalsIgnoreCase("friends_count")) {
                                    res.user.setFriends_count(StringUtils.toInt(xmlParser.nextText()));
                                } else if (tag.equalsIgnoreCase("school_id")) {
                                    res.user.setSchool_id(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("school_name")) {
                                    res.user.setSchool_name(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("academy_name")) {
                                    res.user.setAcademy_name(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("major_name")) {
                                    res.user.setMajor_name(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("school_domain")) {
                                    res.user.setSchool_domain(xmlParser.nextText());
                                } else if (tag.equalsIgnoreCase("level")) {
                                    res.user.setLevel(StringUtils.toInt(xmlParser.nextText()));
                                } else if (tag.equalsIgnoreCase("relation")) {
                                    res.user.setRelation(StringUtils.toInt(xmlParser.nextText()));
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

        return res;

    }

    public User getUser() {
        return user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString(){
        return String.format("RESULT: CODE:%d,MSG:%s", errorCode, errorMessage);
    }
}
