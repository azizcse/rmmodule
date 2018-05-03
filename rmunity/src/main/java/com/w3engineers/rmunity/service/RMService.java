package com.w3engineers.rmunity.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.w3engineers.rmunity.IRMAidlInterface;
import com.w3engineers.rmunity.R;
import com.w3engineers.rmunity.client.RmClient;
import com.w3engineers.rmunity.controller.RMController;
import com.w3engineers.rmunity.server.DataListener;
import com.w3engineers.rmunity.server.RmServer;

/**
 * Created by USER22 on 4/24/2018.
 */

public class RMService extends Service implements DataListener{
    public static final String START_FOREGROUND_ACTION = "com.w3engineers.startforeground";
    public static final String STOP_FOREGROUND_ACTION = "com.w3engineers.stopforeground";
    public static final int FOREGROUND_SERVICE_ID = 101;

    public static final String CHANNEL_NAME = "meshim";
    public static final String CHANNEL_ID = "notification_channel";
    private Notification mServiceNotification;
    private boolean mIsForeground = false;
    private int mVisibleActivities = 0;
    private boolean mIsBound = false;

    private RMController rmController;
    private RmServer mServer;
    private RmClient mClient;
    @Override
    public void onCreate() {
        super.onCreate();

        Intent stopForegroundIntent = new Intent(this, RMService.class);
        stopForegroundIntent.setAction(STOP_FOREGROUND_ACTION);
        PendingIntent pendingIntent  = PendingIntent.getService(this,0,stopForegroundIntent,0);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            //noinspection deprecation
            builder = new NotificationCompat.Builder(this);
        }
        Resources resources = getResources();
        mServiceNotification = builder.setAutoCancel(false)
                .setTicker("RMUnity")
                .setContentTitle("RM running")
                .setContentText("Tab to go offline")
                .setSmallIcon(R.drawable.rightmesh)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setNumber(100)
                .build();

        rmController = new RMController(this);
        rmController.connect(this);

        mServer = new RmServer(getApplicationContext(), this);
        mServer.runServer();
        mClient = new RmClient();
    }

    private IRMAidlInterface.Stub mBinder = new IRMAidlInterface.Stub() {
        @Override
        public void setForeground(boolean value) throws RemoteException {
            if (value) {
                startInForeground();
                mIsForeground = true;
            } else {
                stopForeground(true);
                mIsForeground = false;
            }
        }

        @Override
        public void sendData(){
            String msg = "From android :"+System.currentTimeMillis();
            mClient.sendMessage(msg.getBytes());
        }
    };

    /**
     * Disconnects from RightMesh when service is stopped.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        rmController.disconnect();
        mServer.stopServer();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mIsBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mIsBound = false;
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }
        if (action != null && action.equals(STOP_FOREGROUND_ACTION)) {
            if (mIsBound) {
                stopForeground(true);
            } else {
                stopSelf();
            }
        } else if (action != null && action.equals(START_FOREGROUND_ACTION)) {
            startInForeground();
        }
        return START_STICKY;
    }
    /**
     * creates a notification bar for foreground service and starts the service.
     */
    private void startInForeground() {
        startForeground(FOREGROUND_SERVICE_ID, mServiceNotification);
    }

    @Override
    public void onDataReceived(String id, String data) {
        Log.e("DataSend","Data send from service");
        rmController.sendData(id, data);
    }

    @Override
    public void onDataPrepare(String data) {
       rmController.sendToNetwork(data);
    }

    public void sendToUnity(byte[] data){
        Log.e("DataSend","Send data to Unity API");
        mClient.sendMessage(data);
    }
}
