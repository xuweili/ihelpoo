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
public class MobileCodeResult {

    public final static String NODE_CODE = "code";

    private int errorCode;
    private String errorMessage;
    private RegisterCode registerCode;




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
    public static MobileCodeResult parse(InputStream stream) throws IOException, AppException {
        MobileCodeResult res = null;
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
                        if (tag.equalsIgnoreCase("result")) {
                            res = new MobileCodeResult();
                        } else if (res != null) {
                            if (tag.equalsIgnoreCase("errorCode")) {
                                res.errorCode = StringUtils.toInt(xmlParser.nextText(), -1);
                            } else if (tag.equalsIgnoreCase("errorMessage")) {
                                res.errorMessage = xmlParser.nextText().trim();
                            } else if (tag.equalsIgnoreCase("code")) {
                                res.registerCode = new RegisterCode();
                                res.registerCode.setCode(StringUtils.toInt(xmlParser.nextText(), 0));
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

    public RegisterCode getRegisterCode() {
        return registerCode;
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

    @Override
    public String toString() {
        return String.format("RESULT: CODE:%d,MSG:%s", errorCode, errorMessage);
    }

    public static class RegisterCode extends Base {
        private Integer code;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }
}
