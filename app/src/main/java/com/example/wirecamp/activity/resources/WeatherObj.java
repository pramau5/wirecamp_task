package com.example.wirecamp.activity.resources;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rahul Tiwari on 21-06-2015.
 */
public class WeatherObj extends BaseResource implements Cloneable {

    private String cod;
    private Long message;
    private Long cnt;
    private Long dt;
    private Double pressure;
    private JSONObject tempObj;

    public JSONObject getTempObj() {
        return tempObj;
    }

    public void setTempObj(JSONObject tempObj) {
        this.tempObj = tempObj;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Long getDt() {
        return dt;
    }

    public void setDt(Long dt) {
        this.dt = dt;
    }

    public Long getMessage() {
        return message;
    }

    public void setMessage(Long message) {
        this.message = message;
    }

    public Long getCnt() {
        return cnt;
    }

    public void setCnt(Long cnt) {
        this.cnt = cnt;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    @Override
    public void convertJSONObject2Resource(JSONObject jsonObject) throws JSONException {

        if (jsonObject.has("cod")) {
            this.setCod(jsonObject.getString("cod"));
        }

        if (jsonObject.has("message")) {
            this.setMessage(jsonObject.getLong("message"));
        }

        if (jsonObject.has("cnt")) {
            this.setCnt(jsonObject.getLong("cnt"));
        }

        if (jsonObject.has("dt")) {
            this.setDt(jsonObject.getLong("dt"));
        }

        if (jsonObject.has("pressure")) {
            this.setPressure(jsonObject.getDouble("pressure"));
        }

        if (jsonObject.has("temp")) {
            this.setTempObj(jsonObject.getJSONObject("temp"));
        }
    }

    @Override
    public JSONObject convert2JSONObject() throws JSONException {
        JSONObject object = new JSONObject();
        if (this.cod != null) object.put("cod", this.cod);
        if (this.cnt != null) object.put("cnt", this.cnt);
        if (this.message != null) object.put("message", this.message);
        if (this.dt != null) object.put("dt", this.dt);
        if (this.pressure != null) object.put("pressure", this.pressure);
        if (this.tempObj != null) object.put("temp", this.tempObj);
        return object;
    }

}
