package com.heigvd.sym.symlabo2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.heigvd.sym.symlabo2.Serialization.Tags;
import com.heigvd.sym.symlabo2.Service.CommunicationEventListener;
import com.heigvd.sym.symlabo2.Service.RequestMaker;

public class ObjetsActivity extends AppCompatActivity {

    private RequestMaker maker = new RequestMaker();

    private Button xmlButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objets);

        xmlButton = (Button) findViewById(R.id.object_send_button);


        maker.setCommunicationEventListener(new CommunicationEventListener() {
            public void handleServerResponse(String response) {
                //Toast.makeText(AsynchroneActivity.this, "Reponse du serveur", Toast.LENGTH_LONG).show();
                System.out.println(response);
            }
        });

        xmlButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Clean result
                //result.setText("");

                //System.out.println(generateXMLText());

                String request = generateXMLText();
                if(request.length() == 0)
                    Toast.makeText(ObjetsActivity.this, "Vous n'avez rien écrit", Toast.LENGTH_LONG).show();
                else {
                    maker.sendRequest(request, "http://sym.iict.ch/rest/xml");
                }
            }
        });
    }

    private String generateXMLText(){
        String XMLText = "";

        XMLText += Tags.XML_HEADER + "\n";
        XMLText += Tags.XML_DOCTYPE + "\n";

        XMLText += Tags.XML_DIRECTORY_START + "\n";
        XMLText += Tags.XML_PERSON_START + "\n";

        XMLText += Tags.XML_NAME_START + "Labie" +Tags.XML_NAME_END +"\n";
        XMLText += Tags.XML_FIRSTNAME_START + "Marc" +Tags.XML_FIRSTNAME_END +"\n";
        XMLText += Tags.XML_GENDER_START + "M" +Tags.XML_GENDER_END +"\n";
        XMLText += Tags.generateXMLPhoneTag("1234567890", Tags.PHONE_TYPE.MOBILE) +"\n";

        XMLText += Tags.XML_PERSON_END + "\n";
        XMLText += Tags.XML_DIRECTORY_END + "\n";

        return XMLText;
    }
}
