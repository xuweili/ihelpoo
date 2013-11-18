package com.ihelpoo.app.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.ihelpoo.app.adapter.GridViewFaceAdapter;

public class ExpressionUtil {
	/**
     * 表情图片匹配
     */
	private static Pattern facePattern = Pattern
            .compile("\\[{1}(\\w*)]{1}");
            
    /**
     * 将[12]之类的字符串替换为表情
     *
     * @param context
     * @param content
     */
	
    public static SpannableStringBuilder parseFaceByText(Context context,String content) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Matcher matcher = facePattern.matcher(content);
        while (matcher.find()) {
            // 使用正则表达式找出其中的数字
            int position = StringUtils.toInt(GridViewFaceAdapter.getPosition(matcher.group(1)));
            int resId = 0;
            try {
                /*if (position > 65 && position < 102)
                    position = position - 1;
                else if (position > 102)
                    position = position - 2;*/
                resId = GridViewFaceAdapter.getImageIds()[position];
                Drawable d = context.getResources().getDrawable(resId);
                d.setBounds(0, 0, 35, 35);// 设置表情图片的显示大小
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                builder.setSpan(span, matcher.start(), matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
            }
        }
        return builder;
    }

}
