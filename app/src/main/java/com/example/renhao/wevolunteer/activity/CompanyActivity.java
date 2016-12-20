package com.example.renhao.wevolunteer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.core.AppActionImpl;
import com.example.model.ActionCallbackListener;
import com.example.model.volunteer.VolunteerViewDto;
import com.example.renhao.wevolunteer.R;
import com.example.renhao.wevolunteer.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 现工作单位界面
 */
public class CompanyActivity extends BaseActivity {
    private static final String TAG = "ResidenceActivity";

    private VolunteerViewDto personal_data;
    private String nowcompany;

    private TextView tv_submit;
    private EditText edit_nowcompany;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        Intent intent = getIntent();
        personal_data = (VolunteerViewDto) intent.getSerializableExtra("personal_data");
        edit_nowcompany = (EditText) findViewById(R.id.edit_nowcompany);
        if (personal_data.getWorkunit() != null) {
            nowcompany = personal_data.getWorkunit();
            edit_nowcompany.setText(nowcompany);
        }

        tv_submit = (TextView) findViewById(R.id.tv_nowcompany_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog("正在提交");
                nowcompany = edit_nowcompany.getText().toString();
                List<VolunteerViewDto> vl_updates = new ArrayList<>();
                personal_data.setWorkunit(nowcompany);
                vl_updates.add(personal_data);
                AppActionImpl.getInstance(getApplicationContext()).volunteerUpdate(vl_updates, new ActionCallbackListener<String>() {
                    @Override
                    public void onSuccess(String data) {
                        Intent result = new Intent();
                        result.putExtra("personal_data", personal_data);
                        setResult(RESULT_OK, result);
                        showToast("修改成功");
                        dissMissNormalDialog();
                        CompanyActivity.this.finish();
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        showToast("提交失败，请稍后重试");
                        dissMissNormalDialog();
                    }
                });
            }
        });

        //回退按钮
        ImageView btn_back = (ImageView) findViewById(R.id.imageView_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanyActivity.this.finish();
            }
        });

    }


}
