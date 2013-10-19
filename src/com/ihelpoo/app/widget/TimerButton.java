package com.ihelpoo.app.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import com.ihelpoo.app.R;

/**
 * @author YINZHIPING(yzp531@163.com)
 * @version 1.0, @d2013-4-5 下午10:43:37
 * @(#)TimerButton.java
 * @Copyright 2012 YINZP
 */
public class TimerButton extends Button {

    private int notFocusedTextColor, focusedTextColor, pressedTextColor;
    public static String target = "{1}";
    public static String tempTarget = "{1}";
    private boolean isTextPressed;
    private int time = 30;
    private Handler ajaxHandler;
    private final Handler mainH = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.e("msg.what", msg.what + "");
            if (msg.what > 0) {
                target = replaceStr(tempTarget, time + "S", "(\\d{1,2})");
                sendEmptyMessageDelayed(time--, 1000);
                setText(target + "");
                Log.e("time", time + "");
            } else if (msg.what == 0) {
                if (null != getAjaxHandler())
                    getAjaxHandler().sendEmptyMessage(time);
            }

        }

        ;
    };

    public TimerButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public TimerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TimerButton(Context context) {
        super(context);

    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.TimeBtn);
        String textColorNotFocused = a
                .getString(R.styleable.TimeBtn_textColorNotFocused);
        String textColorFocused = a
                .getString(R.styleable.TimeBtn_textColorFocused);
        String textColorPressed = a
                .getString(R.styleable.TimeBtn_textColorPressed);
        time = a.getInteger(R.styleable.TimeBtn_time, 20);
        if (textColorNotFocused != null && textColorFocused != null
                && textColorPressed != null) {
            notFocusedTextColor = a.getColor(
                    R.styleable.TimeBtn_textColorNotFocused, 0xFF000000);
            focusedTextColor = a.getColor(R.styleable.TimeBtn_textColorFocused,
                    0xFF000000);
            pressedTextColor = a.getColor(R.styleable.TimeBtn_textColorPressed,
                    0xFF000000);
        } else {

        }
        target = getText().toString();
        tempTarget = target;
    }

    public void onDrawBackground(Canvas canvas) {
        // Override this method & do nothing. This prevents the
        // parent.onDrawBackground(canvas)
        // from drawing the button's background.
    }

    /**
     * Capture mouse press events to update text state.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TextOnlyButton", event.getAction() + "");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isTextPressed = true;

            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isTextPressed = false;

            requestFocus();

            invalidate();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (isTextPressed) {
            setTextColor(pressedTextColor);
        } else if (isFocused()) {

            setTextColor(focusedTextColor);
        } else {
            setTextColor(notFocusedTextColor);
        }
        super.onDraw(canvas);
    }

    public static String replaceStr(String target, String reString, String regex) {
        if (null == reString)
            return target;
        reString = reString.trim();
        if ("".equals(reString))
            return target;
        String[] strs = reString.split(",");
        int len = strs.length;

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(target);

        int i = 0;
        while (matcher.find()) {
            if (i < len)
                target = target.replace(matcher.group(0), strs[i++]);

        }
        return target;
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
        mainH.sendEmptyMessage(time);
    }

    /**
     * @return the ajaxHandler
     */
    public Handler getAjaxHandler() {
        return ajaxHandler;
    }

    /**
     * @param ajaxHandler the ajaxHandler to set
     */
    public void setAjaxHandler(Handler ajaxHandler) {
        this.ajaxHandler = ajaxHandler;
    }

    /**
     * @return the mainH
     */
    public Handler getMainH() {
        return this.mainH;
    }
}
