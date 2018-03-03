package fevziomurtekin;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by omurt on 19.02.2018.
 */

public class Typefaces {

    public Typeface ArimoB,ArimoBI,ArimoI,ArimoR;

    public Typefaces createTypeface(Context context){

        ArimoB=Typeface.createFromAsset(context.getAssets(),"fonts/ArimoBold.ttf");
        ArimoBI=Typeface.createFromAsset(context.getAssets(),"fonts/ArimoBoldItalic.ttf");
        ArimoI=Typeface.createFromAsset(context.getAssets(),"fonts/ArimoItalic.ttf");
        ArimoR=Typeface.createFromAsset(context.getAssets(),"fonts/ArimoRegular.ttf");

        return this;
    }

}
