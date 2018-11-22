package com.heigvd.sym.symlabo2;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.heigvd.sym.symlabo2.Service.CommunicationEventListener;
import com.heigvd.sym.symlabo2.Service.RequestMaker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class GraphQLActivity extends AppCompatActivity {

    private RequestMaker maker = new RequestMaker();
    private Spinner dropDownMenu;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_ql);

        dropDownMenu = (Spinner) findViewById(R.id.dropDownMenu);
        textView = (TextView) findViewById(R.id.textView);

        String query = "{\"query\":\"{allAuthors{id first_name last_name}}\"}";

        maker.setCommunicationEventListener(new CommunicationEventListener() {
            public void handleServerResponse(String response) {
                Toast.makeText(GraphQLActivity.this, "Reponse du serveur", Toast.LENGTH_LONG).show();

                textView.setText(response);
                try {
                    textView.setText(readJsonStream(response).toString());
                    List<Author> authors = readJsonStream(response);
                    List<String> authorsName = new ArrayList<String>();

                    for (Author author : authors) {
                        authorsName.add(author.toString());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(GraphQLActivity.this, android.R.layout.simple_spinner_item, authorsName);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dropDownMenu.setAdapter(adapter);
                } catch (IOException e) {
                    textView.setText("Pasencore");
                    Toast.makeText(GraphQLActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });


        maker.sendRequest(query, "http://sym.iict.ch/api/graphql", "POST", "application/json");

    }

    public List<Author> readJsonStream(String in) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(in.getBytes(Charset.forName("UTF-8")));

        JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        try {
            return readgraphQL(reader);
        } finally {
            reader.close();
        }
    }

    public List<Author> readgraphQL(JsonReader reader) throws IOException {
        List<Author> authors = new ArrayList<Author>();


        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("data")) {

                reader.beginObject();
                if (reader.nextName().equals("allAuthors")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        authors.add(readAuthor(reader));
                    }
                    reader.endArray();
                }
                reader.endObject();
            }
        }
        reader.endObject();
        return authors;
    }

    public Author readAuthor(JsonReader reader) throws IOException {

        long id = 0;
        String firstname = null;
        String lastname = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String nextName = reader.nextName();
            if (nextName.equals("id")) {
                id = reader.nextLong();
            } else if (nextName.equals("first_name")) {
                firstname = reader.nextString();
            } else if (nextName.equals("last_name")) {
                lastname = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Author(id, firstname, lastname);
    }

    class Author {
        private long id;
        private String firstname;
        private String lastname;

        public Author(long id, String firstname, String lastname) {
            this.id = id;
            this.firstname = firstname;
            this.lastname = lastname;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        @Override
        public String toString() {
            return firstname + " " + lastname;
        }
    }
}
