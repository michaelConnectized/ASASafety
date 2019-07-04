package com.asa.safety.utils.model;

import android.os.AsyncTask;

import java.net.URL;

public abstract class ApiConnection extends AsyncTask<URL, Integer, String> {
    protected String fullUrl;
    protected String postData;

    public ApiConnection(String fullUrl, String postData) {
        super();
        this.fullUrl = fullUrl;
        this.postData = postData;
    }

    protected String doInBackground(URL... urls) {
        return connect(postData);
    }

    public abstract String connect(String postData);

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        this.cancel(true);
    }

}
