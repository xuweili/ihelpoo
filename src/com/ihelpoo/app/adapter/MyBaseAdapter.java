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

import android.view.View;
import android.widget.BaseAdapter;

import com.ihelpoo.app.common.UIHelper;

public abstract class MyBaseAdapter extends BaseAdapter {
    protected View.OnClickListener imageClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            UIHelper.showImageDialog(v.getContext(), (String) v.getTag());
        }
    };
    //标识LinkView上的链接
	private boolean isLinkViewClick = false;

	public boolean isLinkViewClick() {
		return isLinkViewClick;
	}

	public void setLinkViewClick(boolean isLinkViewClick) {
		this.isLinkViewClick = isLinkViewClick;
	}

    protected String parseBody(String body){
        return body == null ? "" : body.replaceAll("\\\\","");
    }
}
