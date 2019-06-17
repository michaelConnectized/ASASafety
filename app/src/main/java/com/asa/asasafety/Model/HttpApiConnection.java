package com.asa.asasafety.Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpApiConnection extends ApiConnection {

    public HttpApiConnection(String fullUrl, String postData) {
        super(fullUrl, postData);
    }

    public String connect(String postData) {
        StringBuilder response = new StringBuilder("");
        try {
            URL url = new URL( fullUrl );
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestProperty("Content-Type", "application/json");
                if (!postData.equals("")) {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setFixedLengthStreamingMode(postData.getBytes().length);
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
                    writer.write(postData);
                    writer.flush();
                    writer.close();
                    os.close();
                }
                int responseCode=urlConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response.append(line);
                    }
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
        }
        return response.toString();
    }
}
