package com.desay.openmobile;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by uidq0655 on 2017/12/5.
 */

public class Tmc200 {
    private final static String TAG = "TMC200_JAVA";

    public native boolean open();

    public native boolean close();

    public native byte[] transmit(byte[] command);

    public native byte[] reset();

    public native byte[] getATR();

    private static boolean bReaderClosed, bSessionClosed, bChannelClosed;
    private static boolean bIsBasicChannel;

    private static byte[] bSelectChannel = {0x0, 0x70, 0x0, 0x0, 0x1};

    static {
        System.loadLibrary("tmc200");   //defaultConfig.ndk.moduleName
        bReaderClosed = true;
        bSessionClosed = true;
        bChannelClosed = true;
        bIsBasicChannel = true;
    }

    public boolean openReader() {
        if (bReaderClosed == false) {
            Log.e(TAG, "reader is already open\n");
            return false;
        }
        boolean status = open();
        if (status == false) {
            return false;
        }
        byte[] response = reset();
        if (response == null) {
            return false;
        }
        bReaderClosed = false;
        return true;
    }

    public void closeReader()
    {
        close();
        bReaderClosed = true;
    }

    public byte[] getAtr() {
        if (bSessionClosed == false) {
            Log.e(TAG, "session is already open\n");
        }
        byte[] response = getATR();
        if (response != null) {
            bSessionClosed = false;
            return response;
        }
        return null;
    }

    public boolean isSessionClosed()
    {
        return bSessionClosed;
    }

    public void closeChannels()
    {
        bSessionClosed = true;
    }

    public boolean isChannelClosed()
    {
        return bChannelClosed;
    }

    public boolean closeChannel()
    {
        bChannelClosed = true;
        return true;
    }

    public boolean openBasicChannel(byte[] var1, byte var2)
    {
        if (bChannelClosed == false) {
            Log.e(TAG, "channel is already open\n");
            return false;
        }
        bChannelClosed = false;
        bIsBasicChannel = true;
        return false;
    }

    public boolean openLogicalChannel(byte[] var1, byte var2)
    {
        byte[] response;

        if (bChannelClosed == false) {
            Log.e(TAG, "channel is already open\n");
            return false;
        }
        bChannelClosed = false;
        bIsBasicChannel = false;

        response = transmit(bSelectChannel);
        /* check if response is valid */
        if(response.length != 0x03) {
            Log.e(TAG, "select channel command response length error, phase0");
            Log.e(TAG, Arrays.toString(response));
            return false;
        }
        if ((response[1] != (byte)0x90) && (response[2] != (byte)0x90)) {
            Log.e(TAG, "select channel command response error, phase0");
            Log.e(TAG, Arrays.toString(response));
            return false;
        }

        //var1[0] = response[0];
        var1[3] = var2;
        response = transmit(var1);
        if ((response[0] != (byte)0x90) && (response[0] != (byte)0x90)) {
            Log.e(TAG, "select channel command response error, phase1");
            Log.e(TAG, Arrays.toString(response));
            return false;
        }

        return true;
    }

    public boolean isBasicChannel()
    {
        return bIsBasicChannel;
    }
}
