package com.ihelpoo.app.bean;

import android.util.Xml;

import com.ihelpoo.app.AppException;
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wdx on 10/18/13.
 */
public class SchoolList extends Entity {

    private List<SchoolInfo> schoolList = new ArrayList<SchoolInfo>();

    public List<SchoolInfo> getSchoolList() {
        return schoolList;
    }


    public static SchoolList parse(InputStream inputStream) throws IOException, AppException {
        SchoolList schoolList1 = new SchoolList();
        SchoolInfo schoolInfo = null;
        //获得XmlPullParser解析器
        XmlPullParser xmlParser = Xml.newPullParser();
        try {
            xmlParser.setInput(inputStream, UTF8);
            //获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType=xmlParser.getEventType();
            //一直循环，直到文档结束
            while(evtType!=XmlPullParser.END_DOCUMENT){
                String tag = xmlParser.getName();
                switch(evtType){
                    case XmlPullParser.START_TAG:
                        if (tag.equalsIgnoreCase("school"))
                        {
                            schoolInfo = new SchoolInfo();
                        }
                        else if(schoolInfo != null)
                        {
                            if(tag.equalsIgnoreCase("id"))
                            {
                                schoolInfo.id = StringUtils.toInt(xmlParser.nextText(),0);
                            }
                            else if(tag.equalsIgnoreCase("schoolName"))
                            {
                                schoolInfo.setSchool(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase("initial"))
                            {
                                schoolInfo.setInitial(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase("city_op"))
                            {
                                schoolInfo.setCity_op(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("domain"))
                            {
                                schoolInfo.setDomain(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase("domain_main"))
                            {
                                schoolInfo.setDomain_main(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase("time"))
                            {
                                schoolInfo.setTime(StringUtils.toInt(xmlParser.nextText()));
                            }
                            else if(tag.equalsIgnoreCase("status"))
                            {
                                schoolInfo.setStatus(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        //如果遇到标签结束，则把对象添加进集合中
                        if (tag.equalsIgnoreCase("school") && schoolInfo != null) {
                            schoolList1.getSchoolList().add(schoolInfo);
//                            schoolInfo = null;
                        }
                        break;
                }
                //如果xml没有结束，则导航到下一个节点
                evtType=xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            throw AppException.xml(e);
        } finally {
            inputStream.close();
        }
        return schoolList1;
    }
}
