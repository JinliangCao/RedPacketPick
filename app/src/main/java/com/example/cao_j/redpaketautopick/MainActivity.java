package com.example.cao_j.redpaketautopick;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    RedPacketPickService mRedPacketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        bindService(new Intent(MainActivity.this, RedPacketPickService.class),
                new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {

                    }
                }, Context.BIND_AUTO_CREATE);
*/
    }
}
