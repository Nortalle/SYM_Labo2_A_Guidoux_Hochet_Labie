package com.heigvd.sym.symlabo2.Service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;


public class ConnectivityChecker extends AsyncTask<Void, Boolean, Void> {

    private ConnectivityManager connectivityManager;

    private ConnectionEventListener listener;

    private long    delay;
    private boolean run;

    public ConnectivityChecker(Context context, long delay, ConnectionEventListener listener){
        this.listener = listener;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.run = false;
        this.delay = delay;
    }


    public void cancel(){
        run = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {

            NetworkInfo networkInfo;

            run = true;

            while (run){

                networkInfo  = connectivityManager.getActiveNetworkInfo();

                publishProgress(networkInfo != null && networkInfo.isConnected());


                Thread.sleep(delay);
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Boolean... connect) {
        if(this.listener != null)
            this.listener.handleConnectionState(connect[0]);
    }

}
