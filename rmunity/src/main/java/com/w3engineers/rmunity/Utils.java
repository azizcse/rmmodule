package com.w3engineers.rmunity;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by USER22 on 4/24/2018.
 */

public class Utils {
    public static String getDeviceIpAddress(boolean isLocalAddress) {
        String my_ip = null;
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        if (isLocalAddress) {
                            my_ip = inetAddress.getHostAddress();
                        }
                    } else {
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            my_ip = inetAddress.getHostAddress();
                        }
                    }
                }
            }
            return my_ip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
