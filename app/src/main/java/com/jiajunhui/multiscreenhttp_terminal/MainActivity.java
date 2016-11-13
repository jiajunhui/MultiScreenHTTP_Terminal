package com.jiajunhui.multiscreenhttp_terminal;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xapp.jjh.xui.activity.TopBarActivity;
import com.xapp.jjh.xui.inter.MenuType;
import com.xapp.jjh.xui.inter.PageState;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.net.InetAddress;

public class MainActivity extends TopBarActivity {

    private TextView tv_server_info;
    private EditText mEtContent;
    private Button mBtnSend;

    private String HOST;

    private boolean isConnected;

    private final int MSG_RECEIVE_UDP = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_RECEIVE_UDP:
                    setPageState(PageState.SUCCESS);
                    ReceiveUDP.stopListener();
                    showSnackBar("搜到新设备",null,null);
                    tv_server_info.setText("已连接:" + msg.obj.toString());
                    isConnected = true;
                    break;
            }
        }
    };

    @Override
    public View getContentView(LayoutInflater layoutInflater, ViewGroup container) {
        return inflate(R.layout.activity_main);
    }

    @Override
    public void findViewById() {
        tv_server_info = findView(R.id.tv_server_info);
        mEtContent = findView(R.id.et_contet);
        mBtnSend = findView(R.id.btn_send);
    }

    @Override
    public void initData() {
        setMenuType(MenuType.TEXT,R.string.menu_text);
        setTopBarTitle("TestServer");
        setSwipeBackEnable(false);
        setNavigationVisible(false);

    }

    @Override
    public void setListener() {
        super.setListener();
        mBtnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_send:
                if(isConnected){
                    sendInfo();
                }else{
                    showSnackBar("未连接设备",null,null);
                }
                break;
        }
    }

    private void sendInfo() {
        String content = mEtContent.getText().toString();
        if(TextUtils.isEmpty(content)){
            showSnackBar("请输入内容",null,null);
            return;
        }
        OkHttpUtils
                .get()
                .url(HOST + "/input")
                .addParams("content",content)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {
                        showToast("SUCCESS");
                    }
                });
    }

    @Override
    public void onMenuClick() {
        super.onMenuClick();
        setPageState(PageState.LOADING);
        startUDPTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiveUDP.stopListener();
    }

    private void startUDPTimer() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                ReceiveUDP.listenUDP(new ReceiveUDP.OnUDPListener() {
                    @Override
                    public void onReceive(String content, InetAddress inetAddress) {
                        HOST = "http://" + inetAddress.getHostAddress() + ":8080";
                        Message message = Message.obtain();
                        message.what = MSG_RECEIVE_UDP;
                        message.obj = content;
                        mHandler.sendMessage(message);
                    }
                });
            }
        }.start();
    }
}
