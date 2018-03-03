package com.kuarkdijital.fixturemakersocial.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kuarkdijital.fixturemakersocial.Model.Users;
import com.kuarkdijital.fixturemakersocial.R;

import fevziomurtekin.Fragment.MyFragment;
import fevziomurtekin.Key;
import fevziomurtekin.Util;

/**
 * Created by omurt on 20.02.2018.
 */

public class SignUp extends MyFragment implements View.OnClickListener{

    private EditText edtUsername,edtEmail,edtPass,edtPassAgain;
    private Button btnSignUp;
    private TextView txtAlreadyAccount,txtSignIn;

    public static SignUp newInstance() {
        Bundle args = new Bundle();
        args.putInt(KEY_ID, R.layout.sign_up);
        SignUp fragment = new SignUp();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void createItems() {
        super.createItems();
        edtUsername         = (EditText) getChildItems(R.id.edtUsername);
        edtEmail            = (EditText) getChildItems(R.id.edtEmail);
        edtPass             = (EditText) getChildItems(R.id.edtPass);
        edtPassAgain        = (EditText) getChildItems(R.id.edtPassAgain);
        btnSignUp           = (Button) getChildItems(R.id.btnSignUp);
        txtAlreadyAccount   = (TextView) getChildItems(R.id.btnAlreadyAccount);
        txtSignIn           = (TextView) getChildItems(R.id.btnSignIn);

        edtUsername         .setTypeface(getTypeFaces().ArimoR);
        edtEmail            .setTypeface(getTypeFaces().ArimoR);
        edtPass             .setTypeface(getTypeFaces().ArimoR);
        edtPassAgain        .setTypeface(getTypeFaces().ArimoR);
        btnSignUp           .setTypeface(getTypeFaces().ArimoB);
        txtAlreadyAccount   .setTypeface(getTypeFaces().ArimoR);
        txtSignIn           .setTypeface(getTypeFaces().ArimoB);

        btnSignUp.setOnClickListener(this);
        txtSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSignUp:
                if(edtPass.getText().toString().equals(edtPassAgain.getText().toString())){
                saveUsers();
                getAct().openDetail(R.id.sign_container, SignIn.newInstance());
                }else{
                    edtPassAgain.setError(getString(R.string.passerror));
                }
                break;
            case R.id.btnSignIn:
                getAct().openDetail(R.id.sign_container, SignIn.newInstance());
        }
    }

    private void saveUsers() {
        Users user          = new Users();
        user                .setEmail(edtEmail.getText().toString());
        user                .setUsername(edtUsername.getText().toString());
        user                .setPassword(getAct().md5(edtPass.getText().toString()));

        Util                .savePref(getAct(), Key.USER,getAct().getGson().toJson(user));

        getAct()            .createUser(user);
    }
}
