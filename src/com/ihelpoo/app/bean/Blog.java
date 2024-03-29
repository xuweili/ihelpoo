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

import com.ihelpoo.app.AppException;
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 博客实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class Blog extends Entity {

	public final static int DOC_TYPE_REPASTE = 0;//转帖
	public final static int DOC_TYPE_ORIGINAL = 1;//原创
	
	private String title;
	private String where;
	private String body;
	private String author;
	private int authorId;
	private int documentType;
	private String pubDate;
	private int favorite;
	private int commentCount;
	private String url;
	
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getFavorite() {
		return favorite;
	}
	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}
	public String getPubDate() {
		return pubDate;
	}	
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public int getDocumentType() {
		return documentType;
	}
	public void setDocumentType(int documentType) {
		this.documentType = documentType;
	}
	
	public static Blog parse(InputStream inputStream) throws IOException, AppException {
		Blog blog = null;
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
			    		if(tag.equalsIgnoreCase("blog"))
			    		{
			    			blog = new Blog();
			    		}  
			    		else if(blog != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	blog.id = StringUtils.toInt(xmlParser.nextText(), 0);
				            }
				            else if(tag.equalsIgnoreCase("title"))
				            {			            	
				            	blog.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("where"))
				            {			            	
				            	blog.setWhere(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("body"))
				            {			            	
				            	blog.setBody(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase("author"))
				            {			            	
				            	blog.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase("authorid"))
				            {			            	
				            	blog.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase("documentType"))
				            {			            	
				            	blog.setDocumentType(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase("pubDate"))
				            {			            	
				            	blog.setPubDate(xmlParser.nextText());            	
				            }
				            else if(tag.equalsIgnoreCase("favorite"))
				            {			            	
				            	blog.setFavorite(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase("commentCount"))
				            {			            	
				            	blog.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	blog.setUrl(xmlParser.nextText());
				            }
				            //通知信息
				            else if(tag.equalsIgnoreCase("notice"))
				    		{
				            	blog.setNotice(new Notice());
				    		}
				            else if(blog.getNotice() != null)
				    		{
                                if(tag.equalsIgnoreCase("systemCount"))
                                {
                                    blog.getNotice().setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
                                }
                                else if(tag.equalsIgnoreCase("atmeCount"))
                                {
                                    blog.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("commentCount"))
                                {
                                    blog.getNotice().setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("activeCount"))
                                {
                                    blog.getNotice().setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("chatCount"))
                                {
                                    blog.getNotice().setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
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
        return blog;       
	}
}
