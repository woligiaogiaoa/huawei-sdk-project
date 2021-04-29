package com.jiuwan.publication.privacy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jiuwan.publication.R;


public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jiuwan_dialog_activity);
        AgreementDialogFragment dialogFragment=new AgreementDialogFragment();
        dialogFragment.setAgreeLisener(new AgreementDialogFragment.AgreeLisener() {
            @Override
            public void onUseAgree(boolean isAgreed) {
                if(isAgreed){
                    setResult(RESULT_OK);
                    finish();
                }else {
                    setResult(RESULT_CANCELED);
                    finish();
                }

            }
        });
        try {
            dialogFragment.show(getSupportFragmentManager(),"AgreementDialogFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
