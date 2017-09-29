package com.example.wirecamp.activity.resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Pramod on 27-09-2017.
 */
abstract public class BaseResource implements Cloneable, Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    abstract public void convertJSONObject2Resource(JSONObject jsonObject) throws JSONException;
    abstract public JSONObject convert2JSONObject() throws JSONException;

    public BaseResource getClone() throws CloneNotSupportedException{
        return (BaseResource) super.clone();
    }
}
