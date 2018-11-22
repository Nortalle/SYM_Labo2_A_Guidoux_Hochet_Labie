package com.heigvd.sym.symlabo2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.heigvd.sym.symlabo2.Service.ConnectivityChecker;

public class DIffereeActivity extends AppCompatActivity {


    private ConnectivityChecker connectivityChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_differee);

        connectivityChecker = new ConnectivityChecker(this, 1000);

        connectivityChecker.run();

    }
}
