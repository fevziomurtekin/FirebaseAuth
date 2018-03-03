package com.firebase.auth.Fragment;

import android.os.Bundle;
import android.view.View;

import com.firebase.auth.R;

import fevziomurtekin.Fragment.MyFragment;

/**
 * Created by omurt on 28.02.2018.
 */

public class Home extends MyFragment implements View.OnClickListener{

    public static Home newInstance() {

        Bundle args = new Bundle();
        args.putInt(KEY_ID, R.layout.main_layout);
        Home fragment = new Home();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }
    }
}
