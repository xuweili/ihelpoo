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
import com.ihelpoo.app.R;
import com.ihelpoo.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.util.Xml;

/**
 * 动态实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class Active extends Entity {

	public final static int CATALOG_OTHER = 0;//其他
	public final static int CATALOG_NEWS = 1;//新闻
	public final static int CATALOG_POST = 2;//帖子
	public final static int CATALOG_TWEET = 3;//动弹
	public final static int CATALOG_BLOG = 4;//博客
	
	public final static int CLIENT_MOBILE = 2;
	public final static int CLIENT_ANDROID = 3;
	public final static int CLIENT_IPHONE = 4;
	public final static int CLIENT_WINDOWS_PHONE = 5;
	
	private String face;
	private String message;
	private String author;
	private int authorId;
	private int activeType;
	private int objectId;
	private int objectType;
	private int objectCatalog;
    private String objectSayType;
	private String objectTitle;
	private ObjectReply objectReply;
	private int commentCount;
	private String pubDate;
	private String tweetimage;
    private String imgBig;
	private String appClient;
	private String url;
    private String academy;
    private String authorType;
    private String authorGossip;
    private int diffusionCount;
    private int online;
    private int rank;

    public String getObjectSayType() {
        return objectSayType;
    }

    public void setObjectSayType(String objectSayType) {
        this.objectSayType = objectSayType;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getDiffusionCount() {
        return diffusionCount;
    }

    public void setDiffusionCount(int diffusionCount) {
        this.diffusionCount = diffusionCount;
    }

    public String getAuthorGossip() {
        return authorGossip;
    }

    public void setAuthorGossip(String authorGossip) {
        this.authorGossip = authorGossip;
    }

    public String getAuthorType() {
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getEssage() {
        return message;
    }

    public void setEssage(String essage) {
        message = essage;
    }

    public static class ObjectReply implements Serializable{
		public String objectName;
		public String objectBody;
	} 	
	
	public void setObjectReply(ObjectReply objectReply) {
		this.objectReply = objectReply;
	}
	public ObjectReply getObjectReply() {
		return objectReply;
	}
	public String getTweetimage() {
		return tweetimage;
	}
	public void setTweetimage(String tweetimage) {
		this.tweetimage = tweetimage;
	}
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
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
	public int getActiveType() {
		return activeType;
	}
	public void setActiveType(int activeType) {
		this.activeType = activeType;
	}
	public int getObjectId() {
		return objectId;
	}
	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}	
	public int getObjectType() {
		return objectType;
	}
	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	public int getObjectCatalog() {
		return objectCatalog;
	}
	public void setObjectCatalog(int objectCatalog) {
		this.objectCatalog = objectCatalog;
	}
	public String getObjectTitle() {
		return objectTitle;
	}
	public void setObjectTitle(String objectTitle) {
		this.objectTitle = objectTitle;
	}	
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getAppClient() {
		return appClient;
	}
	public void setAppClient(String appClient) {
		this.appClient = appClient;
	}	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

    public String getImgBig() {
        return imgBig;
    }

    public void setImgBig(String imgBig) {
        this.imgBig = imgBig;
    }

    public static Active parse(InputStream inputStream) throws IOException, AppException {
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
			    		if(tag.equalsIgnoreCase("active"))
			    		{
			    			active = new Active();
			    		}			    		
			    		else if(active != null)
			    		{	
				            if(tag.equalsIgnoreCase("id"))
				            {			      
				            	active.id = StringUtils.toInt(xmlParser.nextText(), 0);
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
				            else if(tag.equalsIgnoreCase("objectreply"))
				            {			            	
				            	active.setObjectReply(new ObjectReply());	            	
				            }
                            else if(tag.equalsIgnoreCase("objectSayType"))
                            {
                                active.setObjectSayType(xmlParser.nextText());
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
				            else if(tag.equalsIgnoreCase("url"))
				            {			            	
				            	active.setUrl(xmlParser.nextText());			            	
				            }
                            else if(tag.equalsIgnoreCase("activeRank")){
                                active.setRank(StringUtils.toInt(xmlParser.nextText(),1));
                            }
				            //通知信息
				            else if(tag.equalsIgnoreCase("notice"))
				    		{
				            	active.setNotice(new Notice());
				    		}
				            else if(active.getNotice() != null)
				    		{
                                if(tag.equalsIgnoreCase("systemCount"))
                                {
                                    active.getNotice().setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
                                }
                                else if(tag.equalsIgnoreCase("atmeCount"))
                                {
                                    active.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("commentCount"))
                                {
                                    active.getNotice().setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("activeCount"))
                                {
                                    active.getNotice().setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("chatCount"))
                                {
                                    active.getNotice().setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
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
        return active;       
	}


    public SpannableString bold(String rank) {
        SpannableString spanString = new SpannableString(rank);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        return spanString;
    }

    public DrawableGradient drawBg(Context context, int rank) {
        return new DrawableGradient(new int[]{matchColor(context,rank), matchColor(context,rank), matchColor(context,rank)}, dp2px(context, 1L)).SetTransparency(10);
    }

    public float dp2px(Context context, float dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static class DrawableGradient extends GradientDrawable {
        DrawableGradient(int[] colors, float cornerRadius) {
            super(GradientDrawable.Orientation.TOP_BOTTOM, colors);

            try {
                this.setShape(GradientDrawable.RECTANGLE);
                this.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                this.setCornerRadius(cornerRadius);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public DrawableGradient SetTransparency(int transparencyPercent) {
            this.setAlpha(255 - ((255 * transparencyPercent) / 100));

            return this;
        }
    }

    public int matchColor(Context context, int rank) {
        switch (rank) {
            case 2:
                return context.getResources().getColor(R.color.bg_rank2);
            case 3:
                return context.getResources().getColor(R.color.bg_rank3);
            case 4:
                return context.getResources().getColor(R.color.bg_rank4);
            case 5:
                return context.getResources().getColor(R.color.bg_rank5);
            case 6:
                return context.getResources().getColor(R.color.bg_rank6);
            case 7:
                return context.getResources().getColor(R.color.bg_rank7);
            case 8:
                return context.getResources().getColor(R.color.bg_rank8);
            case 9:
                return context.getResources().getColor(R.color.bg_rank9);
            case 10:
                return context.getResources().getColor(R.color.bg_rank10);
            default:
                return context.getResources().getColor(R.color.bg_rank1);
        }
    }
}
