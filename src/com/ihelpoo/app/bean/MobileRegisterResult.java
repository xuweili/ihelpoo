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
    private RegisterUser user;


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
                                res.user = new RegisterUser();
                            }
                            else if(res.user != null)
                            {
                                if(tag.equalsIgnoreCase(NODE_UID))
                                {
                                    res.user.setUid(StringUtils.toInt(xmlParser.nextText(), 0));
                                }
                                else if(tag.equalsIgnoreCase(NODE_NICKNAME))
                                {
                                    res.user.setNickname(xmlParser.nextText());
                                }
                                else if(tag.equalsIgnoreCase(NODE_SCHOOL_ID))
                                {
                                    res.user.setSchoolId(StringUtils.toInt(xmlParser.nextText()));
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


    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public RegisterUser getUser() {
        return user;
    }

    public void setUser(RegisterUser user) {
        this.user = user;
    }

    @Override
    public String toString(){
        return String.format("RESULT: CODE:%d,MSG:%s", errorCode, errorMessage);
    }
    public static class RegisterUser extends Base {

        private Integer uid;
        private Integer schoolId;
        private String nickname;


        public Integer getUid() {
            return uid;
        }

        public void setUid(Integer uid) {
            this.uid = uid;
        }

        public Integer getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(Integer schoolId) {
            this.schoolId = schoolId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

    }
}
