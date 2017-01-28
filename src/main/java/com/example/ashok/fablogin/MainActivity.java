package com.example.ashok.fablogin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{
    Uri imageuri;
    AccessTokenTracker accessTokenTracker;
    String username, firstname, lastname;
    TextView tv_profile_name;
    ImageView iv_profile_pic;

    private CallbackManager callbackManager;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        tv_profile_name = (TextView) findViewById(R.id.tv_profile_name);
        iv_profile_pic = (ImageView) findViewById(R.id.iv_profile_pic);

        final LoginButton loginButton=(LoginButton)findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));

        callbackManager= CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Toast.makeText(MainActivity.this,"Login Success", Toast.LENGTH_LONG).show();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {


                                try {
                                    String email = object.getString("email");
                                    String birthday = object.getString("birthday");
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    tv_profile_name.setText(name);

                                    String imageur1 = "https://graph.facebook.com/" + id + "/picture?type=large";

                                    Picasso.with(MainActivity.this).load(imageur1).into(iv_profile_pic);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

/**
 * AccessTokenTracker to manage logout
 */
                accessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,AccessToken currentAccessToken) {
                        if (currentAccessToken == null) {
                            tv_profile_name.setText("");
                            iv_profile_pic.setImageResource(R.drawable.home);
                        }
                    }
                };
            }
            @Override
            public void onCancel()
            {
                Toast.makeText(MainActivity.this,"Login Canceled",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onError(FacebookException e)
            {
                Toast.makeText(MainActivity.this,e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
