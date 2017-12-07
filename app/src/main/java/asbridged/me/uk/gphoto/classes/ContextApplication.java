package asbridged.me.uk.gphoto.classes;

import android.app.Application;
import android.content.Context;

/**
 * Created by AsbridgeD on 07-Dec-17.
 * Axtends application to store context. Used in static OptionContent class
 * to access string resources
 * N.b. change to manivest file name=".classes.ContextApplication"
 */

public class ContextApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
