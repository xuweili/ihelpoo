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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihelpoo.app.R;
import com.ihelpoo.app.bean.News;
import com.ihelpoo.app.common.StringUtils;

import java.util.List;

/**
 * 新闻资讯Adapter类
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewNoticeAdapter extends BaseAdapter {
    private Context context;//运行上下文
    private List<News> listItems;//数据集合
    private LayoutInflater listContainer;//视图容器
    private int itemViewResource;//自定义项视图源

    static class ListItemView {                //自定义控件集合
        public TextView title;
        public TextView author;
        public TextView date;
        public TextView count;
        public ImageView flag;
        public TextView inout;
    }

    /**
     * 实例化Adapter
     *
     * @param context
     * @param data
     * @param resource
     */
    public ListViewNoticeAdapter(Context context, List<News> data, int resource) {
        this.context = context;
        this.listContainer = LayoutInflater.from(context);    //创建视图容器并设置上下文
        this.itemViewResource = resource;
        this.listItems = data;
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
        //Log.d("method", "getView");

        //自定义视图
        ListItemView listItemView = null;

        if (convertView == null) {
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(this.itemViewResource, null);

            listItemView = new ListItemView();
            //获取控件对象
            listItemView.title = (TextView) convertView.findViewById(R.id.news_listitem_title);
            listItemView.author = (TextView) convertView.findViewById(R.id.news_listitem_author);
            listItemView.inout = (TextView) convertView.findViewById(R.id.news_listitem_inout);
            listItemView.count = (TextView) convertView.findViewById(R.id.news_listitem_commentCount);
            listItemView.date = (TextView) convertView.findViewById(R.id.news_listitem_date);
            listItemView.flag = (ImageView) convertView.findViewById(R.id.news_listitem_flag);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        //设置文字和图片
        News news = listItems.get(position);

        listItemView.title.setText(news.getTitle());
        listItemView.title.setTag(news);//设置隐藏参数(实体类)
        listItemView.author.setText(news.getAuthor());
        listItemView.date.setText(StringUtils.friendly_time(news.getPubDate()));
        listItemView.inout.setText(news.getInout());
        if (news.getInout().contains("-")) {
            listItemView.inout.setTextColor(context.getResources().getColor(R.color.orange));
        } else {
            listItemView.inout.setTextColor(context.getResources().getColor(R.color.green));
        }
//		listItemView.count.setText(news.getCommentCount()+"");
        if (isDelivered(news.getCommentCount()))
            listItemView.flag.setVisibility(View.GONE);
        else
            listItemView.flag.setVisibility(View.VISIBLE);

        return convertView;
    }

    private boolean isDelivered(int deliver) {
        return 1 == deliver;
    }
}