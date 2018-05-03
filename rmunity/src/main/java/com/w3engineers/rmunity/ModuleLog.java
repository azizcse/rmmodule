package com.w3engineers.rmunity;

/*
*  ****************************************************************************
*  * Created by : Md. Azizul Islam on 4/19/2018 at 12:34 PM.
*  * Email : azizul@w3engineers.com
*  * 
*  * Last edited by : Md. Azizul Islam on 4/19/2018.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/


import android.util.Log;

public class ModuleLog {
    private static String TAG = ModuleLog.class.getName();

    public static void d(String mag) {
        d(TAG, mag);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void e(String mag) {
        e(TAG, mag);
    }

    public static void e(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void i(String mag) {
        i(TAG, mag);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

}
