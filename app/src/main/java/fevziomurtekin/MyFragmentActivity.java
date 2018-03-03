package fevziomurtekin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kuarkdijital.fixturemakersocial.Fragment.SignIn;
import com.kuarkdijital.fixturemakersocial.MainActivity;
import com.kuarkdijital.fixturemakersocial.Model.Fixtures;
import com.kuarkdijital.fixturemakersocial.Model.Users;
import com.kuarkdijital.fixturemakersocial.R;
import com.kuarkdijital.fixturemakersocial.Retro.RetrofitInterface;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by omurt on 19.02.2018.
 */

public class MyFragmentActivity extends AppCompatActivity implements DialogInterface.OnShowListener, GoogleApiClient.OnConnectionFailedListener {

    private RetrofitInterface mApi;
    private ProgressDialog mDialog;
    private Retrofit retrofit,retrofitKuark;
    private DisplayMetrics metrics = new DisplayMetrics(); // ekranın boyutunu ayarlayan metot.
    private float mheight,mweight,scale,mwidth;
    private Gson gson = new Gson();
    public Typefaces typefaces=new Typefaces();
    private boolean isDestroyed = true;
    private String credentials;
    private Context context=this;
    private CallbackManager callbackManager;
    private Dialog dialog;
    private FirebaseAuth firebaseAuth;
    private TwitterAuthConfig authConfig;
    private TwitterConfig twitterConfig;
    private GoogleSignInOptions gso;
    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount account;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Users profile = new Users();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth=FirebaseAuth.getInstance();

        mFirebaseDatabase=FirebaseDatabase.getInstance();

        firebaseStorage     = FirebaseStorage.getInstance();
        storageReference    = firebaseStorage.getReference();

