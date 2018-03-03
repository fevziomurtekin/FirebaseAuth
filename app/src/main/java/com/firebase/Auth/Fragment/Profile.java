package com.kuarkdijital.fixturemakersocial.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.reflect.TypeToken;
import com.kuarkdijital.fixturemakersocial.MainActivity;
import com.kuarkdijital.fixturemakersocial.Model.Users;
import com.kuarkdijital.fixturemakersocial.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

import fevziomurtekin.Fragment.MyFragment;
import fevziomurtekin.Key;
import fevziomurtekin.Util;

/**
 * Created by omurt on 21.02.2018.
 */

public class Profile extends MyFragment implements View.OnClickListener{

    private static final int GALLERY = 1, CAMERA=2 ,RESULT_CANCELED =3;
    private static final int EXTERNAL_STORAGE = 100;
    private RoundedImageView imgAddimage;
    private TextView txtAddImage;
    private EditText edtEmail,edtUsername,edtCountry,edtCity,edtFavoriteSport,edtFavoriteTeam,edtPass,edtNewPass,edtNewPassAgain;
    private FirebaseUser user;
    private Button btnUpdate,btnRenewPass;
    private Users users;
    private RelativeLayout btnAddImage;
    private Uri filePath;

    public static Profile newInstance() {

        Bundle args = new Bundle();
        args.putInt(KEY_ID, R.layout.profile);
        Profile fragment = new Profile();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void createItems() {
        super.createItems();
        user                =  getAct().getAuth().getCurrentUser();
        imgAddimage         = (RoundedImageView) getChildItems(R.id.imgAddimage);
        txtAddImage         = (TextView) getChildItems(R.id.txtAddImage);
        edtEmail            = (EditText) getChildItems(R.id.edtEmail);
        edtUsername         = (EditText) getChildItems(R.id.edtUsername);
        edtCountry          = (EditText) getChildItems(R.id.edtCountry);
        edtCity             = (EditText) getChildItems(R.id.edtCity);
        edtFavoriteSport    = (EditText) getChildItems(R.id.edtFavoriteSport);
        edtFavoriteTeam     = (EditText) getChildItems(R.id.edtFavoriteTeam);
        edtPass             = (EditText) getChildItems(R.id.edtPassword);
        edtNewPass          = (EditText) getChildItems(R.id.edtNewPassword);
        edtNewPassAgain     = (EditText) getChildItems(R.id.edtNewPasswordAgain);
        btnUpdate           = (Button) getChildItems(R.id.btnUpdate);
        btnRenewPass        = (Button) getChildItems(R.id.btnRenewPassword);
        btnAddImage         = (RelativeLayout) getChildItems(R.id.rl_addimage);

        edtEmail            .setTypeface(getTypeFaces().ArimoR);
        edtUsername         .setTypeface(getTypeFaces().ArimoR);
        edtCountry          .setTypeface(getTypeFaces().ArimoR);
        edtCity             .setTypeface(getTypeFaces().ArimoR);
        edtFavoriteSport    .setTypeface(getTypeFaces().ArimoR);
        edtFavoriteTeam     .setTypeface(getTypeFaces().ArimoR);
        edtPass             .setTypeface(getTypeFaces().ArimoR);
        edtNewPass          .setTypeface(getTypeFaces().ArimoR);
        edtNewPassAgain     .setTypeface(getTypeFaces().ArimoR);
        btnUpdate           .setTypeface(getTypeFaces().ArimoB);
        btnRenewPass        .setTypeface(getTypeFaces().ArimoB);

        btnUpdate           .setOnClickListener(this);
        btnRenewPass        .setOnClickListener(this);
        btnAddImage         .setOnClickListener(this);

        users               = getAct().getGson().fromJson(Util.getPref(getAct(), Key.USER),new TypeToken<Users>(){}.getType());

        loadItems();

    }

    private void loadItems() {
        edtEmail            .setText(users.getEmail());
        edtUsername         .setText(users.getUsername()==null ? "" : users.getUsername());
        edtCountry          .setText(users.getCountry());
        edtCity             .setText(users.getCity());
        edtFavoriteTeam     .setText(users.getFavoriteTeam());
        edtFavoriteSport    .setText(users.getFavoriteSport());
        if(users.getPhotoUrl()!=null){
            txtAddImage     .setVisibility(View.INVISIBLE);
            Picasso         .with(getAct()).load(users.getPhotoUrl()).into(imgAddimage);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnUpdate:
                saveProfileData();
                // filePath doluysa firebase upload edilecek.
                if(filePath!=null)
                    uploadImage();
                else {
                    Intent i=new Intent(getAct(), MainActivity.class);
                    i       .putExtra(Key.INTENT,true);
                    startActivity(i);
                }
                break;
            case R.id.btnRenewPassword:
                renewPassword();
                break;
            case R.id.rl_addimage:
                selectImage();
                break;
        }
    }

    private void renewPassword() {
        // Boş bırakılmış alan var mı diye kontrol ediliyor.
        if(edtPass.getText().toString().isEmpty() ||
                edtNewPass.getText().toString().isEmpty() ||
                    edtNewPassAgain.getText().toString().isEmpty())
            getAct().showAlert();

        // Burada şifrelenmiş md5 şifre ile gireceği passwordu karşılaştırıyoruz.
        else if(!users.getPassword().equals(getAct().md5(edtPass.getText().toString())))
            getAct()        .showAlert(getString(R.string.currentpasserror));

        // Önceki şifresi girildiyse hata verecek.
        else if(edtPass.getText().toString().equals(edtNewPass.getText().toString())) {
            getAct().showAlert(getString(R.string.previouspasserror));
            if(!edtNewPass.getText().toString().equals(edtNewPassAgain.getText().toString()))
                edtNewPassAgain .setError(getString(R.string.passerror));
        }

        // yeni gireceğin şifre ile tekrar girilen yeni şifre uyuşmuyorsa hata verecek.
        else if(!edtNewPass.getText().toString().equals(edtNewPassAgain.getText().toString()))
            edtNewPassAgain .setError(getString(R.string.passerror));

        else{
            users           .setPassword(getAct().md5(edtNewPass.getText().toString()));
            getAct()        .updateUser(users);
            Util            .savePref(getAct(),Key.USER,getAct().getGson().toJson(users));
            getAct()        .succesAlert(getString(R.string.passchange),Profile.newInstance());
        }

    }

    private void saveProfileData() {
        users               .setEmail(edtEmail.getText().toString());
        users               .setUsername(edtUsername.getText().toString());
        users               .setCountry(edtCountry.getText().toString());
        users               .setCity(edtCity.getText().toString());
        users               .setFriendsNumber(0);
        users               .setFavoriteSport(edtFavoriteSport.getText().toString());
        users               .setFavoriteTeam(edtFavoriteTeam.getText().toString());


        // tekrardan güncellediğimiz verileri kaydetiyorum ve sayfayı yeniliyorum.
        Util                .savePref(getAct(),Key.USER,getAct().getGson().toJson(users));


    }

    private void selectImage() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getAct());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},EXTERNAL_STORAGE);
                                chosePhotoGallery();
                                break;
                            case 1:
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},EXTERNAL_STORAGE);
                                ActivityCompat.requestPermissions(getAct(), new String[]{Manifest.permission.CAMERA},CAMERA);
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {
        startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE),CAMERA);
    }

    private void uploadImage() {
        // Image upload to firebase.
        StorageReference storageReference   = getAct().getStorageReference().child("Profile/"+ UUID.randomUUID());
        storageReference  .putFile(filePath)
                          .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    users   .setPhotoUrl(String.valueOf(taskSnapshot.getDownloadUrl()));
                                    getAct().updateUser(users);
                                    getAct().dismissLoading();
                                    Util    .savePref(getAct(),Key.USER,getAct().getGson().toJson(users));

                                    getAct().succesAlert(getString(R.string.updatedata));
                                  // firebasedeki verileri güncelleyecek.



                              }

                          })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    getAct().dismissLoading();
                                    getAct().showAlert(getString(R.string.failureupload));
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                }
                            });


    }

    private void chosePhotoGallery() {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                filePath            = data.getData();
                try {
                    Bitmap bitmap   = MediaStore.Images.Media.getBitmap(getAct().getContentResolver(), filePath);
                    imgAddimage     . setImageBitmap(bitmap);
                    txtAddImage     . setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {

            filePath                = data.getData();
            Bitmap thumbnail        = (Bitmap) data.getExtras().get("data");
            imgAddimage             .setImageBitmap(thumbnail);
            txtAddImage             .setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("createnew","garanted");

                } else {
                    Log.e("createnew","not-garanted");
                }
                break;
            case EXTERNAL_STORAGE:
                Log.e(Key.TAG,"external izni alındı");
                break;
        }
    }
}
