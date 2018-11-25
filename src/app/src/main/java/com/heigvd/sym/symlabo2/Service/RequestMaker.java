package com.heigvd.sym.symlabo2.Service;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe permettant de réaliser des requêtes en arrière plan
 */
public class RequestMaker {

    final static String GET     = "GET";
    final static String POST    = "POST";

    private CommunicationResponseEventListener listener;

    public void sendRequest(String request, String url) {
        sendRequest(request, url, POST);
    }

    public void sendRequest(String request, String urltext, String verb) {
        sendRequest(request, urltext, verb, "text/plain");
    }

    public void sendRequest(String request, String urltext, String verb, String contentType) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);
        sendRequest(request, urltext, verb, headers);
    }

    public void sendRequest(String request, String urltext, String verb, Map<String, String> headers) {

        SymTask task = new SymTask(urltext, verb, listener, headers);
        task.execute(request);
    }

    public void setCommunicationEventListener(CommunicationResponseEventListener listener) {
        this.listener = listener;
    }

    /**
     * Tache interne permettant d'exécuter la requête dans un thread séparé
     */
    static class SymTask extends AsyncTask<String, Void, ServerResponse> {

        private String url;
        private String verb;
        private Map<String, String> headers;
        private CommunicationResponseEventListener listener;

        public SymTask(String url, String verb, CommunicationResponseEventListener listener, Map<String, String> headers) {
            this.url = url;
            this.verb = verb;
            this.headers = headers;
            this.listener = listener;
        }

        @Override
        protected ServerResponse doInBackground(String... request) {

            try {

                // Récupération du corps de la requête s'il y en a un
                String body = request.length == 0 ? null : request[0];
                URL url = new URL(this.url);

                // Création de la connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(verb);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                // Assignation des headers
                for(Map.Entry<String, String> header : this.headers.entrySet())
                    connection.setRequestProperty(header.getKey(), header.getValue());

                // Ajout du body s'il y en a un
                if(body != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(body);
                    writer.flush();
                }

                // Partie bloquante en attendant une réponse serveur
                int statusCode = connection.getResponseCode();

                // Récupération des headers
                Map<String, String> responseHeaders = new HashMap<>();
                for(Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                    StringBuilder value = new StringBuilder();
                    List<String> header = entry.getValue();
                    for(int i = 0; i < header.size(); i++) {
                         value.append(header.get(i));
                         if(i < header.size() - 1) value.append(",");
                    }

                    responseHeaders.put(entry.getKey(), value.toString());
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                // Récupération de la réponse
                while((inputLine = in.readLine()) != null)
                    content.append(inputLine);
                in.close();

                connection.disconnect();

                // Délégation au listener s'il y en a un dans onPostExecute
                return new ServerResponse(statusCode, content.toString(), responseHeaders);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(ServerResponse response) {
            if(this.listener != null) this.listener.onServerResponse(response);
        }
    }
}
