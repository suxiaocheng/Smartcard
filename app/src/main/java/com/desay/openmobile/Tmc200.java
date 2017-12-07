package com.desay.openmobile;

/**
 * Created by uidq0655 on 2017/12/5.
 */

public class Tmc200 {
    public native boolean open();

    public native byte[] transmit(byte[] command);

    public native byte[] reset();

    public native byte[] getAtr();

    public native void close();

    public native boolean isClosed();

    public native boolean closeChannels();

    public native boolean openBasicChannel(byte[] var1, byte var2);

    public native boolean openLogicalChannel(byte[] var1, byte var2);

    public native boolean isBasicChannel();

    static {
        System.loadLibrary("tmc200");   //defaultConfig.ndk.moduleName
    }
}
