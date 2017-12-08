package com.desay.openmobile;

import android.content.Context;
import android.content.ServiceConnection;
import android.util.Log;

import java.util.Iterator;

/**
 * Created by uidq0655 on 2017/12/7.
 */

public class SEService {
    private static final String SERVICE_TAG = "SEService";
    private final Object mLock = new Object();
    private final Context mContext;
    private ServiceConnection mConnection;
    private Reader[] mReaders;
    private SEService.CallBack mCallerCallback;

    public SEService(Context context, SEService.CallBack listener) {
        if(context == null) {
            throw new NullPointerException("context must not be null");
        } else {
            this.mContext = context;
            this.mCallerCallback = listener;
            Log.v("SEService", "bindingSuccessful: ");
            listener.serviceConnected(this);
        }
    }

    public boolean isConnected() {
        return true;
    }

    public Reader[] getReaders() throws IllegalStateException, NullPointerException {
        if(this.mReaders == null) {
            Tmc200 tmc200 = new Tmc200();
            this.mReaders = new Reader[1];
            this.mReaders[0] = new Reader(tmc200, tmc200, "SE0");
        }

        return this.mReaders;
    }

    public void shutdown() {
        Object var1 = this.mLock;
        synchronized(this.mLock) {
            if(this.mReaders != null){
                if (this.mReaders[0] != null) {
                    this.mReaders[0].closeSessions();
                }
            }
        }
    }

    public String getVersion() {
        return "3.0";
    }

    public interface CallBack {
        void serviceConnected(SEService var1);
    }
}
