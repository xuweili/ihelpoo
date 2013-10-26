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

import com.ihelpoo.app.AppException;
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 通知信息实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class Notice implements Serializable {
	
	public final static String UTF8 = "UTF-8";

    public final static int TYPE_SYSTEM = 0;
	public final static int	TYPE_ATME = 1;
    public final static int	TYPE_COMMENT = 2;
	public final static int TYPE_ACTIVE = 3;
    public final static int TYPE_CHAT = 4;


    private int systemCount;
    private int atmeCount;
    private int commentCount;
    private int activeCount;
    private int chatCount;
	
	public static Notice parse(InputStream inputStream) throws IOException, AppException {
		Notice notice = null;
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
			            //通知信息
			            if(tag.equalsIgnoreCase("notice"))
			    		{
			            	notice = new Notice();
			    		}
			            else if(notice != null)
			    		{
			    			if(tag.equalsIgnoreCase("systemCount"))
				            {			      
			    				notice.setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
				            }
				            else if(tag.equalsIgnoreCase("atmeCount"))
				            {			            	
				            	notice.setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("commentCount"))
				            {			            	
				            	notice.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
				            else if(tag.equalsIgnoreCase("activeCount"))
				            {			            	
				            	notice.setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
				            }
                            else if(tag.equalsIgnoreCase("chatCount"))
                            {
                                notice.setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:		    		
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
        return notice;       
	}

    public int getSystemCount() {
        return systemCount;
    }

    public void setSystemCount(int systemCount) {
        this.systemCount = systemCount;
    }

    public int getAtmeCount() {
        return atmeCount;
    }

    public void setAtmeCount(int atmeCount) {
        this.atmeCount = atmeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }
}
