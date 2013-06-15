package com.ihelpoo.app.adapter;

import java.util.List;

import com.ihelpoo.app.bean.Active;
import com.ihelpoo.app.common.StringUtils;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.widget.LinkView;
import com.ihelpoo.app.R;
import com.ihelpoo.app.common.BitmapManager;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用户动态Adapter类
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewActiveAdapter extends MyBaseAdapter {
    private Context context;// 运行上下文
    private List<Active> listItems;// 数据集合
    private LayoutInflater listContainer;// 视图容器
    private int itemViewResource;// 自定义项视图源
    private BitmapManager bmpManager;
    private boolean faceClickEnable;

    private final static String AT_HOST_PRE = "http://my.oschina.net";
    private final static String MAIN_HOST = "http://www.oschina.net";

    static class ListItemView { // 自定义控件集合
        public ImageView userface;
        public TextView username;
        public TextView date;
        public LinkView content;
        public TextView reply;
        public TextView commentCount;
        public TextView client;
        public ImageView redirect;
        public ImageView image;

        public TextView type_gossip;
        public TextView rank;
        public TextView academy;
        public TextView online;
        public TextView diffusionCount;


    }

    /**
     * 实例化Adapter
     *
     * @param context
     * @param data
     * @param resource
     */
    public ListViewActiveAdapter(Context context, List<Active> data,
                                 int resource) {
        this(context, data, resource, true);
    }

    /**
     * 实例化Adapter
     *
     * @param context
     * @param data
     * @param resource
     */
    public ListViewActiveAdapter(Context context, List<Active> data,
                                 int resource, boolean faceClickEnable) {
        this.context = context;
        this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.itemViewResource = resource;
        this.listItems = data;
        this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.widget_dface_loading));
        this.faceClickEnable = faceClickEnable;
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
                    .findViewById(R.id.active_listitem_userface);
            listItemView.username = (TextView) convertView
                    .findViewById(R.id.active_listitem_username);
            listItemView.content = (LinkView) convertView
                    .findViewById(R.id.active_listitem_content);
            listItemView.date = (TextView) convertView
                    .findViewById(R.id.active_listitem_date);
            listItemView.commentCount = (TextView) convertView
                    .findViewById(R.id.active_listitem_commentCount);
            listItemView.client = (TextView) convertView
                    .findViewById(R.id.active_listitem_client);
            listItemView.reply = (TextView) convertView
                    .findViewById(R.id.active_listitem_reply);
            listItemView.redirect = (ImageView) convertView
                    .findViewById(R.id.active_listitem_redirect);
            listItemView.image = (ImageView) convertView
                    .findViewById(R.id.active_listitem_image);
            listItemView.type_gossip = (TextView) convertView.findViewById(R.id.active_listitem_type_gossip);
            listItemView.diffusionCount = (TextView) convertView.findViewById(R.id.active_listitem_diffusionCount);
            listItemView.online = (TextView)convertView.findViewById(R.id.active_listitem_online);
            listItemView.academy = (TextView) convertView.findViewById(R.id.active_listitem_academy);
            listItemView.rank = (TextView) convertView.findViewById(R.id.active_listitem_rank);

            // 设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        // 设置文字和图片
        Active active = listItems.get(position);
//		listItemView.username.setText(UIHelper.parseActiveAction(
//                active.getAuthor(), active.getObjectType(),
//                active.getObjectCatalog(), active.getObjectTitle()));
        listItemView.username.setText(active.getAuthor());
        listItemView.username.setTag(active);// 设置隐藏参数(实体类)

        listItemView.type_gossip.setText(active.getAuthorType()+" "+active.getAuthorGossip());

//        SpannableString spanString = new SpannableString(String.valueOf(active.getRank()));
//        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        listItemView.rank.setText(active.getRank());
        listItemView.academy.setText(active.getAcademy());
        listItemView.online.setText(1==active.getOnline()?"在线":"");
//        if(1!=active.getOnline())listItemView.online.setTextColor(Color.LTGRAY);
        listItemView.diffusionCount.setText(active.getDiffusionCount() + "");


        // 把相对路径改成绝对路径
        String message = modifyPath(active.getMessage());

        listItemView.content.setLinkText(message);
        listItemView.content.setTag(active);// 设置隐藏参数(实体类)
        listItemView.content.setOnClickListener(linkViewClickListener);
        listItemView.content.setLinkClickListener(linkClickListener);

        listItemView.date
                .setText(StringUtils.friendly_time(active.getPubDate()));
        listItemView.commentCount.setText(active.getCommentCount() + "");

        switch (active.getAppClient()) {
            default:
                listItemView.client.setText("");
                break;
            case Active.CLIENT_MOBILE:
                listItemView.client.setText("来自:手机");
                break;
            case Active.CLIENT_ANDROID:
                listItemView.client.setText("来自:Android");
                break;
            case Active.CLIENT_IPHONE:
                listItemView.client.setText("来自:iPhone");
                break;
            case Active.CLIENT_WINDOWS_PHONE:
                listItemView.client.setText("来自:Windows Phone");
                break;
        }
        if (StringUtils.isEmpty(listItemView.client.getText().toString()))
            listItemView.client.setVisibility(View.GONE);
        else
            listItemView.client.setVisibility(View.VISIBLE);

        Active.ObjectReply reply = active.getObjectReply();
        if (reply != null) {
            listItemView.reply.setText(UIHelper.parseActiveReply(
                    reply.objectName, reply.objectBody));
            listItemView.reply.setVisibility(TextView.VISIBLE);
        } else {
            listItemView.reply.setText("");
            listItemView.reply.setVisibility(TextView.GONE);
        }

        if (active.getActiveType() == Active.CATALOG_OTHER)
            listItemView.redirect.setVisibility(ImageView.GONE);
        else
            listItemView.redirect.setVisibility(ImageView.VISIBLE);

        String faceURL = active.getFace();
        if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)) {
            listItemView.userface.setImageResource(R.drawable.widget_dface);
        } else {
            bmpManager.loadBitmap(faceURL, listItemView.userface);
        }
        if (faceClickEnable) {
            listItemView.userface.setOnClickListener(faceClickListener);
        }
        listItemView.userface.setTag(active);

        String imgSmall = active.getTweetimage();
        if (!StringUtils.isEmpty(imgSmall)) {
            bmpManager.loadBitmap(imgSmall, listItemView.image, BitmapFactory
                    .decodeResource(context.getResources(),
                            R.drawable.image_loading));
            listItemView.image.setVisibility(ImageView.VISIBLE);
        } else {
            listItemView.image.setVisibility(ImageView.GONE);
        }

        return convertView;
    }

    private View.OnClickListener faceClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Active active = (Active) v.getTag();
            UIHelper.showUserCenter(v.getContext(), active.getAuthorId(),
                    active.getAuthor());
        }
    };

    /**
     * 修正一些路径
     *
     * @param message
     * @return
     */
    private String modifyPath(String message) {
//		message = message.replaceAll("(<a[^>]+href=\")/([\\S]+)\"", "$1"
//				+ AT_HOST_PRE + "/$2\"");
//		message = message.replaceAll("(<a[^>]+href=\")http://m.oschina.net([\\S]+)\"", "$1"+MAIN_HOST+"$2\"");
        return message;
    }

    private View.OnClickListener linkViewClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(!isLinkViewClick()){
                UIHelper.showActiveRedirect(v.getContext(), (Active)v.getTag());
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