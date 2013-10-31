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

import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.widget.LinkView;
import com.ihelpoo.app.R;
import com.ihelpoo.app.bean.Tweet;
import com.ihelpoo.app.common.BitmapManager;
import com.ihelpoo.app.common.UIHelper;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 动弹Adapter类
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewTweetAdapter extends MyBaseAdapter {
    private Context context;//运行上下文
    private List<Tweet> listItems;//数据集合
    private LayoutInflater listContainer;//视图容器
    private int itemViewResource;//自定义项视图源
    private BitmapManager bmpManager;

    static class ListItemView {                //自定义控件集合
        public ImageView userface;
        public TextView username;
        public TextView date;
        public LinkView content;
        public TextView plusCount;
        public TextView diffusionCount;
        public TextView commentCount;
        public TextView helpCount;
        public ImageView commentIcon;
        public ImageView helpIcon;



        public TextView by;
        public ImageView image;

        public TextView type_gossip;
        public TextView rank;
        public TextView academy;
        public TextView online;
    }

    /**
     * 实例化Adapter
     *
     * @param context
     * @param data
     * @param resource
     */
    public ListViewTweetAdapter(Context context, List<Tweet> data, int resource) {
        this.context = context;
        this.listContainer = LayoutInflater.from(context);    //创建视图容器并设置上下文
        this.itemViewResource = resource;
        this.listItems = data;
        this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
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
            listItemView.userface = (ImageView) convertView.findViewById(R.id.tweet_listitem_userface);
            listItemView.username = (TextView) convertView.findViewById(R.id.tweet_listitem_username);
            listItemView.content = (LinkView) convertView.findViewById(R.id.tweet_listitem_content);
            listItemView.image = (ImageView) convertView.findViewById(R.id.tweet_listitem_image);
            listItemView.date = (TextView) convertView.findViewById(R.id.tweet_listitem_date);
            listItemView.plusCount = (TextView) convertView.findViewById(R.id.tweet_listitem_plus_count);
            listItemView.diffusionCount = (TextView) convertView.findViewById(R.id.tweet_listitem_diffusion_count);

            listItemView.commentIcon = (ImageView) convertView.findViewById(R.id.tweet_listitem_comment_icon);
            listItemView.commentCount = (TextView) convertView.findViewById(R.id.tweet_listitem_comment_count);
            listItemView.helpIcon = (ImageView) convertView.findViewById(R.id.tweet_listitem_help_icon);
            listItemView.helpCount = (TextView) convertView.findViewById(R.id.tweet_listitem_help_count);


            listItemView.by = (TextView) convertView.findViewById(R.id.tweet_listitem_by);

            listItemView.type_gossip = (TextView) convertView.findViewById(R.id.tweet_listitem_type_gossip);
            listItemView.online = (TextView) convertView.findViewById(R.id.tweet_listitem_online);
            listItemView.academy = (TextView) convertView.findViewById(R.id.tweet_listitem_academy);
            listItemView.rank = (TextView) convertView.findViewById(R.id.tweet_listitem_rank);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        //设置文字和图片
        Tweet tweet = listItems.get(position);

        listItemView.rank.setText(tweet.bold(String.valueOf(tweet.getRank())));
        listItemView.rank.setBackgroundDrawable(tweet.drawBg(context, tweet.getRank()));

        listItemView.type_gossip.setText(tweet.getAuthorType() + " " + tweet.getAuthorGossip());
        listItemView.academy.setText(tweet.getAcademy());
        listItemView.online.setText(tweet.getOnlineState());
        listItemView.diffusionCount.setText(tweet.getSpreadCount() + "");
        listItemView.plusCount.setText(tweet.getPlusCount() + "");

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)listItemView.diffusionCount.getLayoutParams();
        if("1".equals(tweet.getSayType())){
            listItemView.helpIcon.setVisibility(View.VISIBLE);
            listItemView.helpCount.setVisibility(View.VISIBLE);
            listItemView.commentIcon.setVisibility(View.GONE);
            listItemView.commentCount.setVisibility(View.GONE);
            params.addRule(RelativeLayout.LEFT_OF, R.id.tweet_listitem_help_icon);
        } else {
            listItemView.helpIcon.setVisibility(View.GONE);
            listItemView.helpCount.setVisibility(View.GONE);
            listItemView.commentIcon.setVisibility(View.VISIBLE);
            listItemView.commentCount.setVisibility(View.VISIBLE);
            listItemView.commentCount.setText(tweet.getCommentCount() + "");
            params.addRule(RelativeLayout.LEFT_OF, R.id.tweet_listitem_comment_icon);
        }


        listItemView.username.setText(tweet.getAuthor());
        listItemView.username.setTag(tweet);//设置隐藏参数(实体类)

//		listItemView.content.setText(tweet.getBody());
//		listItemView.content.parseLinkText();
        listItemView.content.setLinkText(parseBody(tweet.getBody()));
        listItemView.content.setTag(tweet);//设置隐藏参数(实体类)
        listItemView.content.setOnClickListener(linkViewClickListener);
        listItemView.content.setLinkClickListener(linkClickListener);

        listItemView.date.setText(StringUtils.friendly_time(tweet.getPubDate()));
        listItemView.by.setText(tweet.getBy());

        String faceURL = tweet.getFace();
        if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)) {
            listItemView.userface.setImageResource(R.drawable.widget_dface);
        } else {
            bmpManager.loadBitmap(faceURL, listItemView.userface);
        }
        listItemView.userface.setOnClickListener(faceClickListener);
        listItemView.userface.setTag(tweet);

        String imgSmall = tweet.getImgSmall();
        if (!StringUtils.isEmpty(imgSmall)) {
            bmpManager.loadBitmap(imgSmall, listItemView.image, BitmapFactory.decodeResource(context.getResources(), R.drawable.image_loading));
            listItemView.image.setOnClickListener(imageClickListener);
            listItemView.image.setTag(tweet.getImgBig());
            listItemView.image.setVisibility(ImageView.VISIBLE);
        } else {
            listItemView.image.setVisibility(ImageView.GONE);
        }

        return convertView;
    }

    private View.OnClickListener faceClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Tweet tweet = (Tweet) v.getTag();
            UIHelper.showUserCenter(v.getContext(), tweet.getAuthorId(), tweet.getAuthor());
        }
    };

    private View.OnClickListener imageClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            UIHelper.showImageDialog(v.getContext(), (String) v.getTag());
        }
    };

    private View.OnClickListener linkViewClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!isLinkViewClick()) {
                UIHelper.showTweetDetail(v.getContext(), ((Tweet) v.getTag()).getId(), false);
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