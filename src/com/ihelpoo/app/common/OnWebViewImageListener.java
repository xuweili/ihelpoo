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

/**
 * 监听webview上的图片
 * 
 * @author yeguozhong@yeah.net
 *
 */
public interface OnWebViewImageListener {

	/**
	 * 点击webview上的图片，传入该缩略图的大图Url
	 * @param bigImageUrl
	 */
	void onImageClick(String bigImageUrl);
	
}
