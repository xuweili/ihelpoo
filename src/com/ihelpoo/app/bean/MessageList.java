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
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 消息列表实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class MessageList extends Entity{

	private int pageSize;
	private int messageCount;
	private List<Messages> messagelist = new ArrayList<Messages>();
	
	public int getPageSize() {
		return pageSize;
	}
	public int getMessageCount() {
		return messageCount;
	}
	public List<Messages> getMessagelist() {
		return messagelist;
	}

	public static MessageList parse(InputStream inputStream) throws IOException, AppException {
		MessageList msglist = new MessageList();
		Messages msg = null;
        //获得XmlPullParser解析器
        XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
            int evtType=xmlParser.getEventType();
			//一直循环，直到文档结束    
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
	    		String tag = xmlParser.getName(); 
	    		int depth = xmlParser.getDepth();
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if(depth==2 && tag.equalsIgnoreCase("message_count"))
			    		{
			    			msglist.messageCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("page_size"))
			    		{
			    			msglist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase("message")) 
			    		{ 
			    			msg = new Messages();
			    		}
			    		else if(msg != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	msg.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("portrait"))
				            {			            	
				            	msg.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("friendid"))
				            {
				            	msg.setFriendId(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("friendname"))
				            {
				            	msg.setFriendName(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("content"))
				            {			            	
				            	msg.setContent(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("sender"))
				            {			            	
				            	msg.setSender(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("senderid"))
				            {			            	
				            	msg.setSenderId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(depth==4 && tag.equalsIgnoreCase("messageCount"))
				            {			            	
				            	msg.setMessageCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	msg.setPubDate(xmlParser.nextText());     	
				            }
				            else if(tag.equalsIgnoreCase("del"))
				            {			            	
				            	msg.setDel(StringUtils.toInt(xmlParser.nextText(), 0));
				            }
			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	msglist.setNotice(new Notice());
			    		}
			            else if(msglist.getNotice() != null)
			    		{
                            if(tag.equalsIgnoreCase("systemCount"))
                            {
                                msglist.getNotice().setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
                            }
                            else if(tag.equalsIgnoreCase("atmeCount"))
                            {
                                msglist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("commentCount"))
                            {
                                msglist.getNotice().setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("activeCount"))
                            {
                                msglist.getNotice().setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("chatCount"))
                            {
                                msglist.getNotice().setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("message") && msg != null) { 
				       		msglist.getMessagelist().add(msg); 
				       		msg = null; 
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
        return msglist;       
	}
}
