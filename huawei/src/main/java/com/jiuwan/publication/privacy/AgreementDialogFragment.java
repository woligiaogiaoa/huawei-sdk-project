package com.jiuwan.publication.privacy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.jiuwan.publication.PublicationSDK;
import com.jiuwan.publication.R;

public class AgreementDialogFragment extends CenterDialogFragment {

    public static String AGREE_KEY="AGREE_KEY";

    public interface AgreeLisener {
        void onUseAgree(boolean isAgreed);
    }

    AgreeLisener agreeLisener;

    public AgreeLisener getAgreeLisener() {
        return agreeLisener;
    }

    public void setAgreeLisener(AgreeLisener agreeLisener) {
        this.agreeLisener = agreeLisener;
    }

    @Override
    int layoutId() {
        return R.layout.dialog_agree;
    }


    @Override
    void initWhenViewCreated() {
        sp= PreferenceManager.getDefaultSharedPreferences(getContext());
        myView.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putBoolean(AGREE_KEY,true).apply();
                agree=true;
                agreeSet=true;
                agreeLisener.onUseAgree(true);
                dismissAllowingStateLoss();
            }
        });
        myView.findViewById(R.id.tv_deny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.findViewById(R.id.v_final).setVisibility(View.VISIBLE);
                myView.findViewById(R.id.v_start).setVisibility(View.GONE);
               /* agree=false;
                agreeSet=true;
                agreeLisener.onUseAgree(false);
                dismissAllowingStateLoss();*/
            }
        });
        myView.findViewById(R.id.tv_deny1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agree=false;
                agreeSet=true;
                agreeLisener.onUseAgree(false);
                dismissAllowingStateLoss();
            }
        });
        myView.findViewById(R.id.tv_ok1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putBoolean(AGREE_KEY,true).apply();
                agree=true;
                agreeSet=true;
                agreeLisener.onUseAgree(true);
                dismissAllowingStateLoss();
            }
        });
        setTermsClickDestination(myView.findViewById(R.id.tv_privacy));

    }

    void setTermsClickDestination(TextView view) {
        SpannableStringBuilder smsLoginTermsBuilder = new SpannableStringBuilder(view.getText());
        String text = view.getText().toString();
        int index1 = 39+9;
        smsLoginTermsBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#7BC2DB")),
                index1, index1+5, Spanned.SPAN_INCLUSIVE_INCLUSIVE
        );
        smsLoginTermsBuilder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if(PublicationSDK.goodsAndPrivacy!=null){
                    String url = PublicationSDK.goodsAndPrivacy.getPtl();
                    try {
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, index1, index1+5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        int index2 = index1+7;
        smsLoginTermsBuilder.setSpan(
                new ForegroundColorSpan(Color.parseColor("#7BC2DB")),
                index2, index2+5, Spanned.SPAN_INCLUSIVE_INCLUSIVE
        );
        smsLoginTermsBuilder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if(PublicationSDK.goodsAndPrivacy!=null){
                    String url = PublicationSDK.goodsAndPrivacy.getPvy();
                    try {
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, index2, index2+5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        view.setMovementMethod(LinkMovementMethod.getInstance()) ;
        view.setText(smsLoginTermsBuilder);
    }

    SharedPreferences sp;
    boolean agree=false;
    boolean agreeSet=false;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(!agreeSet){
            agreeLisener.onUseAgree(false);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);
        return  dialog;
    }
}
