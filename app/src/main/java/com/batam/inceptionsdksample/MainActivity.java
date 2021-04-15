package com.batam.inceptionsdksample;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.batam.sdk.SDKManager;
import com.batam.sdk.Userlistener;
import com.batam.sdk.data.pay.HuaweiPayParam;


public class MainActivity extends AppCompatActivity {

    public static String TAG="mainactivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logining.setValue(false);
        gaming.setValue(false);
        SDKManager.getInstance().init(this);
        observe();
        SDKManager.getInstance().setUserlistener(new Userlistener() {
            @Override
            public void onUserSwitchAccount() {
                gaming.setValue(false);
            }

            @Override
            public void onLoginSuccess(String userInfo) {
                Log.e(TAG, "onLoginSuccess id: "+userInfo );
                gaming.setValue(true);
                logining.setValue(false);
                //enter game
            }

            @Override
            public void onLoginError(String message) {
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                logining.setValue(false);
            }

            @Override
            public void onLogout() {
                gaming.setValue(false);
                logining.setValue(false);
            }
        });
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
        SDKManager.getInstance().onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SDKManager.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SDKManager.getInstance().onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SDKManager.getInstance().onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SDKManager.getInstance().onDestroy();
    }

    MutableLiveData<Boolean> logining= new MutableLiveData<Boolean>();
    MutableLiveData<Boolean> gaming= new MutableLiveData<Boolean>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKManager.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    public void login(View view) {
        logining.setValue(true);
        SDKManager.getInstance().login(this);
    }

    public void switch1(View view) {

        SDKManager.getInstance().logout();
        gaming.setValue(false); //退出游戏
        logining.setValue(true);
        SDKManager.getInstance().login(this);
    }

    public void pay(View view) {
/*
        String order ="{\n" +
                "    \"game_num\": \"1213132131312\",\n" +
                "    \"value\": \"100\",\n" +
                "    \"role_name\": \"帅哥\",\n" +
                "    \"props_name\":\"testproduct01\"," +
                "    \n" +
                "        \"role_id\": \"123\",\n" +
                "        \"server_id\": \"456\",\n" +
                "        \"server_name\": \"一服\",\n" +
                "        \"callback_url\":\" http://test.com\",\n" +
                "        \"extend_data\":\"http://test.com\"\n" +
                "        \n" +
                "\n" +
                "}";

        SDKManager.getInstance().h5OrderJsonPay(order);*/
        SDKManager.getInstance().paramsPay(new HuaweiPayParam.Builder()
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
        //SDKManager.getInstance().consume();
    }

    public void logout(View view) {
        SDKManager.getInstance().logout();
    }
}