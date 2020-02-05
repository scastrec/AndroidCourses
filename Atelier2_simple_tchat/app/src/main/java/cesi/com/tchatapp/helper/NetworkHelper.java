package cesi.com.tchatapp.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import cesi.com.tchatapp.model.HttpResult;

/**
 * Created by sca on 29/05/15.
 */
public class NetworkHelper {


    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e("HelloWorld", "Error on checking internet:", e);

        }
        //default allowed to access internet
        return true;
    }

    /**
     * Doing get request
     * @param purl
     * @param params
     * @return
     */
    public static HttpResult doGet(String purl, Map<String, String> params, String token)  {

        // Un stream pour récevoir la réponse
        InputStream inputStream = null;
        if(purl == null){
            Log.e("CESI", "Error url to call empty");
            throw new RuntimeException("Error url to call empty");
        }

        try {
            StringBuilder sb = new StringBuilder(purl);
            sb.append("?");
            sb.append(concatParams(params));

            URL url = new URL(sb.toString());
            Log.d("Calling URL", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            if(token != null) {
                //set authorization header
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("NetworkHelper", "The response code is: " + response);

            if(response != 200){
                return new HttpResult(response, null);
            } else {
                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = NetworkHelper.readIt(inputStream);
                return new HttpResult(response, contentAsString);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            }
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {
            Log.e("NetworkHelper", e.getMessage());
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("NetworkHelper", e.getMessage());
                }
            }
        }
    }


    public static HttpResult doPost(String purl, Map<String, String> params, String token)  {

        // Un stream pour récevoir la réponse
        InputStream inputStream = null;
        if(purl == null){
            Log.e("CESI", "Error url to call empty");
            throw new RuntimeException("Error url to call empty");
        }

        try {
            URL url = new URL(purl);
            Log.d("Calling URL", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            if(token != null) {
                //set authorization header
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(concatParams(params));
            writer.flush();
            writer.close();
            os.close();
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("NetworkHelper", "The response code is: " + response);

            if(response != 200){
                return new HttpResult(response, null);
            } else {

                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = NetworkHelper.readIt(inputStream);
                return new HttpResult(response, contentAsString);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            }
        } catch (Exception e) {
            Log.e("NetworkHelper", e.getMessage());
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("NetworkHelper", e.getMessage());
                }
            }
        }
    }

    /**
     * Concat params to be send
     * @param params
     * @return
     */
    private static String concatParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size()>0){
            for (Map.Entry<String, String> entry : params.entrySet()){
                try {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                } catch (UnsupportedEncodingException e) {
                    Log.e("Cesi", "Error adding param", e);
                }
            }
        }
        return sb.toString();
    }


    // Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(stream));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
