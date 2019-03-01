package com.wzk.daka;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

public class MainActivity extends AppCompatActivity implements OnDateSetListener {
    AppCompatButton mManualBtn;
    AppCompatButton mAutoBtn;
    AppCompatButton mCancelBtn;
    boolean mAuto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mManualBtn = findViewById(R.id.manualBtn);
        mManualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuto = false;
                showTimePicker();
            }
        });

        mAutoBtn = findViewById(R.id.autoBtn);
        mAutoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuto = true;
                showTimePicker();
            }
        });

        mCancelBtn = findViewById(R.id.cancelBtn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarmService();
            }
        });
    }

    private void showTimePicker() {
        long oneYears = 1L * 365 * 1000 * 60 * 60 * 24L;
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("Cancel")
                .setSureStringId("Sure")
                .setTitleStringId("打卡时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(true)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + oneYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.colorPrimaryDark))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorAccent))
                .setWheelItemTextSize(13)
                .build();
        mDialogAll.show(getSupportFragmentManager(), null);
    }

    private void startAlarmService(long millSeconds) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, AlarmService.class));
        intent.putExtra("time", millSeconds);
        intent.putExtra(AlarmService.AUTO, mAuto);
        startForegroundService(intent);
    }

    private void cancelAlarmService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, AlarmService.class));
        intent.setAction(AlarmService.ACTION_CANCEL_ALARM);
        startForegroundService(intent);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        startAlarmService(millseconds);
    }
}
