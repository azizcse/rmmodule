package com.w3engineers.rmmodule;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.w3engineers.rmunity.IRMAidlInterface;
import com.w3engineers.rmunity.service.RMService;

public class MainActivity extends AppCompatActivity {
    IRMAidlInterface mBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = IRMAidlInterface.Stub.asInterface(service);
            try {
                mBinder.setForeground(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(mBinder != null){
                mBinder =null;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectToService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectFromService();
    }

    /**
     * Disconnect from service when activity stops.
     */
    @Override
    protected void onStop() {
        super.onStop();
        disconnectFromService();
    }

    public void sendData(View v){
        try {
            mBinder.sendData();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**

    /**
     * Handle creating the service intent and binding to it in a reusable function.
     */
    private void connectToService() {
        Intent serviceIntent = new Intent(this, RMService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        startService(serviceIntent);

    }

    private void disconnectFromService() {
        if (mBinder != null) {
            try {
                mBinder.setForeground(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(serviceConnection);
            mBinder = null;
        }
    }
}
