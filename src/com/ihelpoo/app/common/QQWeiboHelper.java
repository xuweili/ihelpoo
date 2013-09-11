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

package com.ihelpoo.app.common;

import java.net.URLEncoder;

import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * 腾讯微博帮助类
 * @author yeguozhong@yeah.net
 * @version 2.0
 * @created 2013-4-25
 */
public class QQWeiboHelper {
	
	private static final String Share_URL = "http://share.v.t.qq.com/index.php?c=share&a=index";
	private static final String Share_Source = "OSChina";
	private static final String Share_Site = "OSChina.net";
	private static final String Share_AppKey = "96f54f97c4de46e393c4835a266207f4";
	

	/**
	 * 分享到腾讯微博
	 * @param activity
	 * @param title
	 * @param url
	 */
	public static void shareToQQ(Activity activity,String title,String url){
		String URL = Share_URL;
		try {
			URL += "&title=" + URLEncoder.encode(title, HTTP.UTF_8) + "&url=" + URLEncoder.encode(url, HTTP.UTF_8) + "&appkey=" + Share_AppKey + "&source=" + Share_Source + "&site=" + Share_Site;	
		} catch (Exception e) {
			e.printStackTrace();
		}
		Uri uri = Uri.parse(URL);
		activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));	
	}
}