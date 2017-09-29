package com.example.wirecamp.activity.callbacks;

import com.example.wirecamp.activity.resources.BaseResource;
import java.util.List;

/**
 * Created by Rahul Tiwari on 22-06-2015.
 */
public interface GetC4tCallback {
    public void success(List<BaseResource> resources);
    public void error(String message);
}
