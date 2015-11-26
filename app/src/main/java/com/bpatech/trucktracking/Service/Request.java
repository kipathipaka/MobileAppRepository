package com.bpatech.trucktracking.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.bpatech.trucktracking.Util.ServiceConstants;
import com.bpatech.trucktracking.R;
import com.bpatech.trucktracking.Util.SessionManager;
import com.bpatech.trucktracking.Activity.ConnectionDetector;

public class Request {
    private Context myContext;
    BufferedReader reader;
    String serverUrl;
    HttpResponse response,locationResponse;
    JSONObject responseJson;
    Boolean internetStatus;

    public Request() {

    }

    public Request(Context context) {
        myContext = context;
    }

    public HttpResponse requestDeleteType(String URL,String version) {
        serverUrl = version + URL;
        try {
            internetStatus = connectionCheck();
            if (internetStatus == true) {
                URI website = new URI(serverUrl);
             HttpClient client;
                client = new DefaultHttpClient();
                HttpDelete deleterequest = new HttpDelete(serverUrl);
               // deleterequest.setParams(getTimeOutParams());
               // deleterequest.setURI(website);
                response = client.execute(deleterequest);
            } else {
                noInternetConnection();
            }

        } catch (Exception e) {

            ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
            StatusLine sl = new BasicStatusLine(pv, 999, "Network Issue");
            response = new BasicHttpResponse(sl);
            return response;
        }
        return responseValidation(response);

    }

    public HttpResponse requestGetType(String Url,String BASE_URL) {


        try {
            internetStatus = connectionCheck();
            if (internetStatus == true) {
                serverUrl = BASE_URL+Url;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(serverUrl);
                response = client.execute(request);
            } else {
                noInternetConnection();
            }

        }  catch (Exception e) {

            e.printStackTrace();

            ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
            StatusLine sl = new BasicStatusLine(pv, 999, "Network Issue");
            response = new BasicHttpResponse(sl);
            return responseValidation(response);

        }
        return responseValidation(response);
    }

