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

package com.ihelpoo.app.adapter;

import java.util.List;

import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.bean.Messages;
import com.ihelpoo.app.common.BitmapManager;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.widget.LinkView;
import com.ihelpoo.app.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用户留言Adapter类
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewMessageAdapter extends MyBaseAdapter {
	private Context context;// 运行上下文
	private List<Messages> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private BitmapManager bmpManager;

	static class ListItemView { // 自定义控件集合
		public ImageView userface;
		public LinkView username;
		public TextView date;
		public TextView messageCount;
		public TextView client;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewMessageAdapter(Context context, List<Messages> data,
			int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.widget_dface_loading));
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// Log.d("method", "getView");

		// 自定义视图
		ListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.userface = (ImageView) convertView
					.findViewById(R.id.message_listitem_userface);
			listItemView.username = (LinkView) convertView
					.findViewById(R.id.message_listitem_username);
			listItemView.date = (TextView) convertView
					.findViewById(R.id.message_listitem_date);
			listItemView.messageCount = (TextView) convertView
					.findViewById(R.id.message_listitem_messageCount);
			listItemView.client = (TextView) convertView
					.findViewById(R.id.message_listitem_client);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
		Messages msg = listItems.get(position);
		AppContext ac = (AppContext) context.getApplicationContext();
		if (msg.getSenderId() == ac.getLoginUid()) {
			UIHelper.parseMessageSpan(listItemView.username,
                    msg.getFriendName(), msg.getContent(), "发给 ");
		} else {
			UIHelper.parseMessageSpan(listItemView.username, msg.getSender(),
					msg.getContent(), "");
		}
		
		listItemView.username.setTag(msg);// 设置隐藏参数(实体类)
		listItemView.username.setOnClickListener(linkViewClickListener);
		listItemView.username.setLinkClickListener(linkClickListener);
		
		listItemView.date.setText(StringUtils.friendly_time(msg.getPubDate()));
		listItemView.messageCount.setText("共有 " + msg.getMessageCount()
				+ " 条留言");

		switch (msg.getAppClient()) {
		default:
			listItemView.client.setText("");
			break;
		case Messages.CLIENT_MOBILE:
			listItemView.client.setText("来自:手机");
			break;
		case Messages.CLIENT_ANDROID:
			listItemView.client.setText("来自:Android");
			break;
		case Messages.CLIENT_IPHONE:
			listItemView.client.setText("来自:iPhone");
			break;
		case Messages.CLIENT_WINDOWS_PHONE:
			listItemView.client.setText("来自:Windows Phone");
			break;
		}
		if (StringUtils.isEmpty(listItemView.client.getText().toString()))
			listItemView.client.setVisibility(View.GONE);
		else
			listItemView.client.setVisibility(View.VISIBLE);

		String faceURL = msg.getFace();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)) {
			listItemView.userface.setImageResource(R.drawable.widget_dface);
		} else {
			bmpManager.loadBitmap(faceURL, listItemView.userface);
		}
		listItemView.userface.setOnClickListener(faceClickListener);
		listItemView.userface.setTag(msg);

		return convertView;
	}

	private View.OnClickListener faceClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			Messages msg = (Messages) v.getTag();
			UIHelper.showUserCenter(v.getContext(), msg.getFriendId(),
					msg.getFriendName());
		}
	};
	
	private View.OnClickListener linkViewClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if(!isLinkViewClick()){
				Messages msg = (Messages)v.getTag();
				UIHelper.showMessageDetail(v.getContext(),
						msg.getFriendId(), msg.getFriendName());
			}
			setLinkViewClick(false);
		}
	};
	
	private LinkView.OnLinkClickListener linkClickListener = new LinkView.OnLinkClickListener() {
		public void onLinkClick() {
			setLinkViewClick(true);
		}
	};
}