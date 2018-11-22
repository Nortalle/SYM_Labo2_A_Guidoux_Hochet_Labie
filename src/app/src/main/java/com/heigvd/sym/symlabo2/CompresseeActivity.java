/*
 * File         : GraphQLActivity.java
 * Project      : Laboratoire 2
 * Authors      : Hochet Guillaume 25 novembre 2018
 *                Labie Marc 25 novembre 2018
 *                Guidoux Vincent 25 novembre 2018
 *
 * Description  :
 *
 */
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

        //fait disparaître le logo de chargement
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        typeChoices = (RadioGroup) findViewById(R.id.typeChoices);
        sendButton = (Button) findViewById(R.id.button_envoyer);
        sendView = (TextView) findViewById(R.id.sendView);
        writeView = (TextView) findViewById(R.id.writeView);

        //rend les TextView scrollable
        sendView.setMovementMethod(new ScrollingMovementMethod());
        writeView.setMovementMethod(new ScrollingMovementMethod());

        //Lorsqu'on reçoit une réponse du serveur
        maker.setCommunicationEventListener(new CommunicationEventListener() {
            public void handleServerResponse(String response) {
                //Operations pour finir le chargement
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                writeView.setEnabled(true);
                sendButton.setEnabled(true);

                response = response == null ? "Mauvaise requête, veuillez vérifier votre requête et réessayer" : response;

                //On affiche la réponse du serveur dans sendView
                sendView.setText(response);
            }
        });

        // Lorsqu'on clique sur "ENVOYER"
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //Opérations pour montrer le chargement
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                writeView.setEnabled(false);
                sendButton.setEnabled(false);
                sendView.setText("");

                // On prend le text qu'a écrit l'utilsateur dans writeView
                String request = writeView.getText().toString();

                //Si la requête est vide, on le signal à l'utilisateur
                if(request.length() == 0) {
                    Toast.makeText(CompresseeActivity.this, "Vous n'avez rien écrit", Toast.LENGTH_LONG).show();
                    sendView.setText("Vous n'avez rien écrit");
                } else {
                    //Si l'utilasteur a écrit dans la requête

                    //On regarde quel format de fichier l'utilisateur désire envoyer
                    int selectedId = typeChoices.getCheckedRadioButtonId();
                    String type = ((RadioButton) findViewById(selectedId)).getText().toString();

                    //On en détermine son Context-Type
                    String contentType = "";
                    if(type == "txt"){
                        contentType = "text/plain";
                    } else {
                        contentType = "application/" + type;
                    }

                    //on envoie la requête
                    maker.sendRequest(request, "http://sym.iict.ch/rest/" + type, "POST", contentType);
                }
            }
        });

        //Lorsqu'on séléctionne un format de fichier à envoyer
        typeChoices.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //On regarde quel format est choisi par l'utilisateur
                String type = ((RadioButton) findViewById(checkedId)).getText().toString();

                //Suivant le format, on propose à l'utilisateur des exemples d'envoi
                try {
                    InputStream is = getAssets().open("sample." + type);
                    int length = is.available();
                    byte[] data = new byte[length];
                    is.read(data);
                    String sample = new String(data);

                    writeView.setText(sample);
                    
                } catch (IOException e) {

                    sendView.setText("Erreur dans la génération de proposition de requête.");
                    e.printStackTrace();
                }
            }
        });
    }
}