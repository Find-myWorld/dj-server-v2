package com.example.iimp_znxj_new2014.activity;


import com.example.iimp_znxj_new2014.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private int recLen = 5;
    private TextView txtView = null;
    private Handler handler = null;
    private Intent intent;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtView = (TextView) findViewById(R.id.Main1_Text);

        new Thread(new MyThread()).start();         // start thread
        //	changePage();
        handler = new Handler() {          // handle
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        recLen--;
                        txtView.setText("" + recLen + "���������ϵͳ  �汾�ţ�2.0.0.2");
                        if (recLen == 0) changePage();
                }
                super.handleMessage(msg);
            }
        };

    }


    public class MyThread implements Runnable {      // thread
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);     // sleep 1000ms
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * ҳ����ת
     */
    public void changePage() {
        //	Toast.makeText(this,R.string.title_activity_amain_frm_welcome,Toast.LENGTH_LONG).show();
        Intent intent = new Intent();//�½�һ��activity
        intent.setClass(MainActivity.this, ChooseMenuActivity.class);//�ӱ����activity��ת��Ŀ��activity��
        startActivityForResult(intent, 0);//ִ��Ŀ��activity
//		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//		getWindow().setWindowAnimations(android.R.anim.slide_in_left);
        //String f="@android:integer/config_mediumAnimTime";
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        //overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        MainActivity.this.finish();
    }

}
