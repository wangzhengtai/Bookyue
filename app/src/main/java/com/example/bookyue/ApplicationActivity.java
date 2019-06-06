package com.example.bookyue;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.bookyue.util.NetworkUtil;

@SuppressLint("Registered")
public class ApplicationActivity extends AppCompatActivity {

    private NetworkChangeReceiver mNetworkChangeReceiver;
    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        mView = getWindow().getDecorView().getRootView();

        mNetworkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkChangeReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkChangeReceiver);
    }

    protected void setSnackView(View view){
        mView = view;
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //网络不可用
            if (!NetworkUtil.isConnected(context)){
                Snackbar.make(mView, R.string.network_cannot_connect,Snackbar.LENGTH_LONG)
                        .setAction(R.string.setting, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //跳转到设置界面
                                Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white))
                        .show();
            }
        }
    }
}