         /* Set up twitter auth */
        authConfig=new TwitterAuthConfig(getString(R.string.twitter_consumer_id),getString(R.string.twitter_secret_id));
        twitterConfig=new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);

        /* Set up google auth */
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        isDestroyed = false;
        gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Key.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mApi = retrofit.create(RetrofitInterface.class);

        profile = getGson().fromJson(Util.getPref(getApplicationContext(),Key.USER),new TypeToken<Users>(){}.getType());

        SetDisplaySizes();
        typefaces.createTypeface(this);
        int theme = Build.VERSION.SDK_INT >= 23 ? android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar : ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT;
        mDialog = new ProgressDialog(this, theme);
        mDialog.setOnShowListener(this);

    }

    public void openDetail(int holderId, Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
                .replace(holderId,fragment,fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();
    }

    private void SetDisplaySizes() {
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mheight = metrics.heightPixels;
        mweight = metrics.widthPixels;
        scale = metrics.density;
    }

    public void createUser(Users users){

        DatabaseReference root  =mFirebaseDatabase.getReference("Users"); // tablo ismi
        users                   .setKey(root.push().getKey().replace("-",""));
        DatabaseReference child =mFirebaseDatabase.getReference("Users/"+users.getKey());
        // eklenecek childlerin ismi.
        child                   .setValue(users);
        // users verilerini ekledik.
    }

    public void createFixture(Fixtures fixtures){

        DatabaseReference root  =mFirebaseDatabase.getReference("Fixtures"); // tablo ismi
        fixtures                .setFixtureCode(root.push().getKey().replace("-",""));
        DatabaseReference child =mFirebaseDatabase.getReference("Fixtures/"+fixtures.getFixtureCode());
        // eklenecek childlerin ismi.
        child                   .setValue(fixtures);
        // users verilerini ekledik.
    }
    public void updateUser(Users users){

        //eklediğimiz keyi alıp ona göre değiştiriyoruz.
        DatabaseReference root              = mFirebaseDatabase.getReference("Users");
        Map<String,Object> userUpdates      = new HashMap<>();
        userUpdates                         .put(users.getKey(),users);
        root                                .updateChildren(userUpdates);

    }

    public void haveRecord(final String email, final String password){
        DatabaseReference drf=mFirebaseDatabase.getReference("Users");
        Util.savePref(getApplicationContext(),Key.SUCCES,"false");
        drf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot gelenler : dataSnapshot.getChildren()){
                    String md5= md5(password);
                    for(DataSnapshot gelen : dataSnapshot.getChildren()){
                        if(gelen.getValue(Users.class).getPassword()!=null) {
                            String emails = gelen.getValue(Users.class).getEmail();
                            String mdfive= gelen.getValue(Users.class).getPassword();
                            if (emails.equals(email) && mdfive.equals(md5)){
                                Users users=new Users();
                                users   .setUsername(gelen.getValue(Users.class).getUsername());
                                users   .setPassword(gelen.getValue(Users.class).getPassword());
                                users   .setEmail(gelen.getValue(Users.class).getEmail());
                                users   .setKey(gelen.getValue(Users.class).getKey());
                                users   .setFriendsNumber(gelen.getValue(Users.class).getFriendsNumber());
                                Util    .savePref(getApplicationContext(),Key.USER,getGson().toJson(users));
                                Util.savePref(getApplicationContext(),Key.SUCCES,"true");
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public FirebaseDatabase getmFirebaseDatabase() {
        return mFirebaseDatabase;
    }

    public View getChild(int id) {
        return findViewById(id);
    }

    public float getHeight() {
        return mheight;
    }

    public float getWidth() {
        return mweight;
    }

    public float getScale() {
        return scale;
    }

    public DisplayMetrics getMetrics() {
        return metrics;
    }

    public Typefaces getTypeFaces() {
        return typefaces;
    }

    public String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Gson getGson() {
        return gson;
    }

    @Override
    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT > 16)
            return super.isDestroyed();
        else return isDestroyed;

    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void animate(final View view) {
        AnimatorSet set = new AnimatorSet();
        view.setPivotX(.5f * getWidth());
        view.setPivotY(getHeight());
        ObjectAnimator anim3, anim2, anim1 = ObjectAnimator.ofFloat(view, "scaleX", .5f, 1f);
        anim2 = ObjectAnimator.ofFloat(view, "scaleY", .5f, 1f);
        anim3 = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
       /* anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.getParent().requestLayout();
            }
        });*/
        set.playTogether(anim1, anim2, anim3);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        set.setDuration(300).setInterpolator(new AccelerateInterpolator());
        set.start();
        /*Animation animation= AnimationUtils.loadAnimation(this,R.anim.slide_in_right1);
        animation.setFillAfter(true);
        view.setVisibility(View.VISIBLE);
        animation.start();*/

    }
    public void setLoadingMessage(String message) {
        mDialog.setMessage(message);
    }

    public void showLoading() {
        try {
            if (!mDialog.isShowing()) mDialog.show();
        } catch (Exception e) {

        }
    }

    public void showLoading(String msg) {
        try {
            if (!mDialog.isShowing()) {
                mDialog.setMessage(msg);
                mDialog.show();
            }
        } catch (Exception e) {

        }
    }

    public void dismissLoading() {
        try {
            mDialog.dismiss();
        } catch (Exception e) {

        }
    }

    public FirebaseStorage getFirebaseStorage() {
        return firebaseStorage;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public RetrofitInterface getApi() {
        return mApi;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        ((ProgressBar) mDialog.findViewById(android.R.id.progress)).getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        ((TextView) mDialog.findViewById(android.R.id.message)).setTypeface(typefaces.ArimoB);
        ((TextView) mDialog.findViewById(android.R.id.message)).setTextColor(getResources().getColor(R.color.textColor));

    }
    public FirebaseAuth getAuth() {
        return firebaseAuth;
    }

    public Context getContext() {
        return context;
    }

    public void showForgotDialog(final RelativeLayout relativeLayout, SignIn signInFragment) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forget_pass);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btnSend=dialog.findViewById(R.id.btnSend);
        Button btnCancel=dialog.findViewById(R.id.btnCancel);
        TextView txtTitle=dialog.findViewById(R.id.forgetpass);
        EditText edtEmail=dialog.findViewById(R.id.edtEmail);
        edtEmail.setTypeface(getTypeFaces().ArimoR);
        txtTitle.setTypeface(getTypeFaces().ArimoB);
        btnSend.setTypeface(getTypeFaces().ArimoB);
        btnCancel.setTypeface(getTypeFaces().ArimoB);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.setAlpha(1);
                dialog.dismiss();
            }
        });

        btnSend.setTag(edtEmail.getText().toString());
        btnSend.setOnClickListener(signInFragment);

        dialog.show();
    }

    public void showAlert() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView btnOk=dialog.findViewById(R.id.btnOk);
        TextView txtMessage=dialog.findViewById(R.id.txtMessage);
        TextView txtTitle=dialog.findViewById(R.id.txtTitle);
        txtTitle.setTypeface(getTypeFaces().ArimoB);
        txtMessage.setTypeface(getTypeFaces().ArimoR);
        btnOk.setTypeface(getTypeFaces().ArimoB);
        txtMessage.setText(getString(R.string.mustfields));
        txtTitle.setText(getString(R.string.error));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showAlert(String message) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView btnOk=dialog.findViewById(R.id.btnOk);
        TextView txtMessage=dialog.findViewById(R.id.txtMessage);
        TextView txtTitle=dialog.findViewById(R.id.txtTitle);
        txtTitle.setTypeface(getTypeFaces().ArimoB);
        txtMessage.setTypeface(getTypeFaces().ArimoR);
        btnOk.setTypeface(getTypeFaces().ArimoB);
        txtMessage.setText(message);
        txtTitle.setText(getString(R.string.error));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void succesAlert(String message, final Fragment fragment) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView btnOk=dialog.findViewById(R.id.btnOk);
        TextView txtMessage=dialog.findViewById(R.id.txtMessage);
        TextView txtTitle=dialog.findViewById(R.id.txtTitle);
        txtTitle.setTypeface(getTypeFaces().ArimoB);
        txtMessage.setTypeface(getTypeFaces().ArimoR);
        btnOk.setTypeface(getTypeFaces().ArimoB);
        txtMessage.setText(message);
        txtTitle.setText(getString(R.string.succes));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openDetail(R.id.main_container,fragment);
            }
        });

        dialog.show();
    }

    public void succesAlert(String message) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView btnOk=dialog.findViewById(R.id.btnOk);
        TextView txtMessage=dialog.findViewById(R.id.txtMessage);
        TextView txtTitle=dialog.findViewById(R.id.txtTitle);
        txtTitle.setTypeface(getTypeFaces().ArimoB);
        txtMessage.setTypeface(getTypeFaces().ArimoR);
        btnOk.setTypeface(getTypeFaces().ArimoB);
        txtMessage.setText(message);
        txtTitle.setText(getString(R.string.succes));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                i       .putExtra(Key.INTENT,true);
                startActivity(i);
            }
        });

        dialog.show();
    }

    public Users getProfile() {
        return profile;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //TODO popuplar eklendikce dialogları girilecek.
}
