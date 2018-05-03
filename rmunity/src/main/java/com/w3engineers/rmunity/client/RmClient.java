package com.w3engineers.rmunity.client;

/*
*  ****************************************************************************
*  * Created by : Md. Azizul Islam on 4/19/2018 at 12:50 PM.
*  * Email : azizul@w3engineers.com
*  * 
*  * Last edited by : Md. Azizul Islam on 4/19/2018.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/


import com.w3engineers.rmunity.ModuleLog;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RmClient {
    private Socket socket;
    private final int PORT_NUMBER = 5002;
    private String localHostIp = "127.0.0.1";
    //private String localHostIp = "192.168.2.19";
    private ExecutorService singleThread;
    public RmClient(){
        singleThread = Executors.newSingleThreadExecutor();
    }

    /**
     *
     * @param msg(required) this is the message will be send to Unity
     * @param ipAddress (required)
     */
    public void sendMessage(final String msg, final String ipAddress) {
        singleThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    ModuleLog.d("Start sending message");
                    InetAddress addr = InetAddress.getByName(ipAddress);
                    SocketAddress sockaddr = new InetSocketAddress(addr, PORT_NUMBER);
                    socket.connect(sockaddr, 8000);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    dos.write(msg.getBytes());
                    //dos.writeUTF(msg);
                    dos.flush();
                    dos.close();
                    socket.close();
                    ModuleLog.d("Close sender socket");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
 public void sendMessage(final byte[] msg) {
        singleThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    ModuleLog.d("Start sending message");
                    InetAddress addr = InetAddress.getByName(localHostIp);
                    SocketAddress sockaddr = new InetSocketAddress(addr, PORT_NUMBER);
                    socket.connect(sockaddr, 8000);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    dos.write(msg);
                    //dos.writeUTF(msg);
                    dos.flush();
                    dos.close();
                    socket.close();
                    ModuleLog.d("Close sender socket");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
