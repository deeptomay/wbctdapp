package com.example.testapp.apiservice;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.testapp.bean.LoginBean;
import com.example.testapp.Login2Activity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class OtpAsyncTask extends AsyncTask<String,Void,String> {

    Login2Activity loginActivity;
    ProgressDialog p;

    public OtpAsyncTask(Login2Activity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    protected void onPreExecute() {

        p = new ProgressDialog(loginActivity);
        p.setMessage("Verifying...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = "http://10.153.36.162:2525/WBCTD_API/loginOtpCheck?usrid=" + params[0]+"&otp=" + params[1];

        System.out.println("user_id>>>>"+params[0]);

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
            e.printStackTrace();
        }

        return response.toString();
    }

    protected void onPostExecute(String s) {

        try {
            // Parse the JSON array
            ObjectMapper objectMapper = new ObjectMapper();
            List<LoginBean> loginDetailsList = objectMapper.readValue(s, new TypeReference<List<LoginBean>>() {});

            // Extract values from the first object in the array
            if (!loginDetailsList.isEmpty()) {
                LoginBean loginDetails = loginDetailsList.get(0); // Get the first object

                // Store values in string variables
                String otp_status = loginDetails.getOtp_verify_status();

                loginActivity.setResulttoUIforOTP(otp_status);


                p.hide();

            }
            else{
                p.hide();

                Toast.makeText(loginActivity,"OTP doesn't match!",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
