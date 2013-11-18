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

import com.ihelpoo.app.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.Arrays;

/**
 * 用户表情Adapter类
 *
 * @version 1.0
 * @created 2012-8-9
 */
public class GridViewFaceAdapter extends BaseAdapter {
    // 定义Context
    private Context mContext;
    // 定义整型数组 即图片源
    private static int[] mImageIds = new int[]{
            R.drawable.f001, R.drawable.f002, R.drawable.f003, R.drawable.f004, R.drawable.f005,//微笑 撇嘴 色   发呆 得意
            R.drawable.f006, R.drawable.f007, R.drawable.f008, R.drawable.f009, R.drawable.f010,//大哭 害羞 闭嘴 睡   流泪
            R.drawable.f011, R.drawable.f012, R.drawable.f013, R.drawable.f014, R.drawable.f015,//尴尬 发怒 调皮 呲牙 惊讶
            R.drawable.f016, R.drawable.f017, R.drawable.f018, R.drawable.f019, R.drawable.f020,//难过 酷   冷汗 抓狂 吐
            R.drawable.f021, R.drawable.f022, R.drawable.f023, R.drawable.f024, R.drawable.f025,//偷笑 可爱 白眼 傲慢 饥饿
            R.drawable.f026, R.drawable.f027, R.drawable.f028, R.drawable.f029, R.drawable.f030,//困   惊恐 流汗 憨笑 大兵
            R.drawable.f031, R.drawable.f032, R.drawable.f033, R.drawable.f034, R.drawable.f035,//奋斗 咒骂 疑问 嘘   晕
            R.drawable.f036, R.drawable.f037, R.drawable.f038, R.drawable.f039, R.drawable.f040,//折磨 衰   骷髅 敲打 再见
            R.drawable.f041, R.drawable.f042, R.drawable.f043, R.drawable.f044, R.drawable.f045,//擦汗 抠鼻 鼓掌 糗大了 坏笑
            R.drawable.f046, R.drawable.f047, R.drawable.f048, R.drawable.f049, R.drawable.f050,//左哼哼 右哼哼 哈欠 鄙视 委屈
            R.drawable.f051, R.drawable.f052, R.drawable.f053, R.drawable.f054, R.drawable.f055,//快哭了 阴险 亲亲 吓 可怜
            R.drawable.f056, R.drawable.f057, R.drawable.f058, R.drawable.f059, R.drawable.f060,//菜刀 西瓜 啤酒 篮球 乒乓
            R.drawable.f061, R.drawable.f062, R.drawable.f063, R.drawable.f064, R.drawable.f065,//咖啡 饭 猪头 玫瑰 凋谢
            R.drawable.f067, R.drawable.f068, R.drawable.f069, R.drawable.f070, R.drawable.f071,//爱心 心碎 蛋糕 闪电 炸弹
            R.drawable.f072, R.drawable.f073, R.drawable.f074, R.drawable.f075, R.drawable.f076,//刀   足球 瓢虫 便便 月亮
            R.drawable.f077, R.drawable.f078, R.drawable.f079, R.drawable.f080, R.drawable.f081,//太阳 礼物 拥抱 强   弱
            R.drawable.f082, R.drawable.f083, R.drawable.f084, R.drawable.f085, R.drawable.f086,//握手 胜利 抱拳 勾引 拳头
            R.drawable.f087, R.drawable.f088, R.drawable.f089, R.drawable.f090, R.drawable.f091,//差劲 爱你 NO   OK   爱情
            R.drawable.f092, R.drawable.f093, R.drawable.f094, R.drawable.f095, R.drawable.f096,//飞吻 跳跳 发抖 怄火 转圈
            R.drawable.f097, R.drawable.f098, R.drawable.f099, R.drawable.f100, R.drawable.f101,//磕头 回头 跳绳 挥手 激动
            R.drawable.f103, R.drawable.f104, R.drawable.f105//献吻 左太极 右太极
    };
    private static String[] emotionsText = new String[]{
            "微笑", "撇嘴", "色", "发呆", "得意",
            "大哭", "害羞", "闭嘴", "睡", "流泪",
            "尴尬", "发怒", "调皮", "呲牙", "惊讶",
            "难过", "酷", "冷汗", "抓狂", "吐",
            "偷笑", "可爱", "白眼", "傲慢", "饥饿",
            "困", "惊恐", "流汗", "憨笑", "大兵",
            "奋斗", "咒骂", "疑问", "嘘", "晕",
            "折磨", "衰", "骷髅", "敲打", "再见",
            "擦汗", "抠鼻", "鼓掌", "糗大了", "坏笑",
            "左哼哼", "右哼哼", "哈欠", "鄙视", "委屈",
            "快哭了", "阴险", "亲亲", "吓", "可怜",
            "菜刀", "西瓜", "啤酒", "篮球", "乒乓",
            "咖啡", "饭", "猪头", "玫瑰", "凋谢",
            "爱心", "心碎", "蛋糕", "闪电", "炸弹",
            "刀", "足球", "瓢虫", "便便", "月亮",
            "太阳", "礼物", "拥抱", "强", "弱",
            "握手", "胜利", "抱拳", "勾引", "拳头",
            "差劲", "爱你", "NO", "OK", "爱情",
            "飞吻", "跳跳", "发抖", "怄火", "转圈",
            "磕头", "回头", "跳绳", "挥手", "激动",
            "献吻", "左太极", "右太极"
    };

    public static int[] getImageIds() {
        return mImageIds;
    }

    public GridViewFaceAdapter(Context c) {
        mContext = c;
    }

    // 获取图片的个数
    public int getCount() {
        return mImageIds.length;
    }

    // 获取图片在库中的位置
    public Object getItem(int position) {
        return position;
    }


    // 获取图片ID
    public long getItemId(int position) {
        return mImageIds[position];
    }

    public static int getPosition(String emotionText){
        return Arrays.asList(emotionsText).indexOf(emotionText);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            // 设置图片n×n显示
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            // 设置显示比例类型
            imageView.setScaleType(ImageView.ScaleType.CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mImageIds[position]);
        imageView.setTag("[" + emotionsText[position] + "]");

        return imageView;
    }
}