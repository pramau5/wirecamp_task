package com.example.wirecamp.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.example.wirecamp.R;
import com.example.wirecamp.activity.callbacks.GetC4tCallback;
import com.example.wirecamp.activity.handlers.Constants;
import com.example.wirecamp.activity.handlers.ServerRequestHandler;
import com.example.wirecamp.activity.handlers.Util;
import com.example.wirecamp.activity.resources.BaseResource;
import com.example.wirecamp.activity.resources.TempObj;
import com.example.wirecamp.activity.resources.WeatherObj;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Activity ca;

    private static final String TAG = MainActivity.class.getSimpleName();

    EditText editTextCityName;
    Button btnByCityName;
    TextView textViewResult, textViewInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ca = this;

        editTextCityName = (EditText)findViewById(R.id.city);
        btnByCityName = (Button)findViewById(R.id.submit);
        textViewResult = (TextView)findViewById(R.id.result);
        textViewInfo = (TextView)findViewById(R.id.info);

        btnByCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInput()) {
                    fetchData();
                }
                //new GetData().execute(editTextCityName.getText().toString());
            }
        });

    }

    public boolean validateInput() {
        final String city_name = editTextCityName.getText().toString();
        if (Util.isEmpty(city_name) || (city_name.trim().length() == 0)) {
            if (!isFinishing())
                Toast.makeText(getApplicationContext(), "Please enter City name", Toast.LENGTH_SHORT).show();
            editTextCityName.requestFocus();
            return true;
        }
        return false;
    }

    public void fetchData() {
        ServerRequestHandler requestHandler = new ServerRequestHandler(ca, false);
        requestHandler.setResource("/data/2.5/forecast/daily");
        requestHandler.setQueryId(editTextCityName.getText().toString());
        requestHandler.getDataRequest(new WeatherObj(), new GetC4tCallback() {
            @Override
            public void success(List<BaseResource> resources) {
                if (resources.size() == 0) return;
                String _res = "";
                for (int i =0; i<resources.size();i++) {
                    final WeatherObj weatherObj = (WeatherObj) resources.get(i);
                    JSONObject _temp =  weatherObj.getTempObj();
                    if (_temp!=null) {
                        try {
                            Log.e(TAG,"Dt long "+weatherObj.getDt());
                            _res = _res + _temp.getString("day") +" Of "+ Util.getDateFormatted(weatherObj.getDt())+"\n\n";
                        } catch (JSONException e) {
                            System.out.println("JSON Excep" + e);
                        }
                    }
                }
                textViewResult.setText(_res);
            }

            @Override
            public void error(String message) {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        });

    }

}
