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
import java.util.ArrayList;
import java.util.List;

import com.ihelpoo.app.AppException;
import com.ihelpoo.app.bean.Active.ObjectReply;
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 动态列表实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class WordList extends Entity{

	public final static int CATALOG_SYSTEM = 0;//最新
	public final static int CATALOG_ATME = 1;//@我
	public final static int CATALOG_COMMENT = 2;//评论
	public final static int CATALOG_ACTIVE = 3;//我自己
    public final static int CATALOG_CHAT = 4;//我自己
	
	private int pageSize;
	private int activeCount;
	private List<Active> activelist = new ArrayList<Active>();
	
	public int getPageSize() {
		return pageSize;
	}
	public int getActiveCount() {
		return activeCount;
	}
	public List<Active> getActivelist() {
		return activelist;
	}

	public static WordList parse(InputStream inputStream) throws IOException, AppException {
		WordList activelist = new WordList();
		Active active = null;
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
			    		if(tag.equalsIgnoreCase("activeCount")) 
			    		{
			    			activelist.activeCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("page_size"))
			    		{
			    			activelist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase("active")) 
			    		{ 
			    			active = new Active();
			    		}
			    		else if(active != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	active.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("portrait"))
				            {			            	
				            	active.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("message"))
				            {			            	
				            	active.setMessage(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("author"))
				            {			            	
				            	active.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("authorid"))
				            {			            	
				            	active.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase("catalog"))
				            {			            	
				            	active.setActiveType(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("objectID"))
				            {			            	
				            	active.setObjectId(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("objecttype"))
				            {			            	
				            	active.setObjectType(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("objectcatalog"))
				            {			            	
				            	active.setObjectCatalog(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("objecttitle"))
				            {			            	
				            	active.setObjectTitle(xmlParser.nextText());			            	
				            }
                            else if(tag.equalsIgnoreCase("objectSayType"))
                            {
                                active.setObjectSayType(xmlParser.nextText());
                            }
				            else if(tag.equalsIgnoreCase("objectreply"))
				            {			            	
				            	active.setObjectReply(new ObjectReply());	            	
				            }
				            else if(active.getObjectReply()!=null && tag.equalsIgnoreCase("objectname"))
				            {			            	
				            	active.getObjectReply().objectName = xmlParser.nextText();		            	
				            }
				            else if(active.getObjectReply()!=null && tag.equalsIgnoreCase("objectbody"))
				            {			            	
				            	active.getObjectReply().objectBody = xmlParser.nextText();		            	
				            }
				            else if(tag.equalsIgnoreCase("commentCount"))
				            {			            	
				            	active.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	active.setPubDate(xmlParser.nextText());			            	
				            }
				            else if(tag.equalsIgnoreCase("tweetimage"))
				            {			            	
				            	active.setTweetimage(xmlParser.nextText());			            	
				            }
                            else if(tag.equalsIgnoreCase("imgBig"))
                            {
                                active.setImgBig(xmlParser.nextText());
                            }
				            else if(tag.equalsIgnoreCase("appclient"))
				            {			            	
				            	active.setAppClient(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	active.setUrl(xmlParser.nextText());			            	
				            }
                            else if(tag.equalsIgnoreCase("diffusionCo")){
                                active.setDiffusionCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("academy")){
                                active.setAcademy(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase("authorType")){
                                active.setAuthorType(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase("authorGossip")){
                                active.setAuthorGossip(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase("online")){
                                active.setOnline(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("activeRank")){
                                active.setRank(StringUtils.toInt(xmlParser.nextText(),1));
                            }
			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	activelist.setNotice(new Notice());
			    		}
			            else if(activelist.getNotice() != null)
			    		{
                            if(tag.equalsIgnoreCase("systemCount"))
                            {
                                activelist.getNotice().setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
                            }
                            else if(tag.equalsIgnoreCase("atmeCount"))
                            {
                                activelist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("commentCount"))
                            {
                                activelist.getNotice().setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("activeCount"))
                            {
                                activelist.getNotice().setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("chatCount"))
                            {
                                activelist.getNotice().setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("active") && active != null) { 
				       		activelist.getActivelist().add(active); 
				       		active = null; 
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
        return activelist;       
	}
}
