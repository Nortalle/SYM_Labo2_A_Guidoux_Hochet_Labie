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
import android.util.JsonReader;
import android.view.View;
import android.widget.AdapterView;
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
    private List<Author> authors;
    private List<Post> posts;
    private long currentAuthorId = 0L;

    private final String firstCursor = "Séléctionnez un auteur";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph_ql);

        //On initialise les deux listes principales de cette activité
        authors = new ArrayList<>();
        posts = new ArrayList<>();

        dropDownMenu = findViewById(R.id.dropDownMenu);
        textView = findViewById(R.id.textView);

        //On ne peut cliquer sur le menu déroulant car l'activity charge toujours tous les auteurs
        dropDownMenu.setEnabled(false);

        //On rend textView scrollable
        textView.setMovementMethod(new ScrollingMovementMethod());

        //Dès qu'on séléctionne un auteur, on fait la requête pour voir ses posts
        dropDownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //Si ce n'est pas la première option et si ce n'est pas la même qu'avant, on fait la
                //requête au serveur
                if(i != 0 && (currentAuthorId != i)) {
                    //requête pour avoir tous les posts de l'auteur.
                    //TODO enlevé id et description pour la prod, pas utile, à moins qu'on les affiche
                    String query = "{\"query\":\"{allPostByAuthor(authorId :" + (i) + "){id title description content date}}\"}";

                    //On envoie la requête pour les post
                    maker.sendRequest(query, "http://sym.iict.ch/api/graphql", "POST", "application/json");

                    //On signale à l'utilisateur que ça charge, il ne peut pas utiliser le menu déroulant
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    dropDownMenu.setEnabled(false);
                    currentAuthorId = i;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                textView.setText("Vous n'avez séléctionné aucun auteur");
            }
        });

        maker.setCommunicationEventListener(new CommunicationEventListener() {
            public void handleServerResponse(String response) {

                //On signale à l'utilasateur que plus rien ne charge, il peut utiliser le menu déroulant à nouveau
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                dropDownMenu.setEnabled(true);

                //On convertit la réponse en InputStream pour pouvoir lire le JSON plus facilement
                InputStream inputStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
                JsonReader reader = null;


                try {
                    //On se facilite la tâche pour lire le JSON
                    reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

                    //Si c'est la réponse avec tous les auteurs, on les mets dans le menu déroulant
                    if (response.startsWith("{\"data\":{\"allAuthors\":")) {

                        //On va chercher nos informations dans le JSON
                        authors = readListAuthors(reader);

                        List<String> authorsName = new ArrayList<String>();

                        //Fait en sorte qu'il n'y ait pas un appel au serveur inutile
                        authorsName.add(firstCursor);

                        //TODO peut-être qu'on a pas besoin de reprendre chaque auteur et leur prénom pour les mettre dans le menu déroulant
                        for (Author author : authors) {
                            authorsName.add(author.toString());
                        }

                        //convertit un tableau de chaîne de caractères pour le mettre dans le menu déroulant
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GraphQLActivity.this, android.R.layout.simple_spinner_item, authorsName);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        dropDownMenu.setAdapter(adapter);

                    } else {
                        //on va chercher nos informations sur les posts dans le JSON
                        posts = readListPosts(reader);

                        //Affiche les posts de l'auteur
                        textView.setText(posts.toString());
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                } finally {
                    //On ferme le JSONreader
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //Dès le chargement de la page, on demande au serveur de nous donner les auteurs
        String query = "{\"query\":\"{allAuthors{id first_name last_name}}\"}";
        maker.sendRequest(query, "http://sym.iict.ch/api/graphql", "POST", "application/json");
    }

    public List<Author> readListAuthors(JsonReader reader) throws IOException {
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

    private List<Post> readListPosts(JsonReader reader) throws IOException {
        List<Post> list = new ArrayList<Post>();

        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("data")) {

                reader.beginObject();
                if (reader.nextName().equals("allPostByAuthor")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        list.add(readPost(reader));
                    }
                    reader.endArray();
                }
                reader.endObject();
            }
        }
        reader.endObject();
        return list;
    }

    private Post readPost(JsonReader reader) throws IOException {
        String title = null;
        String description = null;
        String content = null;
        String date = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String nextName = reader.nextName();
            if (nextName.equals("title")) {
                title = reader.nextString();
            } else if (nextName.equals("description")) {
                description = reader.nextString();
            } else if (nextName.equals("content")) {
                content = reader.nextString();
            } else if (nextName.equals("date")) {
                date = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Post(title, description, content, date);
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

    public class Post {
        private String title;
        private String description;
        private String content;
        private String date;

        public Post(String title, String description, String content, String date) {
            this.title = title;
            this.description = description;
            this.content = content;
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String toString() {
            String out = "";
            out += "title: " + title + "\n" + "\n";
            out += "description: " + description + "\n" + "\n";
            out += "content: " + content + "\n" + "\n";
            out += "date: " + date + "\n" + "\n";

            return out;
        }
    }
}