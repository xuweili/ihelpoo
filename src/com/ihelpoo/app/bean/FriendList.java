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
 * 好友列表实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class FriendList extends Entity{

	public final static int TYPE_FOLLOWER = 0x00;
	public final static int TYPE_FRIEND = 0x01;
	
	private List<Friend> friendlist = new ArrayList<Friend>();


	public List<Friend> getFriendlist() {
		return friendlist;
	}
	public void setFriendlist(List<Friend> resultlist) {
		this.friendlist = resultlist;
	}
	
	public static FriendList parse(InputStream inputStream) throws IOException, AppException {
		FriendList friendlist = new FriendList();
		Friend friend = null;
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
			    		if (tag.equalsIgnoreCase("friend")) 
			    		{ 
			    			friend = new Friend();
			    		}
			    		else if(friend != null)
			    		{	
				            if(tag.equalsIgnoreCase("uid"))
				            {			      
				            	friend.uid = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase("nickname"))
				            {			            	
				            	friend.nickname= xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("avatar_url"))
				            {			            	
				            	friend.avatar_url= xmlParser.nextText();
				            }
				            else if(tag.equalsIgnoreCase("online_status"))
				            {			            	
				            	friend.online_status = xmlParser.nextText();
				            }
                            else if(tag.equalsIgnoreCase("create_time"))
                            {
                                friend.create_time = xmlParser.nextText();
                            }
				            else if(tag.equalsIgnoreCase("gender"))
				            {			            	
				            	friend.gender = StringUtils.toInt(xmlParser.nextText(),0);
				            }
			    		}
			            //通知信息
			            else if(tag.equalsIgnoreCase("notice"))
			    		{
			            	friendlist.setNotice(new Notice());
			    		}
			            else if(friendlist.getNotice() != null)
			    		{
                            if(tag.equalsIgnoreCase("systemCount"))
                            {
                                friendlist.getNotice().setSystemCount(StringUtils.toInt(xmlParser.nextText(), 0));
                            }
                            else if(tag.equalsIgnoreCase("atmeCount"))
                            {
                                friendlist.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("commentCount"))
                            {
                                friendlist.getNotice().setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("activeCount"))
                            {
                                friendlist.getNotice().setActiveCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
                            else if(tag.equalsIgnoreCase("chatCount"))
                            {
                                friendlist.getNotice().setChatCount(StringUtils.toInt(xmlParser.nextText(),0));
                            }
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:	
					   	//如果遇到标签结束，则把对象添加进集合中
				       	if (tag.equalsIgnoreCase("friend") && friend != null) { 
				       		friendlist.getFriendlist().add(friend); 
				       		friend = null; 
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
        return friendlist;       
	}



    /**
     * 好友实体类
     */
    public static class Friend implements Serializable {


        private int uid;
        private String nickname;
        private String email;
        private int email_verified;
        private int gender;
        private String birthday;
        private String enrol_time;
        private int user_type;
        private String login_time;
        private int last_login;
        private String create_time;
        private int login_days;
        private String online_status;
        private int active_credits;
        private String avatar_type;
        private String avatar_url;
        private String web_theme;
        private String self_intro;
        private String real_name;
        private int followers_count;
        private int friends_count;
        private String school_id;
        private String school_name;
        private String academy_name;
        private String major_name;
        private String school_domain;
        private String dorm_name;
        private int level;
        private int relation;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getEmail_verified() {
            return email_verified;
        }

        public void setEmail_verified(int email_verified) {
            this.email_verified = email_verified;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getEnrol_time() {
            return enrol_time;
        }

        public void setEnrol_time(String enrol_time) {
            this.enrol_time = enrol_time;
        }

        public int getUser_type() {
            return user_type;
        }

        public void setUser_type(int user_type) {
            this.user_type = user_type;
        }

        public String getLogin_time() {
            return login_time;
        }

        public void setLogin_time(String login_time) {
            this.login_time = login_time;
        }

        public int getLast_login() {
            return last_login;
        }

        public void setLast_login(int last_login) {
            this.last_login = last_login;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getLogin_days() {
            return login_days;
        }

        public void setLogin_days(int login_days) {
            this.login_days = login_days;
        }

        public String getOnline_status() {
            return online_status;
        }

        public void setOnline_status(String online_status) {
            this.online_status = online_status;
        }

        public int getActive_credits() {
            return active_credits;
        }

        public void setActive_credits(int active_credits) {
            this.active_credits = active_credits;
        }

        public String getAvatar_type() {
            return avatar_type;
        }

        public void setAvatar_type(String avatar_type) {
            this.avatar_type = avatar_type;
        }

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public String getWeb_theme() {
            return web_theme;
        }

        public void setWeb_theme(String web_theme) {
            this.web_theme = web_theme;
        }

        public String getSelf_intro() {
            return self_intro;
        }

        public void setSelf_intro(String self_intro) {
            this.self_intro = self_intro;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public int getFollowers_count() {
            return followers_count;
        }

        public void setFollowers_count(int followers_count) {
            this.followers_count = followers_count;
        }

        public int getFriends_count() {
            return friends_count;
        }

        public void setFriends_count(int friends_count) {
            this.friends_count = friends_count;
        }

        public String getSchool_id() {
            return school_id;
        }

        public void setSchool_id(String school_id) {
            this.school_id = school_id;
        }

        public String getSchool_name() {
            return school_name;
        }

        public void setSchool_name(String school_name) {
            this.school_name = school_name;
        }

        public String getAcademy_name() {
            return academy_name;
        }

        public void setAcademy_name(String academy_name) {
            this.academy_name = academy_name;
        }

        public String getMajor_name() {
            return major_name;
        }

        public void setMajor_name(String major_name) {
            this.major_name = major_name;
        }

        public String getSchool_domain() {
            return school_domain;
        }

        public void setSchool_domain(String school_domain) {
            this.school_domain = school_domain;
        }

        public String getDorm_name() {
            return dorm_name;
        }

        public void setDorm_name(String dorm_name) {
            this.dorm_name = dorm_name;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getRelation() {
            return relation;
        }

        public void setRelation(int relation) {
            this.relation = relation;
        }
    }
}
