package fevziomurtekin;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by omurt on 19.02.2018.
 */

public class Util {
    private static Gson gson;

    public static Gson getGson() {
        if(gson==null)
            gson=new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
        return gson;
    }

    public static String getTarihSaat(Date tarih){
        DateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  format.format(tarih);
    }

    public static void savePref(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }
    public static void removePref(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply();
    }
    public static String  getPref(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public static boolean hasPref(Context context,String keyUser) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(keyUser);
    }

    public static void hideKeyboard(Activity context, View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public static void hideKeyboard(Activity context){
        try {
            hideKeyboard(context, context.getCurrentFocus());
        }catch (Exception e){}
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator createCenteredReveal(View view) {
        // Could optimize by reusing a temporary Rect instead of allocating a new one
        Rect bounds = new Rect();
        view.getDrawingRect(bounds);
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();
        int finalRadius = Math.max(bounds.width(), bounds.height());
        return ViewAnimationUtils.createCircularReveal(view, centerX, centerY, finalRadius, 0f);
    }

    public static boolean isInternetAvailable(Context context) {
        if(context==null)return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork!=null&&activeNetwork.isConnected())
            return true;
        return false;
    }

}
