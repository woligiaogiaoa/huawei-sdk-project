package com.jiuwan.publication.privacy;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jiuwan.publication.R;

import static com.jiuwan.publication.privacy.AgreementDialogFragment.AGREE_KEY;


public class DialogActivity extends AppCompatActivity {


    public static final String DIALOG_CANCELLABLE = "cancelable";

    private Boolean cancellable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean agree= PreferenceManager.getDefaultSharedPreferences(this).getBoolean(AGREE_KEY,false);
        if(agree){
            setResult(RESULT_OK);
            finish();
        }
        cancellable=getIntent().getBooleanExtra(DIALOG_CANCELLABLE,true);

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

    @Override
    public void onBackPressed() {
        if(cancellable)
            super.onBackPressed();
    }
}
