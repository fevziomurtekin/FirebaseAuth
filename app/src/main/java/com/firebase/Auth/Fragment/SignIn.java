package com.kuarkdijital.fixturemakersocial.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuarkdijital.fixturemakersocial.MainActivity;
import com.kuarkdijital.fixturemakersocial.Model.Users;
import com.kuarkdijital.fixturemakersocial.R;

import fevziomurtekin.Fragment.MyFragment;
import fevziomurtekin.Key;
import fevziomurtekin.Util;

/**
 * Created by omurt on 20.02.2018.
 */

public class SignIn extends MyFragment implements View.OnClickListener {

    private EditText edtEmail,edtPass;
    private Button signIn;
    private TextView forgot_pass,facebook,twitter,google,doyouhaveaccount,signUp,loginWith;
    private RelativeLayout rl_sign_in;

    public static SignIn newInstance() {
        Bundle args = new Bundle();
        args.putInt(KEY_ID, R.layout.sign_in);
        SignIn fragment = new SignIn();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void createItems() {
        super.createItems();

        edtEmail            = (EditText) getChildItems(R.id.edtEmail);
        edtPass             = (EditText) getChildItems(R.id.edtPass);
        signIn              = (Button) getChildItems(R.id.btnSignIn);
        forgot_pass         = (TextView) getChildItems(R.id.txtForgotPass);
        facebook            = (TextView) getChildItems(R.id.btnFacebook);
        google              = (TextView) getChildItems(R.id.btnGoogle);
        twitter             = (TextView) getChildItems(R.id.btnTwitter);
        doyouhaveaccount    = (TextView) getChildItems(R.id.btnhaveAccount);
        signUp              = (TextView) getChildItems(R.id.btnSignUp);
        loginWith           = (TextView) getChildItems(R.id.login_with);
        rl_sign_in          = (RelativeLayout) getChildItems(R.id.rl_sign_in);

        edtEmail            .setTypeface(getTypeFaces().ArimoR);
        edtPass             .setTypeface(getTypeFaces().ArimoR);
        loginWith           .setTypeface(getTypeFaces().ArimoB);
        forgot_pass         .setTypeface(getTypeFaces().ArimoR);
        facebook            .setTypeface(getTypeFaces().ArimoB);
        twitter             .setTypeface(getTypeFaces().ArimoB);
        google              .setTypeface(getTypeFaces().ArimoB);
        signUp              .setTypeface(getTypeFaces().ArimoB);
        doyouhaveaccount    .setTypeface(getTypeFaces().ArimoR);
        signIn              .setTypeface(getTypeFaces().ArimoR);

        signIn              .setOnClickListener(this);
        signUp              .setOnClickListener(this);
        facebook            .setOnClickListener((View.OnClickListener)getAct());
        twitter             .setOnClickListener((View.OnClickListener)getAct());
        google              .setOnClickListener((View.OnClickListener)getAct());
        forgot_pass         .setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSignIn:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(Util.getPref(getAct(),Key.SUCCES).equals("true")) {
                            Intent i=new Intent(getAct(),MainActivity.class);
                            i       .putExtra(Key.LOGİN,true);
                            getAct().dismissLoading();
                            startActivity(i);
                        }else
                            getAct().showAlert(getString(R.string.loginerror));
                    }
                },3000);
                getAct().showLoading();
                getAct().haveRecord(edtEmail.getText().toString(),edtPass.getText().toString());
                break;
            case R.id.btnSignUp:
                getAct().openDetail(R.id.sign_container, SignUp.newInstance());
                break;

            case R.id.txtForgotPass:
                getAct().showForgotDialog(rl_sign_in,this);
                break;
            case R.id.btnSend:
                //TODO send işlemi
                break;

        }
    }



}
