package com.example.wirecamp.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wirecamp.R;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;


public class LoginActivity extends Activity {

    public static String TAG = "LoginActivity";
    Activity currentActivity;
    private ImageButton facebookLogin;
    private CallbackManager callbackManager;
    public LoginActivity() {
        super();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null)
            getActionBar().hide();

        currentActivity = this;

        setContentView(R.layout.login_activity);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.e("Fb Login: ", "User ID:" + loginResult.getAccessToken().getUserId());
                Log.e("Fb Login: ", "Auth Token:" + loginResult.getAccessToken().getToken());

                if (Profile.getCurrentProfile() == null) {
                    Toast.makeText(currentActivity, "Unable to retrieve from Facebook Server, Please try again!", Toast.LENGTH_LONG).show();
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            Log.v("facebook - profile", profile2.getName());
                            mProfileTracker.stopTracking();
                            Profile.setCurrentProfile(profile2);
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                } else {
                    final Profile profile = Profile.getCurrentProfile();
                    Log.e("Fb Profile: ", " " + profile.getName());
                    Log.e("Fb Profile: ", "" + profile.getId());

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v("LoginActivity", response.toString());

                                    try {

                                        Uri pic = profile.getProfilePictureUri(32, 32);

                                        String pic_url = "" + pic;

                                        System.out.println("Pic ::  "+pic);



                                        Intent intent = new Intent(currentActivity,MainActivity.class);
                                        intent.putExtra("pic",pic_url);
                                        startActivity(intent);
                                        currentActivity.finish();

                                    } catch (Exception e) {
                                        Toast.makeText(currentActivity, "Unable to retrieve email from Facebook Server, Please try again!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();

                }
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "Facebook Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "Facebook Login attempt failed.");
            }
        });

        // customized fb login button
        facebookLogin = (ImageButton) findViewById(R.id.facebook);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(currentActivity, Arrays.asList("public_profile", "email"));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
