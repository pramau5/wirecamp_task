package com.example.wirecamp.activity.handlers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.wirecamp.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.example.wirecamp.activity.callbacks.GetC4tCallback;
import com.example.wirecamp.activity.callbacks.PostC4tCallback;
import com.example.wirecamp.activity.resources.BaseResource;

/**
 * Created by Pramod on 27-09-2017.
 */
public class ServerRequestHandler {
    private static final String TAG = "ServerRequestHandler";
    String  arguid;
    boolean loginRequired;
    View promptView;
    private String resource;
    private String queryId;
    private ProgressBarHandler progressBarHandler;
    private Context context;
    private boolean background= false;
    private int CONNECTION_TIMEOUT = 1000*20;    // timeout 20 second

    public ServerRequestHandler(Context context, boolean isWaitDialog) {
        this.context = context;
        loginRequired = true;
        progressBarHandler = new ProgressBarHandler(context, isWaitDialog);
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                progressBarHandler.show();
            }
        background = false;
    }

    public ServerRequestHandler(Context context, boolean isWaitDialog,boolean background) {
        this.context = context;
        loginRequired = true;
        progressBarHandler = new ProgressBarHandler(context, isWaitDialog);
        this.background = background;
        if (!background) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                progressBarHandler.show();
            }
        }
    }

    public ServerRequestHandler(Context context) {
        this.context = context;
    }

    public String getArguid() {
        return arguid;
    }

    public void setArguid(String arguid) {
        this.arguid = arguid;
    }

    public boolean isLoginRequired() {
        return loginRequired;
    }

    public void setLoginRequired(boolean loginRequired) {
        this.loginRequired = loginRequired;
    }

    public boolean isBackground() {
        return background;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }


    public void setResource(String resource) {
        this.resource = resource;
    }

    public void getDataRequest(BaseResource baseResource, GetC4tCallback callback) {
        if (Util.isInternetConnected(context)) {
            new GetHandler(baseResource, callback).execute();
        } else {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                progressBarHandler.hide();
                noInternetHandling();
            }
        }
    }

    public void postDataRequest(BaseResource baseResource, PostC4tCallback callback) {
        if (Util.isInternetConnected(context)) {
            new PostHandler(baseResource, callback).execute();
        } else {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                progressBarHandler.hide();
                noInternetHandling();
            }
        }
    }

    public void actionRequest(BaseResource baseResource, String action, PostC4tCallback callback) {
        if (Util.isInternetConnected(context)) {
            new PostHandler(baseResource, action, callback).execute();
        } else {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                progressBarHandler.hide();
                noInternetHandling();
            }
        }
    }

    public void noInternetHandling(){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        promptView = layoutInflater.inflate(R.layout.error, null);
        TextView title = (TextView) promptView.findViewById(R.id.title);
        title.setText("No Internet!");
        TextView detail = (TextView) promptView.findViewById(R.id.detail);
        detail.setText("No Internet. Check your connection or try again.");
        final AlertDialog.Builder alertDialogBuilders = new AlertDialog.Builder(context);
        alertDialogBuilders.setView(promptView);
        alertDialogBuilders.setCancelable(false)
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alerts = alertDialogBuilders.create();
        alerts.show();
    }

    public void serverNotAvailable(){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        promptView = layoutInflater.inflate(R.layout.error, null);
        TextView title = (TextView) promptView.findViewById(R.id.title);
        title.setText("Not available now.");
        TextView detail = (TextView) promptView.findViewById(R.id.detail);
        detail.setText("Sorry! Server is not available right now. Retry after some time.");
        final AlertDialog.Builder alertDialogBuilders = new AlertDialog.Builder(context);
        alertDialogBuilders.setView(promptView);
        alertDialogBuilders.setCancelable(false)
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alerts = alertDialogBuilders.create();
        alerts.show();
    }

    public void showError(String message){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        promptView = layoutInflater.inflate(R.layout.error, null);
        TextView title = (TextView) promptView.findViewById(R.id.title);
        title.setText("Error");
        TextView detail = (TextView) promptView.findViewById(R.id.detail);
        detail.setText(message);
        final AlertDialog.Builder alertDialogBuilders = new AlertDialog.Builder(context);
        alertDialogBuilders.setView(promptView);
        alertDialogBuilders.setCancelable(false)
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alerts = alertDialogBuilders.create();
        alerts.show();
    }

    private class GetHandler extends AsyncTask<Void, Void, JSONObject> {

        private GetC4tCallback callback;
        private BaseResource baseResource;

        GetHandler(BaseResource baseResource, GetC4tCallback callback) {
            this.baseResource = baseResource;
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {

                String responseStr = sendGetRequest();
                return new JSONObject(responseStr);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private String sendGetRequest() throws IOException {

            HttpURLConnection conn = null;
            InputStream in = null;
            try {

                String ServerUrl = Constants.SERVER_URL + resource + "?q=" + URLEncoder.encode(queryId, "UTF-8") + "&APPID=ea574594b9d36ab688642d5fbeab847e";

                Log.d(TAG, "REQUEST-URL :: " + ServerUrl);

                URL url = new URL(ServerUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(CONNECTION_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setDoInput(true);

                conn.connect();
                int response = conn.getResponseCode();
                if (response == 200) {
                    in = conn.getInputStream();
                    return readIt(in);
                }
                Log.d(TAG, "SERVER RESPONSE CODE :: " + response);
                return null;

            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream) throws IOException {
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            StringBuffer response = new StringBuffer();

            while((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            Log.d(TAG, "Server Response :: "+response.toString());
            return response.toString();
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (progressBarHandler != null) {
                    Activity activity = (Activity) context;
                    if (!activity.isFinishing()) {
                        progressBarHandler.hide();
                    }
            }
            int errCode = -1;
            String message = "Something went wrong!!! Please check your Internet!";
            if (response != null) {
                List<BaseResource> resources = new ArrayList<>();
                try {
                    errCode = response.getInt("cod");
                    message = response.getString("message");
                    if (errCode == 200) {
                        JSONArray arr = response.getJSONArray("list");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject __resource = arr.getJSONObject(i);
                            BaseResource obj = baseResource.getClone();
                            obj.convertJSONObject2Resource(__resource);
                            resources.add(obj);
                        }
                    } else if (errCode != 0){
                        Activity activity = (Activity) context;
                        if (!activity.isFinishing()) {
                            showError(message);
                        }

                        return;
                    }
                } catch (Exception e) {
                    Activity activity = (Activity) context;
                    if (!activity.isFinishing()) {
                        serverNotAvailable();
                    }
                }
                callback.success(resources);
            } else {
                Activity activity = (Activity) context;
                if (!activity.isFinishing()) {
                    serverNotAvailable();
                }
            }
        }
    }

    private class PostHandler extends AsyncTask<Void, Void, JSONObject> {

        private PostC4tCallback callback;
        private BaseResource baseResource;
        private String action;

        PostHandler(BaseResource baseResource, PostC4tCallback callback) {
            this.baseResource = baseResource;
            this.callback = callback;
        }

        PostHandler(BaseResource baseResource, String action, PostC4tCallback callback) {
            this.baseResource = baseResource;
            this.action = action;
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                String str = this.baseResource.convert2JSONObject().toString();
                Log.d(TAG, "SERVER REQUEST DATA :: " + str + " :: " + action);
                String encodedString = Util.encodeBase64(str);
                String parameters = "resource="+encodedString;
                if (this.action != null) {
                    parameters += "&action="+action;
                }

                String responseStr = sendPostRequest(parameters);
                Log.d(TAG, "SERVER RESPONSE :: " + responseStr);

                return new JSONObject(responseStr);

            } catch (Exception e) {
                return null;
            }
        }

        public String sendPostRequest(String urlParameters) {
            if (resource == null) return null;
            URL url;
            HttpURLConnection connection = null;
            String serverUrl = Constants.SERVER_URL+"/api/"+resource;
            Log.d(TAG, "REQUEST URL :: " + serverUrl);
            //String session_id = Util.getSessionId((Activity)context);
            try {
                //Create connection
                url = new URL(serverUrl);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
//                connection.setRequestProperty("Cookie", "session_id=" + session_id);
//                Log.d(TAG, "Session id -> " + session_id);

                connection.setReadTimeout(CONNECTION_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setUseCaches (false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream ());
                wr.writeBytes (urlParameters);
                wr.flush ();
                wr.close ();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();

            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {

                if(connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (progressBarHandler != null) {
                    Activity activity = (Activity) context;
                    if (!activity.isFinishing()) {
                        progressBarHandler.hide();
                    }
            }
            int errCode = -1;
            String message = "Something went wrong! Please try again!!";
            if (response != null) {

                try {
                    errCode = response.getInt("errCode");
                    message = response.getString("message");
                    if ((errCode == 0) && ("success".equalsIgnoreCase(message))) {
                        JSONArray arr = response.getJSONArray("resource");
                        JSONObject __jsonObject = arr.getJSONObject(0);
                        baseResource.convertJSONObject2Resource(__jsonObject);
                        callback.success(baseResource);
                    } else {
                        showError(message);
                    }
                } catch (Exception e) {
                        Activity activity = (Activity) context;
                        if (!activity.isFinishing()) {
                            serverNotAvailable();
                        }
                }
            } else {
                    Activity activity = (Activity) context;
                    if (!activity.isFinishing()) {
                        serverNotAvailable();
                    }
            }
            super.onPostExecute(response);
        }
    }

}
