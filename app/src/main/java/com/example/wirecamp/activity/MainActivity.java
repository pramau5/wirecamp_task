package com.example.wirecamp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.wirecamp.activity.handlers.DatabaseHandler;
import com.example.wirecamp.activity.handlers.ServerRequestHandler;
import com.example.wirecamp.activity.handlers.Util;
import com.example.wirecamp.activity.resources.BaseResource;
import com.example.wirecamp.activity.resources.TempObj;
import com.example.wirecamp.activity.resources.WeatherObj;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Activity ca;
    DatabaseHandler handler;

    private static final String TAG = MainActivity.class.getSimpleName();

    EditText editTextCityName;
    Button btnByCityName;
    GraphView graphView;
    ImageView imageView;
    String day_x="",temp_y="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ca = this;

        handler = DatabaseHandler.getInstance(this);

        editTextCityName = (EditText)findViewById(R.id.city);
        btnByCityName = (Button)findViewById(R.id.submit);
        graphView = (GraphView) findViewById(R.id.graph);
        imageView = (ImageView) findViewById(R.id.profile_pic);

        String pic = getIntent().getExtras().getString("pic");

        if (!Util.isEmpty(pic)) {
            Picasso.with(ca)
                    .load(pic)
                    .error(R.drawable.ic_card_travel_black_24dp)
                    .placeholder(R.drawable.ic_card_travel_black_24dp)
                    .fit()
                    .centerCrop()
                    .into(imageView);
        }


        btnByCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInput()) {
                    InputMethodManager imm = (InputMethodManager) ca.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
        List<BaseResource> resources = handler.getByExpression(new WeatherObj(), "object_type='TEMP_OBJ'");
        if (resources.size() != 0) {
            System.out.println("Local DB :: "+resources.get(0));
            String _res = "";
            for (int i =0; i<resources.size();i++) {
                final WeatherObj weatherObj = (WeatherObj) resources.get(i);
                JSONObject _temp = weatherObj.getTempObj();
                if (_temp != null) {
                    try {
                        _res = _res + _temp.getString("day") + " Of " + Util.getDateFormatted(weatherObj.getDt()) + "\n\n";

                        String[] day_date = Util.getDateFormatted(weatherObj.getDt()).split("/");
                        if (day_date.length > 0) {
                            if ("".equals(day_x)) {
                                day_x = day_date[0];
                            } else {
                                day_x = day_x + "-" + day_date[0];
                            }
                            if ("".equals(temp_y)) {
                                temp_y = _temp.getString("day");
                            } else {
                                temp_y = temp_y + "-" + _temp.getString("day");
                            }
                        }
                    } catch (JSONException e) {
                        System.out.println("JSON Excep" + e);
                    }
                }
            }
            return;
        }
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


                    // Storing to Local DB/ Caching.
                    handler.delete(new WeatherObj().getClass(), weatherObj.getId());
                    weatherObj.setObject_type("TEMP_OBJ");
                    weatherObj.setTempObj(weatherObj.getTempObj());
                    handler.addOrUpdate(weatherObj);

                    JSONObject _temp =  weatherObj.getTempObj();
                    if (_temp!=null) {
                        try {
                            _res = _res + _temp.getString("day") +" Of "+ Util.getDateFormatted(weatherObj.getDt())+"\n\n";

                            String[] day_date = Util.getDateFormatted(weatherObj.getDt()).split("/");
                            if (day_date.length>0) {
                                if ("".equals(day_x)) {
                                    day_x = day_date[0];
                                } else {
                                    day_x = day_x + "-" + day_date[0];
                                }
                                if ("".equals(temp_y)) {
                                    temp_y = _temp.getString("day");
                                } else {
                                    temp_y = temp_y + "-" + _temp.getString("day");
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("JSON Excep" + e);
                        }
                    }
                }
                String[] sep_day = day_x.split("-");
                String[] sep_temp = temp_y.split("-");
                for (int i=0;i<sep_day.length;i++) {
                    System.out.println("PPP ::   "+sep_day[i] + "   :: TEMP Values :: "+ sep_temp[i]);
                }
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(Integer.parseInt(sep_day[2]), Double.parseDouble(sep_temp[2])),
                        new DataPoint(Integer.parseInt(sep_day[3]), Double.parseDouble(sep_temp[3])),
                        new DataPoint(Integer.parseInt(sep_day[4]), Double.parseDouble(sep_temp[4])),
                        new DataPoint(Integer.parseInt(sep_day[5]), Double.parseDouble(sep_temp[5])),
                        new DataPoint(Integer.parseInt(sep_day[6]), Double.parseDouble(sep_temp[6])),
//                        new DataPoint(Integer.parseInt(sep_day[1]), Double.parseDouble(sep_temp[1])),
//                        new DataPoint(Integer.parseInt(sep_day[0]), Double.parseDouble(sep_temp[0])),

                });
                graphView.addSeries(series);

                Viewport viewport = graphView.getViewport();
                viewport.setYAxisBoundsManual(true);
                viewport.setMinY(100);
                viewport.setMaxY(500);
                viewport.setScrollable(true);
            }

            @Override
            public void error(String message) {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        });

    }

}
