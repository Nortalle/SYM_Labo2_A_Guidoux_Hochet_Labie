package com.heigvd.sym.symlabo2.Service;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestMaker {

    final static String GET = "GET";
    final static String POST = "POST";

    private CommunicationEventListener listener;

    public void sendRequest(String request, String url) {
        sendRequest(request, url, POST);
    }

    public void sendRequest(String request, String urltext, String verb) {

        SymTask task = new SymTask(urltext, verb, listener);
        task.execute(request);
    }

    public void setCommunicationEventListener(CommunicationEventListener listener) {
        this.listener = listener;
    }

    static class SymTask extends AsyncTask<String, Void, String> {

        private String url;
        private String verb;
        private CommunicationEventListener listener;

        public SymTask(String url, String verb, CommunicationEventListener listener) {
            this.url = url;
            this.verb = verb;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... request) {

            try {

                String body = request.length == 0 ? null : request[0];
                String contentType = request.length != 2 ? "text/plain" : request[1];

                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(verb);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", contentType);

                if(body != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(body);
                    writer.flush();
                }

                int statusCode = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while((inputLine = in.readLine()) != null)
                    content.append(inputLine);
                in.close();
                System.out.println("Post read response");

                connection.disconnect();

                return content.toString();

            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String result) {
            if(this.listener != null) this.listener.handleServerResponse(result);
        }
    }
}
