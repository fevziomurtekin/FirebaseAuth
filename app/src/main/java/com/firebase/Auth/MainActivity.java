package com.kuarkdijital.fixturemakersocial;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kuarkdijital.fixturemakersocial.Fragment.CreateNewFixture;
import com.kuarkdijital.fixturemakersocial.Fragment.Home;
import com.kuarkdijital.fixturemakersocial.Fragment.MyFixture;
import com.kuarkdijital.fixturemakersocial.Fragment.MyHowtodo;
import com.kuarkdijital.fixturemakersocial.Fragment.MyNotifications;
import com.kuarkdijital.fixturemakersocial.Fragment.MyUseCode;
import com.kuarkdijital.fixturemakersocial.Fragment.Profile;
import com.kuarkdijital.fixturemakersocial.Fragment.SearchFriends;
import com.kuarkdijital.fixturemakersocial.Fragment.SearchTeam;
import com.kuarkdijital.fixturemakersocial.Model.Users;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import fevziomurtekin.Key;
import fevziomurtekin.MyFragmentActivity;
import fevziomurtekin.Util;

public class MainActivity extends MyFragmentActivity implements View.OnClickListener, SlidingMenu.OnClosedListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SlidingMenu menu;
    private View menuView;
    private ImageView btnYenile;
    private RoundedImageView imgMenu,imgToolbar;
    private TextView txtUsername,txtFriends,txtFriendsNumber,txtId,txtIds,btnSignout,btnChangelang,txtProfile,txtMyFix,txtCreateFix,txtNotification,txtNotNmbr,txtUseCode,txtHowtodo;
    private RelativeLayout btnProfile,btnMyfix,btnCreateFix,btnNotification,btnUseCode,btnHowtodo;
    private Fragment onCloseFragment;
    private Users users;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSlidingMenu(); //sliding menuyu oluşturuyoruz.
        createItems();
    }

    private void createSlidingMenu() {
        menuView=getLayoutInflater().inflate(R.layout.slidingmenu,null,false);
        setUpMenu((ViewGroup) menuView);
        menu= new SlidingMenu(this);
        Configuration config = getResources().getConfiguration();
        if(Build.VERSION.SDK_INT>16 && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL)
            menu.setMode(SlidingMenu.RIGHT);
        else
            menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidth(0);
        menu.setBehindOffset((int) (getWidth() * .25));  // %25 acılıyor.
        menu.setFadeDegree(0);
        menu.setBackgroundColor(getResources().getColor(R.color.textColor));
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(menuView);
        menu.setOnClosedListener(this);
        menu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale=(1-(percentOpen*.1f));
                //Util.log("scale=%s",scale);
                menu.getContent().setScaleX(scale);
                menu.getContent().setScaleY(scale);
            }
        });

    }

    private void setUpMenu(ViewGroup menuView) {

        imgMenu         =   menuView.findViewById(R.id.imgMenu);
        btnYenile       =   menuView.findViewById(R.id.btnYenile);
        txtUsername     =   menuView.findViewById(R.id.txtMenuUsername);
        txtFriends      =   menuView.findViewById(R.id.txtMenuFriend);
        txtFriendsNumber=   menuView.findViewById(R.id.txtMenumbr);
        txtId           =   menuView.findViewById(R.id.txtId);
        txtIds          =   menuView.findViewById(R.id.txtIds);
        btnProfile      =   menuView.findViewById(R.id.btnProfile);
        btnMyfix        =   menuView.findViewById(R.id.btnMyFixture);
        btnCreateFix    =   menuView.findViewById(R.id.btnCreateNew);
        btnNotification =   menuView.findViewById(R.id.btnNotification);
        btnUseCode      =   menuView.findViewById(R.id.btnUseCode);
        btnHowtodo      =   menuView.findViewById(R.id.btnHowtodo);
        btnSignout      =   menuView.findViewById(R.id.btnSignOut);
        btnChangelang   =   menuView.findViewById(R.id.btnChangeLang);
        txtProfile      =   menuView.findViewById(R.id.txtProfile);
        txtMyFix        =   menuView.findViewById(R.id.txtMyFix);
        txtCreateFix    =   menuView.findViewById(R.id.txtCreatenewFixture);
        txtNotification =   menuView.findViewById(R.id.txtNotification);
        txtNotNmbr      =   menuView.findViewById(R.id.txtNotificationNumber);
        txtUseCode      =   menuView.findViewById(R.id.txtUseCode);
        txtHowtodo      =   menuView.findViewById(R.id.txthowtodo);
        imgToolbar      =   (RoundedImageView) getChild(R.id.imgUserPhoto);


        txtUsername     .setTypeface(getTypeFaces().ArimoB);
        txtFriends      .setTypeface(getTypeFaces().ArimoR);
        txtFriendsNumber.setTypeface(getTypeFaces().ArimoB);
        txtId           .setTypeface(getTypeFaces().ArimoR);
        txtIds          .setTypeface(getTypeFaces().ArimoB);
        txtProfile      .setTypeface(getTypeFaces().ArimoB);
        txtMyFix        .setTypeface(getTypeFaces().ArimoB);
        txtCreateFix    .setTypeface(getTypeFaces().ArimoB);
        txtNotification .setTypeface(getTypeFaces().ArimoB);
        txtNotNmbr      .setTypeface(getTypeFaces().ArimoB);
        txtUseCode      .setTypeface(getTypeFaces().ArimoB);
        txtHowtodo      .setTypeface(getTypeFaces().ArimoB);
        btnSignout      .setTypeface(getTypeFaces().ArimoB);
        btnChangelang   .setTypeface(getTypeFaces().ArimoB);

        btnProfile      .setOnClickListener(this);
        btnMyfix        .setOnClickListener(this);
        btnCreateFix    .setOnClickListener(this);
        btnNotification .setOnClickListener(this);
        btnUseCode      .setOnClickListener(this);
        btnHowtodo      .setOnClickListener(this);
        btnSignout      .setOnClickListener(this);
        btnChangelang   .setOnClickListener(this);
        txtFriends      .setOnClickListener(this);
        imgToolbar      .setOnClickListener(this);
        imgMenu         .setOnClickListener(this);
        txtUsername     .setOnClickListener(this);
        btnYenile       .setOnClickListener(this);

        //Verileri kaydetmek için yapıyoruz.
        if(Util.getPref(getApplicationContext(),Key.USER).length()==0)
            startActivity(new Intent(this, SignActivity.class));
        else {
            if(getIntent().getBooleanExtra(Key.INTENT,false))
                openDetail(R.id.main_container, Profile.newInstance());
            else
                openDetail(R.id.main_container,Home.newInstance());
        }
        // Eğer giriş yapıldıysa,profil fragmentine girecek.
        if(getIntent().getBooleanExtra(Key.LOGİN,false))
            openDetail(R.id.main_container,Profile.newInstance());
    }

    private long getGeneratedNumber() {
        return (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
    }

    private void createItems() {
        users=getGson().fromJson(Util.getPref(this,Key.USER),new TypeToken<Users>(){}.getType());
        saveUser(users);
    }

    private void saveUser(Users user) {
        //TODO save user.
        if(users.getPhotoUrl()!=null){
            Picasso         .with(getApplicationContext()).load(users.getPhotoUrl()).into(imgMenu);
            Picasso         .with(getApplicationContext()).load(users.getPhotoUrl()).into(imgToolbar);
        }else{
            imgMenu         .setImageResource(R.drawable.addteamicon);
            imgToolbar      .setImageResource(R.drawable.addteamicon);
        }

        txtUsername.setText(users.getUsername());
        if(users.getGenerateId()==null) {
            users           .setGenerateId(String.valueOf(getGeneratedNumber()));
            Util            .savePref(this,Key.USER,getGson().toJson(users));
            txtIds          .setText(users.getGenerateId());
        }else{
            txtIds          .setText(users.getGenerateId());
            txtFriendsNumber.setText(String.valueOf(users.getFriendsNumber()));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProfile:
                openDetail(R.id.main_container, Profile.newInstance());
                menu.showContent(true);
                //TODO profile
                break;
            case R.id.btnMyFixture:
                //TODO
                menu.showContent(true);
                //TODO myfix
                break;
            case R.id.btnCreateNew:
                openDetail(R.id.main_container, CreateNewFixture.newInstance());
                menu.showContent(true);
                //TODO createNewFix
                break;
            case R.id.btnNotification:
                openDetail(R.id.main_container, MyNotifications.newInstance());
                menu.showContent(true);
                //TODO Notifications
                break;
            case R.id.btnUseCode:
                openDetail(R.id.main_container, MyUseCode.newInstance());
                menu.showContent(true);
                //TODO useCode
                break;
            case R.id.btnHowtodo:
                openDetail(R.id.main_container, MyHowtodo.newInstance());
                menu.showContent(true);
                //TODO how to do
                break;
            case R.id.btnSignOut:
                LoginManager.getInstance().logOut(); //facebookta loginden sonra logout yapıyor.
                getAuth().signOut();
                Auth.GoogleSignInApi.signOut(getGoogleApiClient()).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.e(Key.TAG,"google-cikis yapildi");
                    }
                });
                Util.removePref(getApplicationContext(),Key.USER);
                startActivity(new Intent(this,MainActivity.class));
                //TODO signout
                break;
            case R.id.btnChangeLang:
                //TODO changeLang
                break;
            case R.id.btnYenile:
                users .setGenerateId(String.valueOf(getGeneratedNumber()));
                Util  .savePref(this, Key.USER, getGson().toJson(users));
                txtIds.setText(users.getGenerateId());
                break;
            case R.id.txtMenuFriend:
                openDetail(R.id.main_container, MyHowtodo.newInstance());
                menu.showContent(true);
                //TODO friend
                break;
            case R.id.btnAddUserOne:
                openDetail(R.id.main_container, SearchFriends.newInstance());
                break;
            case R.id.btnAddUserTwo:
                openDetail(R.id.main_container,SearchFriends.newInstance());
                break;
            case R.id.btnCContinue:
                //TODO save
                break;
            case R.id.btnContinue:
                //TODO save
                break;
            case R.id.btnAddTeam:
                openDetail(R.id.main_container, SearchTeam.newInstance());
                break;
            case R.id.imgUserPhoto:
                menu.toggle(); // menuyu acıyor.(toggle=geçiş)
                break;
            case R.id.imgMenu:
                startActivity(new Intent(this,MainActivity.class));
                menu.toggle();
                break;
            case R.id.txtMenuUsername:
                startActivity(new Intent(this,MainActivity.class));
                menu.toggle();
                break;
        }
    }

    @Override
    public void onClosed() {
        menu.getContent().setScaleX(1);
        menu.getContent().setScaleY(1);
        if(onCloseFragment!=null){
            openDetail(R.id.main_container,onCloseFragment);
            onCloseFragment=null;
        }

    }
}
