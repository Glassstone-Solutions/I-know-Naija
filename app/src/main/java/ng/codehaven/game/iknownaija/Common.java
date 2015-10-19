package ng.codehaven.game.iknownaija;

import android.app.Application;
import android.graphics.Typeface;

import cat.ppicas.customtypeface.CustomTypeface;
import ng.codehaven.game.iknownaija.ui.views.TextField;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Application Class
 */
public class Common extends Application {

    public static final String SHARED_PREF = "user_pref";
    public static final String FIRST_RUN_KEY = "first_key";
    public static final String G_PLUS_SIGNED_IN = "g+";

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {

        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-ThinItalic.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .addCustomStyle(TextField.class, R.attr.textFieldStyle)
                        .build()
        );

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Cantarell-Bold.ttf");
        CustomTypeface.getInstance().registerTypeface("cantarell-bold", typeface);

        CustomTypeface.getInstance().registerTypeface("oswald-stencbab", getAssets(), "fonts/Oswald-Stencbab.ttf");
    }
}
