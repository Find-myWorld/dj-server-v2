package com.example.iimp_znxj_new2014.activity;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.service.UdpService;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends BaseActivity implements OnClickListener {
	private Button buttonOK;
	private Button buttonBack;
	private EditText editServer;
	private EditText editRoom;
	private EditText rollcalltime;
	private String cellNumber;
	private String duration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		initView();

	}

	public void initView() {
		buttonOK = (Button) findViewById(R.id.buttonOK);
		buttonBack = (Button) findViewById(R.id.buttonBack);
		editServer = (EditText) findViewById(R.id.editServer);
		editRoom = (EditText) findViewById(R.id.editRoom);
		rollcalltime = (EditText) findViewById(R.id.rollcalltime);

		if (checkIsFirstUse()) {
			editServer.setText("192.168.1.253:19008");
			editRoom.setText("0101");
			rollcalltime.setText("1");
		} else {
			String serverIp = PreferenceUtil.getSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_SERVERIP);
			String port = PreferenceUtil.getSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_PORT);
			String cellNumber = PreferenceUtil.getSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_CELLNUMBER);
			String duration = PreferenceUtil.getSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_DURATION);
			editServer.setText(serverIp + ":" + port);
			editRoom.setText(cellNumber);
			rollcalltime.setText(duration);
		}

		buttonOK.setOnClickListener(this);
		buttonBack.setOnClickListener(this);
	}

	private String path;
	private String serverIp;
	private String port;

	public void setData() {
		// 192.168.0.26:89
		path = editServer.getText().toString();
		serverIp = path.split(":")[0];
		port = path.split(":")[1];

		cellNumber = editRoom.getText().toString();
		duration = rollcalltime.getText().toString();

		PreferenceUtil.putSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_SERVERIP, serverIp);
		PreferenceUtil.putSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_PORT, port);
		PreferenceUtil.putSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_CELLNUMBER, cellNumber);
		PreferenceUtil.putSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_DURATION, duration);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 4) {
			gotoIndex();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonOK:
			setData();
			gotoIndex();
			break;
		case R.id.buttonBack:
			gotoIndex();
			break;
		}
	}

	public void gotoIndex() {
		this.finish();
		Intent intent = new Intent();
		intent.setClass(this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	public boolean checkIsFirstUse() {
		if (PreferenceUtil.getSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_SERVERIP).equals("")) {
			return true;
		} else {
			return false;
		}
	}
}