package com.ihelpoo.app.bean;

import android.util.Xml;

import com.ihelpoo.app.AppException;
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wdx on 10/18/13.
 */
public class AcademyList extends Entity {

    private List<AcademyInfo> academyList = new ArrayList<AcademyInfo>();

    public List<AcademyInfo> getAcademyList() {
        return academyList;
    }


    public static AcademyList parse(InputStream inputStream) throws IOException, AppException {
        AcademyList academyList1 = new AcademyList();
        AcademyInfo academyInfo = null;
        //获得XmlPullParser解析器
        XmlPullParser xmlParser = Xml.newPullParser();
        try {
            xmlParser.setInput(inputStream, UTF8);
            //获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType = xmlParser.getEventType();
            //一直循环，直到文档结束
            while (evtType != XmlPullParser.END_DOCUMENT) {
                String tag = xmlParser.getName();
                switch (evtType) {
                    case XmlPullParser.START_TAG:
                        if (tag.equalsIgnoreCase("academy")) {
                            academyInfo = new AcademyInfo();
                        } else if (academyInfo != null) {
                            if (tag.equalsIgnoreCase("id")) {
                                academyInfo.setId(StringUtils.toInt(xmlParser.nextText(), 0));
                            } else if (tag.equalsIgnoreCase("academyName")) {
                                academyInfo.setAcademyName(xmlParser.nextText());
                            } else if (tag.equalsIgnoreCase("schoolId")) {
                                academyInfo.setSchoolId(StringUtils.toInt(xmlParser.nextText(), 0));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        //如果遇到标签结束，则把对象添加进集合中
                        if (tag.equalsIgnoreCase("academy") && academyInfo != null) {
                            academyList1.getAcademyList().add(academyInfo);
//                            academyInfo = null;
                        }
                        break;
                }
                //如果xml没有结束，则导航到下一个节点
                evtType = xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            throw AppException.xml(e);
        } finally {
            inputStream.close();
        }
        return academyList1;
    }
}
