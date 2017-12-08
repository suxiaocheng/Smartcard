package com.desay.openmobile;

import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;

/**
 * Created by uidq0655 on 2017/12/7.
 */

public class Channel {
    private Session mSession;
    private final Tmc200 mChannel;
    private final Object mLock = new Object();

    Channel(Session session, Tmc200 channel) {
        this.mSession = session;
        this.mChannel = channel;
    }

    public void close() {
        if(this.mSession.getReader().getSEService() != null) {
            if(this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                if(!this.isClosed()) {
                    Object var1 = this.mLock;
                    this.mChannel.closeChannel();
                }

            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public boolean isClosed() {
        if(this.mSession.getReader().getSEService() != null) {
            if(this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                return this.mChannel.isChannelClosed();
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public boolean isBasicChannel() {
        if(this.mSession.getReader().getSEService() != null) {
            if(this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                return this.mChannel.isBasicChannel();
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public byte[] transmit(byte[] command) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NullPointerException {
        if(this.mSession.getReader().getSEService() != null) {
            if(this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                Object var2 = this.mLock;
                synchronized(this.mLock) {
                    byte[] response = this.mChannel.transmit(command);
                    return response;
                }
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public Session getSession() {
        return this.mSession;
    }

    public byte[] getSelectResponse() {
        if(this.mSession.getReader().getSEService() != null) {
            if(this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                throw new IllegalStateException("not support");
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public boolean selectNext() throws IOException, IllegalStateException, UnsupportedOperationException {
        if(this.mSession.getReader().getSEService() != null) {
            if(this.mChannel == null) {
                throw new IllegalStateException("channel must not be null");
            } else {
                throw new IllegalStateException("not support");
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }
}
