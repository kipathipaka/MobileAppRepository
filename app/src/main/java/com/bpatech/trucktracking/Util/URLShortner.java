package com.bpatech.trucktracking.Util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class URLShortner {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public URLShortner() {

    }

    public JSONObject getJSONFromUrl(String tripid) {

        // Making HTTP request
        System.out.println("tripid:::::::::::::::::::::::"+tripid);
        try {
           String address="https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyC2p7LAIkxVgj6962KN5Y01GcpH0LBxZ54";
           String longUrl="http://ec2-52-88-194-128.us-west-2.compute.amazonaws.com:2020/vehicletracking-spring/api/web/"+tripid;
            // DefaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
           JSONObject inputJson = new JSONObject();
            inputJson.put("longUrl",longUrl);
            HttpPost httpPost = new HttpPost(address);


            httpPost.setEntity(new StringEntity(inputJson.toString()));
            httpPost.setHeader("Content-Type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200){
                jObj = parseResponse(httpResponse);
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Return JSON String
        return jObj;

    }
    private JSONObject parseResponse(HttpResponse httpResponse){
        try {
        HttpEntity httpEntity = httpResponse.getEntity();
        is = httpEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
            Log.e("JSON", json);
            jObj = new JSONObject(json);
        }catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }


        return jObj;
    }
}
