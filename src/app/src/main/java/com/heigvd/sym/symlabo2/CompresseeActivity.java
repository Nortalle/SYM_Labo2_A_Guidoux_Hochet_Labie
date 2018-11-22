package com.heigvd.sym.symlabo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.heigvd.sym.symlabo2.Service.CommunicationEventListener;
import com.heigvd.sym.symlabo2.Service.RequestMaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CompresseeActivity extends AppCompatActivity {

    private RequestMaker maker = new RequestMaker();
    private RadioGroup typeChoices;
    private Button sendButton;
    private TextView sendView;
    private TextView writeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compressee);

        typeChoices = (RadioGroup) findViewById(R.id.typeChoices);
        sendButton = (Button) findViewById(R.id.button_envoyer);
        sendView = (TextView) findViewById(R.id.sendView);
        writeView = (TextView) findViewById(R.id.writeView);


        sendView.setMovementMethod(new ScrollingMovementMethod());
        writeView.setMovementMethod(new ScrollingMovementMethod());

        maker.setCommunicationEventListener(new CommunicationEventListener() {
            public void handleServerResponse(String response) {
                Toast.makeText(CompresseeActivity.this, "Reponse du serveur", Toast.LENGTH_LONG).show();
                System.out.println(response);
                sendView.setText(response);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Clean result
                sendView.setText("");

                String request = writeView.getText().toString();
                if(request.length() == 0)
                    Toast.makeText(CompresseeActivity.this, "Vous n'avez rien écrit", Toast.LENGTH_LONG).show();
                else {
                    int selectedId = typeChoices.getCheckedRadioButtonId();
                    String type = ((RadioButton) findViewById(selectedId)).getText().toString();
                    String contentType = "";
                    if(type == "txt"){
                        contentType = "text/plain";
                    } else {
                        contentType = "application/" + type;
                    }

                    maker.sendRequest(request, "http://sym.iict.ch/rest/" + type, "POST", contentType);
                }
            }
        });

        typeChoices.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                String type = ((RadioButton) findViewById(checkedId)).getText().toString();
                try {

                    sendView.setText("YOUPI");
                    InputStream is = getAssets().open("sample." + type);
                    int length = is.available();
                    byte[] data = new byte[length];
                    is.read(data);
                    String sample = new String(data);
                    writeView.setText(sample);
                    
                } catch (IOException e) {

                    sendView.setText("ERREUR");
                    e.printStackTrace();
                }
            }
        });


    }
}
