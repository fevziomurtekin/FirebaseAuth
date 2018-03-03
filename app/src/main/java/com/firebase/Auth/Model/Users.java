package com.firebase.auth.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by omurt on 27.02.2018.
 */

public class Users implements Serializable {

    @SerializedName("Email")
    @Expose
    private String Email;

    @SerializedName("Username")
    @Expose
    private String Username;

    @SerializedName("nameSurname")
    @Expose
    private String nameSurname;

    @SerializedName("Password")
    @Expose
    private String Password;

    @SerializedName("Country")
    @Expose
    private String Country;

    @SerializedName("City")
    @Expose
    private String City;

    @SerializedName("FavoriteSport")
    @Expose
    private String FavoriteSport;

    @SerializedName("FavoriteTeam")
    @Expose
    private String FavoriteTeam;

    @SerializedName("PhotoUrl")
    @Expose
    private String PhotoUrl;

    @SerializedName("PhoneNumber")
    @Expose
    private String PhoneNumber;

    @SerializedName("GenerateId")
    @Expose
    private String GenerateId;

    @SerializedName("FriendsNumber")
    @Expose
    private int FriendsNumber=0;

    @SerializedName("Key")
    @Expose
    private String Key;

    @SerializedName("MyTeams")
    @Expose
    private List<Teams> MyTeams;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getFavoriteSport() {
        return FavoriteSport;
    }

    public void setFavoriteSport(String favoriteSport) {
        FavoriteSport = favoriteSport;
    }

    public String getFavoriteTeam() {
        return FavoriteTeam;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setFavoriteTeam(String favoriteTeam) {
        FavoriteTeam = favoriteTeam;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getGenerateId() {
        return GenerateId;
    }

    public void setGenerateId(String generateId) {
        GenerateId = generateId;
    }

    public int getFriendsNumber() {
        return FriendsNumber;
    }

    public void setFriendsNumber(int friendsNumber) {
        FriendsNumber = friendsNumber;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getKey() {
        return Key;
    }

    public List<Teams> getMyTeams() {
        return MyTeams;
    }

    public void setMyTeams(List<Teams> myTeams) {
        MyTeams = myTeams;
    }
}
