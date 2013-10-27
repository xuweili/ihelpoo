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
 * 动弹列表实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class TweetList extends Entity{
	
	public final static int CATALOG_STREAM = 0;
	public final static int CATALOG_HELP = -1;
    public final static int CATALOG_MINE = -2;

	private int pageSize;
	private int tweetCount;
	private List<Tweet> tweetlist = new ArrayList<Tweet>();
	
	public int getPageSize() {
		return pageSize;
	}
	public int getTweetCount() {
		return tweetCount;
	}
	public List<Tweet> getTweetlist() {
		return tweetlist;
	}

	public static TweetList parse(InputStream inputStream) throws IOException, AppException {
		TweetList tweetlist = new TweetList();
		Tweet tweet = null;
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
			    		if(tag.equalsIgnoreCase("tweetCount")) 
			    		{
			    			tweetlist.tweetCount = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if(tag.equalsIgnoreCase("pageSize")) 
			    		{
			    			tweetlist.pageSize = StringUtils.toInt(xmlParser.nextText(),0);
			    		}
			    		else if (tag.equalsIgnoreCase(Tweet.NODE_START)) 
			    		{ 
			    			tweet = new Tweet();
			    		}
			    		else if(tweet != null)
			    		{	
				            if(tag.equalsIgnoreCase(Tweet.NODE_ID))
				            {			      
				            	tweet.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(Tweet.NODE_FACE))
				            {			            	
				            	tweet.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(Tweet.NODE_BODY))
				            {			            	
				            	tweet.setBody(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Tweet.NODE_AUTHOR))
				            {			            	
				            	tweet.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(Tweet.NODE_AUTHORID))
				            {			            	
				            	tweet.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(Tweet.NODE_COMMENTCOUNT))
				            {			            	
				            	tweet.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(Tweet.NODE_PUBDATE))
				            {			            	
				            	tweet.setPubDate(xmlParser.nextText());	
				            }
				            else if(tag.equalsIgnoreCase(Tweet.NODE_IMGSMALL))
				            {			            	
				            	tweet.setImgSmall(xmlParser.nextText());			            	
				            }
				            else if(tag.equalsIgnoreCase(Tweet.NODE_IMGBIG))
				            {			            	
				            	tweet.setImgBig(xmlParser.nextText());			            	
				            }
				            else if(tag.equalsIgnoreCase(Tweet.NODE_APPCLIENT))
				            {			            	
				            	tweet.setAppClient(StringUtils.toInt(xmlParser.nextText(),0));				            	
				            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_BY))
                            {
                                tweet.setBy(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_academy))
                            {
                                tweet.setAcademy(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_authorGossip))
                            {
                                tweet.setAuthorGossip(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_authorType))
                            {
                                tweet.setAuthorType(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_onlineState))
                            {
                                tweet.setOnlineState(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_rank))
                            {
                                tweet.setRank(StringUtils.toInt(xmlParser.nextText(),1));
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_spreadCount))
                            {
                                tweet.setSpreadCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }

			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	tweetlist.setNotice(new Notice());
			    		}
			            else if(tweetlist.getNotice() != null)
			    		{
                            if(tag.equalsIgnoreCase("systemCount"))
                            {
                                tweetlist.getNotice().setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
                            }
                            else if(tag.equalsIgnoreCase("atmeCount"))
                            {
                                tweetlist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("commentCount"))
                            {
                                tweetlist.getNotice().setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("activeCount"))
                            {
                                tweetlist.getNotice().setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("chatCount"))
                            {
                                tweetlist.getNotice().setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("tweet") && tweet != null) { 
				       		tweetlist.getTweetlist().add(tweet); 
				       		tweet = null; 
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
        return tweetlist;       
	}
}
