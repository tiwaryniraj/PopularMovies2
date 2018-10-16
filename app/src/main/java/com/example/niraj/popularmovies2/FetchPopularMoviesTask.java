package com.example.niraj.popularmovies2;

import android.os.AsyncTask;

import com.example.niraj.popularmovies2.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;


public class FetchPopularMoviesTask extends AsyncTask<URL, Void, String>{

    private AsyncTaskCompleteListener listener = null;

    public interface AsyncTaskCompleteListener {
        void asyncTaskProcessFinish(String output);
    }

    public FetchPopularMoviesTask(AsyncTaskCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL fetchUrl = urls[0];

        String mFetchResults = null;

        try {
            mFetchResults = NetworkUtils.getResponseFromHttp(fetchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mFetchResults;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.asyncTaskProcessFinish(s);
    }
}
