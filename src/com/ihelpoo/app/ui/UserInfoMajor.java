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

package com.ihelpoo.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ihelpoo.app.AppContext;
import com.ihelpoo.app.AppException;
import com.ihelpoo.app.R;
import com.ihelpoo.app.bean.AcademyInfo;
import com.ihelpoo.app.bean.AcademyList;
import com.ihelpoo.app.bean.DormInfo;
import com.ihelpoo.app.bean.DormList;
import com.ihelpoo.app.bean.MajorInfo;
import com.ihelpoo.app.bean.MajorList;
import com.ihelpoo.app.bean.MyInformation;
import com.ihelpoo.app.bean.Result;
import com.ihelpoo.app.bean.SchoolInfo;
import com.ihelpoo.app.bean.SchoolList;
import com.ihelpoo.app.common.UIHelper;
import com.ihelpoo.app.widget.LoadingDialog;

import java.util.Collections;
import java.util.List;

/**
 * 用户资料
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class UserInfoMajor extends BaseActivity {

    private Button back;
    private ImageView refresh;

    private LoadingDialog loading;
    private MyInformation user;
    private Handler mHandler;

    private Spinner spSchool;
    private Spinner spAcademy;
    private Spinner spMajor;
    private Spinner spDorm;

    ArrayAdapter<SchoolInfo> schoolAdapter;
    ArrayAdapter<AcademyInfo> academyAdapter;
    ArrayAdapter<MajorInfo> majorAdapter;
    ArrayAdapter<DormInfo> dormAdapter;

    List<SchoolInfo> schoolInfoList;
    List<AcademyInfo> academyInfoList;
    List<MajorInfo> majorInfoList;
    List<DormInfo> dormInfoList;

    private ProgressDialog mProgress;

    private int schoolFlag;
    private int academyFlag;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_major);

        //初始化视图控件
        this.initView();
        //初始化视图数据
        this.initData();
    }

    private void initView() {
        back = (Button) findViewById(R.id.user_nickname_back);
        refresh = (ImageView) findViewById(R.id.user_nickname_refresh);
        back.setOnClickListener(UIHelper.finish(this));
        refresh.setOnClickListener(refreshClickListener);

        spSchool = (Spinner) findViewById(R.id.sp_user_major_school);
        spAcademy = (Spinner) findViewById(R.id.sp_user_major_academy);
        spMajor = (Spinner) findViewById(R.id.sp_user_major_major);
        spDorm = (Spinner) findViewById(R.id.sp_user_major_dorm);

        spSchool.setOnItemSelectedListener(onSelectSchool);
        spAcademy.setOnItemSelectedListener(onSelectAcademy);

    }

    public static class Major {
        public SchoolList schoolList;
        public AcademyList academyList;
        public MajorList majorList;
        public DormList dormList;
    }


    private void initData() {

        final AppContext ac = (AppContext) getApplication();
        mProgress = ProgressDialog.show(this, "", "正在初始化数据···");
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {

                if (mProgress != null) mProgress.dismiss();

                if (msg.what == 1 && msg.obj != null) {
                    Major major = (Major) msg.obj;

                    schoolInfoList = major.schoolList == null ? Collections.<SchoolInfo>emptyList() : major.schoolList.getSchoolList();
                    academyInfoList = major.academyList == null ? Collections.<AcademyInfo>emptyList() : major.academyList.getAcademyList();
                    majorInfoList = major.majorList == null ? Collections.<MajorInfo>emptyList() : major.majorList.getMajorList();
                    dormInfoList = major.dormList == null ? Collections.<DormInfo>emptyList() : major.dormList.getDormList();

                    schoolAdapter = new ArrayAdapter<SchoolInfo>(getApplicationContext(), android.R.layout.simple_spinner_item, schoolInfoList);
                    spSchool.setAdapter(schoolAdapter);

                    academyAdapter = new ArrayAdapter<AcademyInfo>(getApplicationContext(), android.R.layout.simple_spinner_item, academyInfoList);
                    spAcademy.setAdapter(academyAdapter);

                    majorAdapter = new ArrayAdapter<MajorInfo>(getApplicationContext(), android.R.layout.simple_spinner_item, majorInfoList);
                    spMajor.setAdapter(majorAdapter);

                    dormAdapter = new ArrayAdapter<DormInfo>(getApplicationContext(), android.R.layout.simple_spinner_item, dormInfoList);
                    spDorm.setAdapter(dormAdapter);
                } else {
                    ((AppException) msg.obj).makeToast(UserInfoMajor.this);
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    Major major = new Major();
                    major.schoolList = ac.getSchoolList();
                    if (major.schoolList.getSchoolList().size() > 0) {
                        int schoolId = major.schoolList.getSchoolList().get(0).getId();
                        major.academyList = ac.getAcademyList(schoolId);
                        if (major.academyList.getAcademyList().size() > 0) {
                            major.majorList = ac.getMajorList(schoolId, major.academyList.getAcademyList().get(0).getId());
                        }
                        major.dormList = ac.getDormList(schoolId);
                    }

                    msg.what = 1;
                    msg.obj = major;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();

    }

    private AdapterView.OnItemSelectedListener onSelectSchool = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//            if(!enrol.getText().toString().equals(spEnrol.getSelectedItem().toString())){
//                updateEnrol();
//            }
            schoolFlag += 1;
            if (schoolFlag <= 1) return;

            mProgress = ProgressDialog.show(UserInfoMajor.this, "", "正在获取数据···");
            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {

                    if (mProgress != null) mProgress.dismiss();

                    if (msg.what == 1 && msg.obj != null) {
                        Major major = (Major) msg.obj;
                        academyInfoList.clear();
                        majorInfoList.clear();
                        dormInfoList.clear();
                        if (major.academyList != null) {
                            academyInfoList.addAll(major.academyList.getAcademyList());
                        }
                        if (major.majorList != null) {
                            majorInfoList.addAll(major.majorList.getMajorList());
                        }
                        if (major.dormList != null) {
                            dormInfoList.addAll(major.dormList.getDormList());
                        }
                        academyAdapter.notifyDataSetChanged();
                        majorAdapter.notifyDataSetChanged();
                        dormAdapter.notifyDataSetChanged();
                    } else {
                        ((AppException) msg.obj).makeToast(UserInfoMajor.this);
                    }
                }
            };
            new Thread() {
                public void run() {
                    Message msg = new Message();
                    try {
                        Major major = new Major();
                        SchoolInfo schoolInfo = (SchoolInfo) spSchool.getSelectedItem();
                        final AppContext ac = (AppContext) getApplication();
                        major.academyList = ac.getAcademyList(schoolInfo.getId());
                        if (major.academyList.getAcademyList().size() > 0) {
                            major.majorList = ac.getMajorList(schoolInfo.getId(), major.academyList.getAcademyList().get(0).getId());
                        }
                        major.dormList = ac.getDormList(schoolInfo.getId());

                        msg.what = 1;
                        msg.obj = major;
                    } catch (AppException e) {
                        e.printStackTrace();
                        msg.what = -1;
                        msg.obj = e;
                    }
                    handler.sendMessage(msg);
                }
            }.start();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }
    };


    private AdapterView.OnItemSelectedListener onSelectAcademy = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//            if(!enrol.getText().toString().equals(spEnrol.getSelectedItem().toString())){
//                updateEnrol();
//            }
            academyFlag += 1;

            if (academyFlag <= 1) return;
            mProgress = ProgressDialog.show(UserInfoMajor.this, "", "正在获取数据···");
            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {

                    if (mProgress != null) mProgress.dismiss();

                    if (msg.what == 1 && msg.obj != null) {
                        Major major = (Major) msg.obj;
                        majorInfoList.clear();
                        if (major.majorList != null) {
                            majorInfoList.addAll(major.majorList.getMajorList());
                        }
                        majorAdapter.notifyDataSetChanged();
                    } else {
                        ((AppException) msg.obj).makeToast(UserInfoMajor.this);
                    }
                }
            };
            new Thread() {
                public void run() {
                    Message msg = new Message();
                    try {
                        Major major = new Major();
                        AcademyInfo academyInfo = (AcademyInfo) spAcademy.getSelectedItem();
                        SchoolInfo schoolInfo = (SchoolInfo) spSchool.getSelectedItem();
                        final AppContext ac = (AppContext) getApplication();
                        major.majorList = ac.getMajorList(schoolInfo.getId(), academyInfo.getId());

                        msg.what = 1;
                        msg.obj = major;
                    } catch (AppException e) {
                        e.printStackTrace();
                        msg.what = -1;
                        msg.obj = e;
                    }
                    handler.sendMessage(msg);
                }
            }.start();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }
    };

    private void updateGender(final String gender) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (loading != null) loading.dismiss();
                if (msg.what == 1 && msg.obj != null) {
                    Result res = (Result) msg.obj;
                    //提示信息
                    UIHelper.ToastMessage(UserInfoMajor.this, res.getErrorMessage());
                    if (res.OK()) {
                        finishEdit(gender);
                    }
                } else if (msg.what == -1 && msg.obj != null) {
                    ((AppException) msg.obj).makeToast(UserInfoMajor.this);
                }
            }

        };

        if (loading != null) {
            loading.setLoadText("正在修改性别···");
            loading.show();
        }

        new Thread() {
            public void run() {
                AppContext appContext = (AppContext) getApplication();
                Message msg = new Message();
                try {
                    Result res = appContext.updateGender(appContext.getLoginUid(), gender);
                    msg.what = 1;
                    msg.obj = res;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();

    }


    private void finishEdit(final String gender) {
        Intent intent = new Intent(this, UserInfoEdit.class);
        setResult(UserInfoEdit.REQUEST_CODE_EDIT_GENDER, intent);
        intent.putExtra("gender", gender);
        finish();
    }

    private View.OnClickListener refreshClickListener = new View.OnClickListener() {
        public void onClick(View v) {
        }
    };
}
