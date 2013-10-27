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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
 * 动弹实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class Tweet extends Entity{

	public final static String NODE_ID = "id";
	public final static String NODE_FACE = "portrait";
	public final static String NODE_BODY = "body";
	public final static String NODE_AUTHORID = "authorid";
	public final static String NODE_AUTHOR = "author";
	public final static String NODE_PUBDATE = "pubDate";
	public final static String NODE_COMMENTCOUNT = "commentCount";
	public final static String NODE_IMGSMALL = "imgSmall";
	public final static String NODE_IMGBIG = "imgBig";
	public final static String NODE_APPCLIENT = "appclient";
	public final static String NODE_START = "tweet";
    public final static String NODE_BY = "by";
    public final static String NODE_SAY_TYPE = "sayType";


    public final static String NODE_spreadCount = "spreadCount";
    public final static String NODE_plusCount = "plusCount";

    public final static String NODE_academy = "academy";
    public final static String NODE_authorType = "authorType";
    public final static String NODE_authorGossip = "authorGossip";
    public final static String NODE_onlineState = "onlineState";
    public final static String NODE_rank = "rank";
    public final static String NODE_plusByMe = "plusByMe";
    public final static String NODE_diffuseByMe = "diffuseByMe";

	
	public final static int CLIENT_MOBILE = 2;
	public final static int CLIENT_ANDROID = 3;
	public final static int CLIENT_IPHONE = 4;
	public final static int CLIENT_WINDOWS_PHONE = 5;
	
	private String face;
	private String body;
	private String author;
	private int authorId;
	private int commentCount;
	private String pubDate;
	private String imgSmall;
	private String imgBig;
	private File imageFile;
	private int appClient;
    private String by;
    private String sayType;

    private int spreadCount;
    private int plusCount;
    private String academy;
    private String authorType;
    private String authorGossip;
    private String onlineState;
    private int rank;
    private int reward;
    private int plusByMe;
    private int diffuseByMe;

    public String getSayType() {
        return sayType;
    }

    public void setSayType(String sayType) {
        this.sayType = sayType;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public int getPlusCount() {
        return plusCount;
    }

    public void setPlusCount(int plusCount) {
        this.plusCount = plusCount;
    }

    public int getPlusByMe() {
        return plusByMe;
    }

    public void setPlusByMe(int plusByMe) {
        this.plusByMe = plusByMe;
    }

    public int getDiffuseByMe() {
        return diffuseByMe;
    }

    public void setDiffuseByMe(int diffuseByMe) {
        this.diffuseByMe = diffuseByMe;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(String onlineState) {
        this.onlineState = onlineState;
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

    public int getSpreadCount() {
        return spreadCount;
    }

    public void setSpreadCount(int spreadCount) {
        this.spreadCount = spreadCount;
    }

    public int getAppClient() {
		return appClient;
	}
	public void setAppClient(int appClient) {
		this.appClient = appClient;
	}
	public File getImageFile() {
		return imageFile;
	}
	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}	
	public String getImgSmall() {
		return imgSmall;
	}
	public void setImgSmall(String imgSmall) {
		this.imgSmall = imgSmall;
	}
	public String getImgBig() {
		return imgBig;
	}
	public void setImgBig(String imgBig) {
		this.imgBig = imgBig;
	}
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
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
	
	public static Tweet parse(InputStream inputStream) throws IOException, AppException {
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
			    		if(tag.equalsIgnoreCase(NODE_START))
			    		{
			    			tweet = new Tweet();
			    		}
			    		else if(tweet != null)
			    		{	
				            if(tag.equalsIgnoreCase(NODE_ID))
				            {			      
				            	tweet.id = StringUtils.toInt(xmlParser.nextText(), 0);
				            }
				            else if(tag.equalsIgnoreCase(NODE_FACE))
				            {			            	
				            	tweet.setFace(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_BODY))
				            {			            	
				            	tweet.setBody(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHOR))
				            {			            	
				            	tweet.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHORID))
				            {			            	
				            	tweet.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_COMMENTCOUNT))
				            {			            	
				            	tweet.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_PUBDATE))
				            {			            	
				            	tweet.setPubDate(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_IMGSMALL))
				            {			            	
				            	tweet.setImgSmall(xmlParser.nextText());			            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_IMGBIG))
				            {			            	
				            	tweet.setImgBig(xmlParser.nextText());			            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_APPCLIENT))
				            {			            	
				            	tweet.setAppClient(StringUtils.toInt(xmlParser.nextText(),0));			            	
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
                            else if(tag.equalsIgnoreCase(Tweet.NODE_plusByMe))
                            {
                                tweet.setPlusByMe(StringUtils.toInt(xmlParser.nextText(),1));
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_diffuseByMe))
                            {
                                tweet.setPlusByMe(StringUtils.toInt(xmlParser.nextText(), 1));
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_spreadCount))
                            {
                                tweet.setSpreadCount(StringUtils.toInt(xmlParser.nextText(), 0));
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_plusCount))
                            {
                                tweet.setPlusCount(StringUtils.toInt(xmlParser.nextText(), 0));
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_BY))
                            {
                                tweet.setBy(xmlParser.nextText());
                            }
                            else if(tag.equalsIgnoreCase(Tweet.NODE_SAY_TYPE))
                            {
                                tweet.setSayType(xmlParser.nextText());
                            }
				            //通知信息
				            else if(tag.equalsIgnoreCase("notice"))
				    		{
				            	tweet.setNotice(new Notice());
				    		}
				            else if(tweet.getNotice() != null)
				    		{
                                if(tag.equalsIgnoreCase("systemCount"))
                                {
                                    tweet.getNotice().setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
                                }
                                else if(tag.equalsIgnoreCase("atmeCount"))
                                {
                                    tweet.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("commentCount"))
                                {
                                    tweet.getNotice().setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("activeCount"))
                                {
                                    tweet.getNotice().setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
                                }
                                else if(tag.equalsIgnoreCase("chatCount"))
                                {
                                    tweet.getNotice().setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
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
        return tweet;       
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
