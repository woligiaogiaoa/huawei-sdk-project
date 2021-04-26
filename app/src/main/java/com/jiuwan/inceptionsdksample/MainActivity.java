package com.jiuwan.inceptionsdksample;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jiuwan.publication.PublicationSDK;
import com.jiuwan.publication.LoginCallback;
import com.jiuwan.publication.callback.ExitCallback;
import com.jiuwan.publication.data.pay.HuaweiPayParam;


public class MainActivity extends AppCompatActivity {

    public static String TAG="mainactivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logining.setValue(false);
        gaming.setValue(false);

        /*--------------------sdk api -----*/
        PublicationSDK.onCreate(this);
        PublicationSDK.setLoginCallback(new LoginCallback() {

            @Override
            public void onLoginSuccess(String userInfo) {
                Log.e(TAG, "onLoginSuccess id: "+userInfo );
                gaming.setValue(true);
                logining.setValue(false);
                //enter game
            }

            @Override
            public void onLoginError(String message,int code) {
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                logining.setValue(false);
            }

        });

        observe();
    }

    private void observe() {
        gaming.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean ingame) {
                findViewById(R.id.bt_login).setVisibility(ingame ? View.GONE :View.VISIBLE);
            }
        });

        logining.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean inLogingProgress) {
                findViewById(R.id.bt_login).setEnabled(!inLogingProgress);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*--------------------sdk api -----*/
        PublicationSDK.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*--------------------sdk api -----*/
        PublicationSDK.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*--------------------sdk api -----*/
        PublicationSDK.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*--------------------sdk api -----*/
        PublicationSDK.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*--------------------sdk api -----*/
        PublicationSDK.onDestroy();
    }

    MutableLiveData<Boolean> logining= new MutableLiveData<Boolean>();
    MutableLiveData<Boolean> gaming= new MutableLiveData<Boolean>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*--------------------sdk api -----*/
        PublicationSDK.onActivityResult(requestCode,resultCode,data);
    }

    public void login(View view) {
        logining.setValue(true);
        /*--------------------sdk api -----*/
        PublicationSDK.login(this);
    }

    public void switch1(View view) {
        /*--------------------sdk api -----*/

        PublicationSDK.exit(this, new ExitCallback() {
            @Override
            public void onSuccess() {
                gaming.setValue(false);
                logining.setValue(false);
            }
        });
        /*--------------------sdk api -----*/
        PublicationSDK.login(this);
    }

    public void pay(View view) {
        /*--------------------sdk api -----*/
        PublicationSDK.paramsPay(new HuaweiPayParam.Builder()
        .callbackUrl("http://test")
                .extendData("http://test")
                .gameOrderNum("testorder123")
                .price("100")
                .productId("test01")
                .productName("testproduct01")
                .roleID("roleid123")
                .roleLevel("1")
                .roleName("shuai")
                .serverID("server001")
                .serverName("server001")
                .build());
    }

    public void logout(View view) {
        /*--------------------sdk api -----*/
        PublicationSDK.exit(this, new ExitCallback() {
            @Override
            public void onSuccess() {
                gaming.setValue(false);
                logining.setValue(false);
            }
        });
    }
}