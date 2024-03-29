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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ihelpoo.app.AppException;
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 软件列表实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class SoftwareList extends Entity{
	
	public final static String TAG_RECOMMEND = "recommend";//推荐
	public final static String TAG_LASTEST = "time";//最新
	public final static String TAG_HOT = "view";//热门
	public final static String TAG_CHINA = "list_cn";//国产
	
	private int softwarecount;
	private int pagesize;
	private List<Software> softwarelist = new ArrayList<Software>();
	
	public static class Software implements Serializable {
		public String name;
		public String description;
		public String url;
	}

	public int getSoftwarecount() {
		return softwarecount;
	}
	public void setSoftwarecount(int softwarecount) {
		this.softwarecount = softwarecount;
	}
	public int getPageSize() {
		return pagesize;
	}
	public void setPageSize(int pagesize) {
		this.pagesize = pagesize;
	}
	public List<Software> getSoftwarelist() {
		return softwarelist;
	}
	public void setSoftwarelist(List<Software> softwarelist) {
		this.softwarelist = softwarelist;
	}
	
	public static SoftwareList parse(InputStream inputStream) throws IOException, AppException {
		SoftwareList softwarelist = new SoftwareList();
		Software software = null;
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
			    		if(tag.equalsIgnoreCase("softwarecount")) 
			    		{
			    			softwarelist.setSoftwarecount(StringUtils.toInt(xmlParser.nextText(), 0));
			    		}
			    		else if(tag.equalsIgnoreCase("pagesize")) 
			    		{
			    			softwarelist.setPageSize(StringUtils.toInt(xmlParser.nextText(),0));
			    		}
			    		else if (tag.equalsIgnoreCase("software")) 
			    		{ 
			    			software = new Software();
			    		}
			    		else if(software != null)
			    		{	
				            if(tag.equalsIgnoreCase("name"))
				            {			      
				            	software.name = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("description"))
				            {			            	
				            	software.description = xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	software.url = xmlParser.nextText();
				            }
				            
			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	softwarelist.setNotice(new Notice());
			    		}
			            else if(softwarelist.getNotice() != null)
			    		{
                            if(tag.equalsIgnoreCase("systemCount"))
                            {
                                softwarelist.getNotice().setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
                            }
                            else if(tag.equalsIgnoreCase("atmeCount"))
                            {
                                softwarelist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("commentCount"))
                            {
                                softwarelist.getNotice().setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("activeCount"))
                            {
                                softwarelist.getNotice().setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("chatCount"))
                            {
                                softwarelist.getNotice().setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("software") && software != null) { 
				       		softwarelist.getSoftwarelist().add(software); 
				       		software = null; 
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
        return softwarelist;       
	}
}
