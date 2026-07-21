package com.example.testapp.apiservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.testapp.UploadPicActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UploadAsysncTask extends AsyncTask<String,Void,String> {


    UploadPicActivity ua;
    ProgressDialog p;




    public UploadAsysncTask(UploadPicActivity ua) {
        this.ua = ua;

    }

    @Override
    protected void onPreExecute() {
        p = new ProgressDialog(ua);
        p.setMessage("Uploading...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();
    }

    @Override
    protected String doInBackground(String... params) {

        StringBuilder response = new StringBuilder();
        String requestURL = "http://10.153.45.133:2525/WBCTD_API/getPictureUpload"; // Change to your endpoint
//        String requestURL = "http://10.153.36.161:2525/WBCTD_API/getPictureUpload"; // Change to your endpoint
        String filePath = params[2]; // Change to your file path
        String charset = "UTF-8";
        String boundary = "===" + System.currentTimeMillis() + "===";
        String LINE_FEED = "\r\n";

        String usrid = params[0];
        String gstn = params[1];
        String fileName = params[3];
//        String cur_lat= params[4];
//        String cur_lon= params[5];
//        System.out.println("lat>>>>>>"+params[4]+"----lon>>>>>"+params[5]);


        try {
            File file = new File(filePath);
            HttpURLConnection connection = (HttpURLConnection) new URL(requestURL).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

            // Add form fields
            addFormField(writer, "usrid", usrid, boundary);
            addFormField(writer, "gstn", gstn, boundary);
            addFormField(writer, "file_name", fileName, boundary);
//            addFormField(writer, "lati", cur_lat, boundary);
//            addFormField(writer, "longi", cur_lon, boundary);

            // Add file part
            addFilePart(writer, outputStream, "file", file, boundary,LINE_FEED);

            // End of multipart form data
            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.flush();
            writer.close();

            // Get server response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                System.out.println("Server Response: " + response.toString());
            } else {
                System.out.println("Upload failed. Response Code: " + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        p.hide();
        Toast.makeText(ua,"Photo uploaded successfully",Toast.LENGTH_SHORT).show();
    }

    private static void addFormField(PrintWriter writer, String fieldName, String value, String boundary) {
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"\r\n");
        writer.append("Content-Type: text/plain; charset=UTF-8\r\n\r\n");
        writer.append(value).append("\r\n");
        writer.flush();
    }


    private static void addFilePart(PrintWriter writer, OutputStream outputStream, String fieldName, File file, String boundary, String LINE_FEED) throws IOException {

        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"file_image\"; filename=\"" + file.getName() + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);

        writer.flush();

        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append("\r\n");
        writer.flush();
    }
}
