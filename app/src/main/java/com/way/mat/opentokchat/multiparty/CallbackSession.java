package com.way.mat.opentokchat.multiparty;

/**
 * Created by oleh on 02.09.16.
 */
public interface CallbackSession {

    void onSessionConnected();

    void onSessionDisconnected();

    void onSessionError(final String error);
}
