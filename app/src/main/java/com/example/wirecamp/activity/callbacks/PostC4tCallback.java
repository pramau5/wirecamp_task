package com.example.wirecamp.activity.callbacks;

import com.example.wirecamp.activity.resources.BaseResource;


/**
 * Created by Rahul Tiwari on 23-06-2015.
 */
public interface PostC4tCallback {

    public void success(BaseResource resource);
    public void error(String message);
}
