package com.heigvd.sym.symlabo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.heigvd.sym.symlabo2.Service.CommunicationEventListener;
import com.heigvd.sym.symlabo2.Service.RequestMaker;

public class AsynchroneActivity extends AppCompatActivity {

    private RequestMaker maker = new RequestMaker();
    private TextView write;
    private TextView result;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asynchrone);

        write = (TextView) findViewById(R.id.async_write_text);
        result = (TextView) findViewById(R.id.async_result_text);
        sendButton = (Button) findViewById(R.id.async_send_button);

        maker.setCommunicationEventListener(new CommunicationEventListener() {
            public void handleServerResponse(String response) {
                Toast.makeText(AsynchroneActivity.this, "Reponse du serveur", Toast.LENGTH_LONG).show();
                System.out.println(response);
                result.setText(response);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Clean result
                result.setText("");

                String request = write.getText().toString();
                if(request.length() == 0)
                    Toast.makeText(AsynchroneActivity.this, "Vous n'avez rien écrit", Toast.LENGTH_LONG).show();
                else {
                    maker.sendRequest(request, "http://sym.iict.ch/rest/txt");
                }
            }
        });
    }
}
