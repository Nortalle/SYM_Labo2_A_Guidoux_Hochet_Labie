package com.heigvd.sym.symlabo2;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.heigvd.sym.symlabo2.Service.CommunicationEventListener;
import com.heigvd.sym.symlabo2.Service.ConnectionEventListener;
import com.heigvd.sym.symlabo2.Service.ConnectivityChecker;
import com.heigvd.sym.symlabo2.Service.RequestMaker;

import java.util.LinkedList;
import java.util.List;


public class DIffereeActivity extends AppCompatActivity {

    final private String CONNECTE   = "oui";
    final private String DECONNECTE = "non";


    private ConnectivityChecker connectivityChecker;
    private RequestMaker maker = new RequestMaker();

    private TextView connected_txt;
    private TextView connected_state;
    private TextView waiting_elem_txt;
    private TextView nbr_waiting_txt;
    private TextInputEditText input_text;
    private Button sendButton;


    private int nbr_waiting_request;
    private boolean connected;
    private List<String> waitingRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_differee);

        nbr_waiting_request = 0;
        connected           = false;
        waitingRequests     = new LinkedList<String>();

        connected_txt       = (TextView) findViewById(R.id.differee_connected_text);
        connected_state     = (TextView) findViewById(R.id.differee_connected_state_text);
        sendButton          = (Button)   findViewById(R.id.differe_send_button);

        waiting_elem_txt    = (TextView) findViewById(R.id.differe_waiting_request_text);
        nbr_waiting_txt     = (TextView) findViewById(R.id.differe_nbr_waiting_text);

        input_text          = (TextInputEditText) findViewById(R.id.differe_input_text);


        nbr_waiting_txt.setText(Integer.toString(nbr_waiting_request));



        maker.setCommunicationEventListener(new CommunicationEventListener() {
            public void handleServerResponse(String response) {
                Toast.makeText(DIffereeActivity.this, "Reponse du serveur", Toast.LENGTH_LONG).show();
                System.out.println(response);
                //result.setText(response);
            }
        });


        connectivityChecker = new ConnectivityChecker(this, 1000, new ConnectionEventListener() {
            @Override
            public void handleConnectionState(boolean connect) {
                connected = connect;
                if(connect){
                    connected_state.setText(CONNECTE);
                }else {
                    connected_state.setText(DECONNECTE);
                }
            }
        });

        connectivityChecker.execute();


        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String request = input_text.getText().toString();

                if(request.length() == 0)
                    Toast.makeText(DIffereeActivity.this, "Vous n'avez rien écrit", Toast.LENGTH_LONG).show();
                else {
                    if(connected){
                        maker.sendRequest(request, "http://sym.iict.ch/rest/txt");
                    }else {
                        waitingRequests.add(request);
                        nbr_waiting_request++;
                        nbr_waiting_txt.setText(Integer.toString(nbr_waiting_request));
                    }
                }
            }
        });

    }
}
