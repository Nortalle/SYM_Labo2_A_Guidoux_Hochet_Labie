package com.heigvd.sym.symlabo2.Service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityChecker extends Thread{

    private ConnectivityManager connectivityManager;
    private long    delay;
    private boolean run;

    public ConnectivityChecker(Context context, long delay){
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        run = false;
        this.delay = delay;
    }



    public void cancel(){
        run = false;
    }



    @Override
    public void run() {

        NetworkInfo networkInfo;

        run = true;

        while (run){

            networkInfo  = connectivityManager.getActiveNetworkInfo();

            if(networkInfo !=  null && networkInfo.isConnected()){
                System.out.println("CONNECTE !");
            }else {
                System.out.println("MERDEUUUX !");
            }

            try {
                sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