    public JSONObject responseParsing(HttpResponse response) {
        try {
            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(
                    entity.getContent()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            responseJson = new JSONObject(sb.toString());

        } catch (Exception e) {

        }
        return responseJson;
    }

    public JSONArray responseArrayParsing(HttpResponse response) {
        JSONArray responsearr = null;
        try {
            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(
                    entity.getContent()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            responsearr = new JSONArray(sb.toString());

        } catch (Exception e) {

            ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
            StatusLine sl = new BasicStatusLine(pv, 999, "Network Issue");
            response = new BasicHttpResponse(sl);
            return responsearr;
        }
        return responsearr;
    }

    public HttpResponse requestPutType(String Url,List<NameValuePair> userlist,String BASE_URL) {

        try {
            internetStatus = connectionCheck();
            if (internetStatus==true) {
                HttpClient client = new DefaultHttpClient();
                serverUrl = BASE_URL + Url;
                URI website = new URI(serverUrl);

                HttpPut request = new HttpPut(serverUrl);
               /* request.setParams(getTimeOutParams());
                request.setURI(website);
                StringEntity entityString = new StringEntity(
                        jsonObj.toString(), "UTF8");*/
                request.setEntity(new UrlEncodedFormEntity(userlist));
                /*request.setHeader(ServiceConstants.CONTENT_TYPE_KEY,
                        ServiceConstants.CONTENT_TYPE_VALUE);
                request.setHeader(ServiceConstants.X_APP_VERSION_KEY,
                        ServiceConstants.X_APP_VERSION_VALUE);*/

                response = client.execute(request);
            } else {
                noInternetConnection();
            }

        } catch (Exception e) {

            ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
            StatusLine sl = new BasicStatusLine(pv, 999, "Network Issue");
            response = new BasicHttpResponse(sl);
            return response;
        }
        return responseValidation(response);

    }

    public HttpResponse withOutCertificate(String Url, List<NameValuePair> userlist) throws Exception {
        serverUrl = ServiceConstants.BASE_URL + Url;

        URI website = new URI(serverUrl);
        HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        DefaultHttpClient client = new DefaultHttpClient();

        SchemeRegistry registry = new SchemeRegistry();
        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        socketFactory
                .setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
        registry.register(new Scheme("http", socketFactory, 443));
        SingleClientConnManager mgr = new SingleClientConnManager(
                client.getParams(), registry);
        DefaultHttpClient httpClient = new DefaultHttpClient(mgr,
                client.getParams());

        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

        HttpPost httpPost = new HttpPost(serverUrl);
        httpPost.setURI(website);
       // StringEntity entityString = new StringEntity(jsonObj.toString(), "UTF8");
        httpPost.setEntity(new UrlEncodedFormEntity(userlist));

        HttpResponse response = httpClient.execute(httpPost);
        return responseValidation(response);
    }

    public HttpResponse requestPostType(String Url,List<NameValuePair> userlist, String BASE_URL) {
        try {
            internetStatus = connectionCheck();
            if (internetStatus==true) {
                serverUrl = BASE_URL+Url;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(serverUrl);
                request.setEntity(new UrlEncodedFormEntity(userlist));
                response = client.execute(request);
            } else {
               // networkIssue();
               noInternetConnection();
            }

        } catch (SSLException e) {
            try {
                response = withOutCertificate(Url,userlist);
            } catch (Exception e1) {

                e.printStackTrace();
                e1.printStackTrace();

                ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
                StatusLine sl = new BasicStatusLine(pv, 999, "Network Issue");
                response = new BasicHttpResponse(sl);
                return responseValidation(response);
            }
            return responseValidation(response);
        }
         catch (Exception e) {

            e.printStackTrace();

            ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
            StatusLine sl = new BasicStatusLine(pv, 999, "Network Issue");
            response = new BasicHttpResponse(sl);
            return responseValidation(response);

        }
        return responseValidation(response);
    }
    public HttpResponse requestLocationServicePostType(String Url,List<NameValuePair> userlist, String BASE_URL) {
        try {
            internetStatus = connectionCheck();
            if (internetStatus==true) {
                serverUrl = BASE_URL+Url;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(serverUrl);
                request.setEntity(new UrlEncodedFormEntity(userlist));
                locationResponse = client.execute(request);
            } else {
                // networkIssue();
                noInternetConnection();
            }

        } catch (SSLException e) {
            try {
                locationResponse = withOutCertificate(Url,userlist);
            } catch (Exception e1) {

                e.printStackTrace();
                e1.printStackTrace();

                ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
                StatusLine sl = new BasicStatusLine(pv, 999, "Network Issue");
                locationResponse = new BasicHttpResponse(sl);
                return responseValidation(locationResponse);
            }
            return responseValidation(locationResponse);
        }
        catch (Exception e) {

            e.printStackTrace();

            ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
            StatusLine sl = new BasicStatusLine(pv, 999, "Network Issue");
            response = new BasicHttpResponse(sl);
            return responseValidation(locationResponse);

        }
        return responseValidation(locationResponse);
    }

    public HttpResponse responseValidation(HttpResponse response) {

       if (response.getStatusLine().getStatusCode()!= 200) {
            networkIssue();
        }

        return response;
    }

    // this method will check the internet status
    public boolean connectionCheck() {
        ConnectionDetector cd = new ConnectionDetector(myContext);
        boolean status = cd.isConnectingToInternet();
        return status;
    }

    // this method will set the timeout parameters to httpclient
    public HttpParams getTimeOutParams() {

        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = 15000;// 5000
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 20000;// 10000
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        // DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        return httpParameters;

    }

    public void networkIssue() {
        ((Activity) myContext).runOnUiThread(new Runnable() {
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(myContext);
              /*  View view = inflater.inflate(R.layout.network_failure_popup,
                        null);*/
                View promptsView = inflater.inflate(R.layout.network_failure,null);


                final Dialog dialog = new Dialog(myContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                promptsView.setBackgroundResource(R.color.white);
                dialog.setContentView(promptsView);
                dialog.show();

                Button btnOK = (Button) promptsView.findViewById(R.id.btn_OK);
                btnOK.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();

                    }

                });
            }
        });
    }
    public void noInternetConnection() {
        ((Activity) myContext).runOnUiThread(new Runnable() {
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(myContext);
               View promptsView = inflater.inflate(R.layout.network_failure_popup,
                        null);

                final Dialog dialog = new Dialog(myContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                promptsView.setBackgroundResource(R.color.white);
                dialog.setContentView(promptsView);
                dialog.show();

                Button btnOK = (Button) promptsView.findViewById(R.id.btnOK);
                btnOK.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();

                    }

                });
            }
        });
    }
}
