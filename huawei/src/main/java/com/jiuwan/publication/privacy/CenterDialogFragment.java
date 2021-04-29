package com.jiuwan.publication.privacy;


import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jiuwan.publication.R;

public abstract class CenterDialogFragment extends DialogFragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(layoutId(), container, false);
        return myView;
    }

    abstract int  layoutId();

    abstract void initWhenViewCreated();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWhenViewCreated();

    }


    @Override
    public void onResume() {
        super.onResume();
        Window window=getDialog().getWindow();
        if(window==null) return;

        Point size = new Point();


        Display display ;
              /*  (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) ?
            getContext().getDisplay() :
            else window?.windowManager?.defaultDisplay*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = getContext().getDisplay();
        }
        else {
            display=window.getWindowManager().getDefaultDisplay();
        }
        if(display==null)
            return;

        display.getSize(size);

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        if(getContext()==null) return;
                window.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_center_dialog_rounded));
    }
}