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

package com.ihelpoo.app.widget;

import com.ihelpoo.app.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 新数据Toast提示控件(带音乐播放)
 * @version 1.0
 * @created 2012-8-30
 */
public class NewDataToast extends Toast{
	
	private MediaPlayer mPlayer;
	private boolean isSound;
	
	public NewDataToast(Context context) {
		this(context, false);
	}
	
	public NewDataToast(Context context, boolean isSound) {
		super(context);
		
		this.isSound = isSound;

        mPlayer = MediaPlayer.create(context, R.raw.newdatatoast);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}        	
        });

    }

	@Override
	public void show() {
		super.show();
		
		if(isSound){
			mPlayer.start();
		}
	}
	
	/**
	 * 设置是否播放声音
	 */
	public void setIsSound(boolean isSound) {
		this.isSound = isSound;
	}
	
	/**
	 * 获取控件实例
	 * @param context
	 * @param text 提示消息
	 * @param isSound 是否播放声音
	 * @return
	 */
	public static NewDataToast makeText(Context context, CharSequence text, boolean isSound) {
		NewDataToast result = new NewDataToast(context, isSound);
		
        LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        
        View v = inflate.inflate(R.layout.new_data_toast, null);
        v.setMinimumWidth(dm.widthPixels);//设置控件最小宽度为手机屏幕宽度
        Drawable background = context.getResources().getDrawable(R.drawable.new_data_toast);
        background.setAlpha(50);
        TextView tv = (TextView)v.findViewById(R.id.new_data_toast_message);
        tv.setText(text);
        
        result.setView(v);
        result.setDuration(600);
        result.setGravity(Gravity.TOP, 0, (int)(dm.density*75));

        return result;
    }
	
}
