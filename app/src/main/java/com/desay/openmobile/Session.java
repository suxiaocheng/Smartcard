package com.desay.openmobile;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Created by uidq0655 on 2017/12/7.
 */

public class Session {
    private final Object mLock = new Object();
    private final Reader mReader;
    private final Tmc200 mSession;

    Session(Tmc200 session, Reader reader) {
        this.mReader = reader;
        this.mSession = session;
    }

    public Reader getReader() {
        return this.mReader;
    }

    public byte[] getATR() {
        if(this.mReader.getSEService() != null) {
            if(this.mSession == null) {
                throw new IllegalStateException("service session is null");
            } else {
                return this.mSession.getAtr();
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public void close() {
        if(this.mReader.getSEService() != null) {
            if(this.mSession != null) {
                Object var1 = this.mLock;
                synchronized(this.mLock) {
                    this.mSession.close();
                }
            }

        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public boolean isClosed() {
        return this.mSession == null || this.mSession.isSessionClosed();
    }

    public void closeChannels() {
        if(this.mReader.getSEService() != null) {
            if(this.mSession != null) {
                Object var1 = this.mLock;
                synchronized(this.mLock) {
                    this.mSession.closeChannels();
                }
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public Channel openBasicChannel(byte[] aid, byte p2) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NoSuchElementException, UnsupportedOperationException {
        if(this.mReader.getSEService() != null) {
            if(this.mSession == null) {
                throw new IllegalStateException("service session is null");
            } else if(this.getReader() == null) {
                throw new IllegalStateException("reader must not be null");
            } else {
                Object var3 = this.mLock;
                synchronized(this.mLock) {
                    Channel var10000;
                    if (this.mSession.openBasicChannel(aid, p2)){
                        var10000 = new Channel(this, this.mSession);
                        return var10000;
                    }
                    return null;
                }
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public Channel openBasicChannel(byte[] aid) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NoSuchElementException, UnsupportedOperationException {
        return this.openBasicChannel(aid, (byte)0);
    }

    public Channel openLogicalChannel(byte[] aid, byte p2) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NoSuchElementException, UnsupportedOperationException {
        if(this.mReader.getSEService() != null) {
            if(this.mSession == null) {
                throw new IllegalStateException("service session is null");
            } else if(this.getReader() == null) {
                throw new IllegalStateException("reader must not be null");
            } else {
                Object var3 = this.mLock;
                synchronized(this.mLock) {
                    Channel var10000;
                    if (this.mSession.openLogicalChannel(aid, p2)){
                        var10000 = new Channel(this, this.mSession);
                        return var10000;
                    }
                    return null;
                }
            }
        } else {
            throw new IllegalStateException("service not connected to system");
        }
    }

    public Channel openLogicalChannel(byte[] aid) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException, NoSuchElementException, UnsupportedOperationException {
        return this.openLogicalChannel(aid, (byte)0);
    }
}
