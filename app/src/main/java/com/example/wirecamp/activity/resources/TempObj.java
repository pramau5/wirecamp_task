package com.example.wirecamp.activity.resources;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rahul Tiwari on 21-06-2015.
 */
public class TempObj extends BaseResource implements Cloneable {

    private Double day;
    private Double min;
    private Double max;
    private Double night;
    private Double eve;
    private Double morn;

    public Double getDay() {
        return day;
    }

    public void setDay(Double day) {
        this.day = day;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getNight() {
        return night;
    }

    public void setNight(Double night) {
        this.night = night;
    }

    public Double getEve() {
        return eve;
    }

    public void setEve(Double eve) {
        this.eve = eve;
    }

    public Double getMorn() {
        return morn;
    }

    public void setMorn(Double morn) {
        this.morn = morn;
    }

    @Override
    public void convertJSONObject2Resource(JSONObject jsonObject) throws JSONException {

        if (jsonObject.has("day")) {
            this.setDay(jsonObject.getDouble("day"));
        }

        if (jsonObject.has("min")) {
            this.setMin(jsonObject.getDouble("min"));
        }

        if (jsonObject.has("max")) {
            this.setMax(jsonObject.getDouble("max"));
        }

        if (jsonObject.has("night")) {
            this.setNight(jsonObject.getDouble("night"));
        }

        if (jsonObject.has("eve")) {
            this.setEve(jsonObject.getDouble("eve"));
        }

        if (jsonObject.has("morn")) {
            this.setMorn(jsonObject.getDouble("morn"));
        }

    }

    @Override
    public JSONObject convert2JSONObject() throws JSONException {
        JSONObject object = new JSONObject();
        if (this.day != null) object.put("day", this.day);
        if (this.min != null) object.put("min", this.min);
        if (this.max != null) object.put("max", this.max);
        if (this.night != null) object.put("night", this.night);
        if (this.eve != null) object.put("eve", this.eve);
        if (this.morn != null) object.put("morn", this.morn);
        return object;
    }

}
