package com.heigvd.sym.symlabo2.Service;

public abstract class CommunicationEventListener implements CommunicationResponseEventListener {
    abstract public void handleServerResponse(String response);

    public void onServerResponse(ServerResponse response) {
        handleServerResponse(response.getResponse());
    }
}
