package com.kuarkdijital.fixturemakersocial;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.kuarkdijital.fixturemakersocial.Fragment.SignIn;
import com.kuarkdijital.fixturemakersocial.Model.Users;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fevziomurtekin.Key;
import fevziomurtekin.MyFragmentActivity;
import fevziomurtekin.Util;

/**
 * Created by omurt on 19.02.2018.
 */

public class SignActivity extends MyFragmentActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener firebAuthStateListener;
    private TwitterAuthClient client;
    private boolean isFacebook=false,isGoogle=false,isTwitter=false;
    private static final int RC_SIGN_IN = 777;
    private Users usersModel=new Users();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        getBase64HashKey();
        openDetail(R.id.sign_container, SignIn.newInstance());
        firebaseAuth = FirebaseAuth.getInstance();
        setupFacebook();

    }

    private void getBase64HashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.kuarkdijital.fixturemakersocial", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);

            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFacebook:
                isFacebook=true;
                LoginManager.getInstance().logInWithReadPermissions(SignActivity.this
                        , Arrays.asList("public_profile ","user_friends","email"));
                break;
            case R.id.btnTwitter:
                isTwitter=true;
                client=new TwitterAuthClient();
                client.authorize(this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        Log.e(Key.TAG,"succes-twitter");
                        signInTwitter(result.data);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.e(Key.TAG,"error-twitter");
                        //TODO hata mesajı gelecek dialog.
                    }
                });
                break;
            case R.id.btnGoogle:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(getGoogleApiClient());
                startActivityForResult(signInIntent, RC_SIGN_IN);
                firebAuthStateListener=new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user=getAuth().getCurrentUser();
                        if(user!=null){
                            startActivity(new Intent(SignActivity.this,MainActivity.class));
                        }


                    }
                };
                break;
        }
    }

    private void signInTwitter(TwitterSession data) {
        AuthCredential credential= TwitterAuthProvider.getCredential(data.getAuthToken().token,
                data.getAuthToken().secret);

        getAuth().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Log.e("twitter","task-succes");
                    FirebaseUser user=getAuth().getCurrentUser();
                    UserData(user);
                    Intent i=new Intent(SignActivity.this,MainActivity.class);
                    i.putExtra(Key.LOGİN,true);
                    startActivity(i);
                }
                else{
                    showAlert(getString(R.string.connecterror));
                }
            }
        });
    }

    private void setupFacebook() {
        LoginManager.getInstance().registerCallback(getCallbackManager(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                graphRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(Key.TAG,error.toString());
            }
        });
    }

    private void graphRequest(AccessToken accessToken) {
        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());

       getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            user= getAuth().getCurrentUser();
                            UserData(user);
                            Intent i=new Intent(SignActivity.this,MainActivity.class);
                            i.putExtra(Key.LOGİN,true);
                            startActivity(i);
                        }else
                            showAlert(getString(R.string.connecterror));
                    }
                });

        firebAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=getAuth().getCurrentUser();
                if(user!=null)
                    startActivity(new Intent(SignActivity.this,MainActivity.class));
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(isFacebook)
            getCallbackManager().onActivityResult(requestCode,resultCode,data);
        else if(isTwitter)
            client.onActivityResult(requestCode, resultCode, data);
        else if(requestCode==RC_SIGN_IN){
            Log.e(Key.TAG,"google girdi");
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInGoogle(result);
        }
    }

    private void signInGoogle(GoogleSignInResult result) {
        if(result.isSuccess()){
            AuthCredential credential= GoogleAuthProvider.getCredential(result.getSignInAccount().getIdToken(),null);
            getAuth().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        user= getAuth().getCurrentUser();
                        UserData(user);
                        Intent i=new Intent(SignActivity.this,MainActivity.class);
                        i.putExtra(Key.LOGİN,true);
                        startActivity(i);
                    }else{
                        showAlert(getString(R.string.connecterror));
                    }
                }
            });
        }
    }

    private void UserData(FirebaseUser user) {
        usersModel  .setEmail(user.getEmail());
        usersModel  .setNameSurname(user.getDisplayName());
        usersModel  .setPhotoUrl(String.valueOf(user.getPhotoUrl()));
        usersModel  .setPhoneNumber(user.getPhoneNumber());

        //user save to pref.
        Util        .savePref(this, Key.USER,getGson().toJson(usersModel));


        //Firebase userı kaydettik.
        createUser(usersModel);

    } //user verilerini kaydetiyorum.

    @Override
    protected void onStart() {
        super.onStart();
        if(firebAuthStateListener!=null)
            getAuth().addAuthStateListener(firebAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebAuthStateListener!=null)
            getAuth().removeAuthStateListener(firebAuthStateListener);
    }
}
