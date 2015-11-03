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
    public static final String TICKER = "ticker";
    public static final String TICKER_STARTED = "ticker_started";
    public static final String SAVED_GAME = "saved_game";
    public static final String LEADERBOARD_ID = "CgklxlRuNIQEAIQAQ";

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

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Cantarell-Bold.ttf");
        CustomTypeface.getInstance().registerTypeface("cantarell-bold", typeface);

        CustomTypeface.getInstance().registerTypeface("oswald-stencbab", getAssets(), "fonts/Oswald-Stencbab.ttf");
        CustomTypeface.getInstance().registerTypeface("roboto-bold", getAssets(), "fonts/Roboto-Bold.ttf");
    }
}
