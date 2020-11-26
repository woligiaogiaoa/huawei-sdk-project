package com.batam.sdk.widget;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.batam.sdk.SDKManager;

public class LoadingFragment extends DialogFragment {
    private DialogInterface.OnCancelListener cancelListener;

    public DialogInterface.OnCancelListener getCancelListener() {
        return cancelListener;
    }

    public void setCancelListener(DialogInterface.OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    private static class Holder{
        private static LoadingFragment fragment=new LoadingFragment();
    }

    public static void show(FragmentManager manager){
        LoadingFragment dialog=Holder.fragment;
        if(!dialog.isAdded()){
            dialog.show(manager,LoadingFragment.class.getName());
        }
    }

    public static void hide(){
        Holder.fragment.dismissAllowingStateLoss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(getLayoutIdByName(""),container,false);
        return view;
    }

    public static int getLayoutIdByName(String name) {
        return SDKManager.getInstance().getApplication().getResources().getIdentifier(name, "layout", SDKManager.getInstance().getApplication().getPackageName());
    }
}
