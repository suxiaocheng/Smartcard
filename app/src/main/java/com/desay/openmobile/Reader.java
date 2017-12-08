package com.desay.openmobile;

import android.os.RemoteException;

import java.io.IOException;

/**
 * Created by uidq0655 on 2017/12/7.
 */

public class Reader {
    private final String mName;
    private Object mService;
    private Tmc200 mReader;
    private final Object mLock = new Object();

    public Reader(Object service, Tmc200 reader, String name) {
        this.mName = name;
        this.mService = service;
        this.mReader = reader;
    }

    public String getName() {
        return this.mName;
    }

    public Session openSession() throws IOException {
        if(this.mService != null) {
            Object var1 = this.mLock;
            synchronized(this.mLock) {
                Session var10000;

                if (mReader.openReader() == true) {
                    var10000 = new Session(mReader, this);
                    return var10000;
                } else {
                    throw new IOException("openSession fail");
                }
            }
        } else {
            throw new IllegalStateException("service is not connected");
        }
    }

    public boolean isSecureElementPresent() {
        if(this.mService != null) {
            return true;
        } else {
            return false;
        }
    }

    public Object getSEService() {
        return this.mService;
    }

    public void closeSessions() {
        if(this.mService != null) {
            Object var1 = this.mLock;
            synchronized(this.mLock) {
                this.mReader.closeReader();
            }
        } else {
            throw new IllegalStateException("service is not connected");
        }
    }
}
