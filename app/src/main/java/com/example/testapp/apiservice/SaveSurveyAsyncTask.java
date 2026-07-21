package com.example.testapp.apiservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.testapp.MainActivity;
import com.example.testapp.UploadPicActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaveSurveyAsyncTask extends AsyncTask<String,Void,String> {



    UploadPicActivity ua;
    ProgressDialog p;

    public SaveSurveyAsyncTask(UploadPicActivity ua) {
        this.ua = ua;
    }

    @Override
    protected void onPreExecute() {
        p = new ProgressDialog(ua);
        p.setMessage("Submitting...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = "http://10.153.45.133:2525/WBCTD_API/getDataInsert?usr_cd=" + params[0]+"&gstin=" + params[1]+"&physical_add=" + params[2]+"&lat=" + params[3]+"&lon=" + params[4];
//        String urlString = "http://10.153.36.161:2525/WBCTD_API/getDataInsert?usr_cd=" + params[0]+"&gstin=" + params[1]+"&physical_add=" + params[2]+"&lat=" + params[3]+"&lon=" + params[4];

        System.out.println("user_id>>>>"+params[0]);
        System.out.println("phy_addrs>>>>"+params[2]);

        StringBuilder response = new StringBuilder();
        try {
            // Create URL object
            URL url = new URL(urlString);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("POST");

            // Set headers if needed (optional)
//            connection.setRequestProperty("Accept", "application/json");

            // Check response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read response
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) { // Success
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else { // Error handling
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }


            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Print the response
            System.out.println("Response: " + response.toString());

            // Disconnect the connection
            connection.disconnect();

        } catch (Exception e) {
            p.hide();
            Toast.makeText(ua,"Error in saving!!!",Toast.LENGTH_SHORT).show();
        }

        return response.toString();
    }


    @Override
    protected void onPostExecute(String s) {
        p.hide();
        Toast.makeText(ua,s,Toast.LENGTH_SHORT).show();
        ua.returnToMain();


    }
}
