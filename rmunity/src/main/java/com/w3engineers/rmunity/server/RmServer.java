package com.w3engineers.rmunity.server;

/*
*  ****************************************************************************
*  * Created by : Md. Azizul Islam on 4/19/2018 at 11:47 AM.
*  * Email : azizul@w3engineers.com
*  * 
*  * Last edited by : Md. Azizul Islam on 4/19/2018.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/


import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.widget.Toast;

import com.w3engineers.rmunity.App;
import com.w3engineers.rmunity.ModuleLog;
import com.w3engineers.rmunity.parser.JsonKeys;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RmServer implements Runnable {
    private final int PORT_NUMBER = 5001;
    private Thread mThread;
    private boolean isRunning;
    private ServerSocket serverSocket;
    private Handler handler;
    private Context context;
    private DataListener dataListener;
    public RmServer(Context context, DataListener dataListener) {
        this.context = context;
        try {
            this.dataListener = dataListener;
            handler = new Handler(Looper.getMainLooper());
            mThread = new Thread(this,"rmthread");
            mThread.setDaemon(true);
            serverSocket = new ServerSocket(PORT_NUMBER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean runServer() {
        if (isRunning) return false;
        isRunning = true;
        mThread.start();
        ModuleLog.d("Server started");
        return true;
    }

    public boolean stopServer() {
        if (!isRunning) return false;
        isRunning = false;
        mThread.interrupt();
        ModuleLog.d("Server stopped");
        return true;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                ModuleLog.d("Server is running");
                Socket socket = serverSocket.accept();
                DataInputStream DIS = new DataInputStream(socket.getInputStream());
                byte[] recvBuf = IOUtils.toByteArray(DIS);
                String receivedData = new String(recvBuf);
                ModuleLog.d("Received message ="+getLocalAddress());
                processMessage(receivedData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(final String msg){
        try {
            JSONObject jsonObject = new JSONObject(msg);

            String receiverId = jsonObject.getString(JsonKeys.ID_KEY);
            dataListener.onDataReceived(receiverId, msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getLocalAddress() throws IOException {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
}
