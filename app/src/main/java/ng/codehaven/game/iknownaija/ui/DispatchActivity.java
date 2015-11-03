package ng.codehaven.game.iknownaija.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cat.ppicas.customtypeface.CustomTypeface;
import cat.ppicas.customtypeface.CustomTypefaceFactory;
import io.realm.Realm;
import io.realm.RealmResults;
import ng.codehaven.game.iknownaija.Common;
import ng.codehaven.game.iknownaija.R;
import ng.codehaven.game.iknownaija.models.Category;
import ng.codehaven.game.iknownaija.models.JsonAttributes;
import ng.codehaven.game.iknownaija.models.Quiz;
import ng.codehaven.game.iknownaija.models.User;

public class DispatchActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    SharedPreferences sp;
    @InjectView(R.id.view_control)
    LinearLayout mControl;
    @InjectView(R.id.txt_i)
    TextView mTxtI;
    @InjectView(R.id.txt_know)
    TextView mTxtKnow;
    @InjectView(R.id.txt_naija)
    TextView mTxtNaija;
    @InjectView(R.id.sign_in_bar)
    LinearLayout mSignUpBar;
    @InjectView(R.id.sign_out_bar)
    LinearLayout mSignOutBar;
    @InjectView(R.id.loading_bg)
    View mLoadingBg;
    @InjectView(R.id.progress)
    ProgressBar mProgress;

    @InjectView(R.id.btn_play)
    Button mPlayBtn;
    @InjectView(R.id.btn_leader_board)
    Button mLBBtn;
    @InjectView(R.id.btn_achievements)
    Button mAchieveBtn;

    private boolean animDone = false;
    private boolean animRunning = true;

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    private Realm realm;

    private Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new CustomTypefaceFactory(
                this, CustomTypeface.getInstance()));

        super.onCreate(savedInstanceState);

        realm = Realm.getInstance(this);

        mResources = getResources();

        User user = realm.where(User.class).findFirst();

        if (user == null) {
            user = new User();
            user.setScore(0);
            realm.beginTransaction();
            realm.copyToRealm(user);
            realm.commitTransaction();
        }

        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        setContentView(R.layout.activity_dispatch);
        ButterKnife.inject(this);
        mControl.setAlpha(0f);
        mTxtI.setAlpha(0f);
        mTxtKnow.setAlpha(0f);
        mTxtNaija.setAlpha(0f);
        mSignUpBar.setAlpha(0f);
        mSignOutBar.setAlpha(0f);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mGoogleApiClient != null && !mGoogleApiClient.isConnecting()) {
//            mGoogleApiClient.connect();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sp = getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);
        if (sp.getBoolean(Common.FIRST_RUN_KEY, true)) {
            init(realm);
        } else {
            if (mLoadingBg.getVisibility() == View.VISIBLE || mProgress.getVisibility() == View.VISIBLE) {
                mLoadingBg.setVisibility(View.GONE);
                mProgress.setVisibility(View.GONE);

                doAnimations();
                setOnClickListiners();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_other_error);
            }
        }
    }

    private void doAnimations() {

        if (mLoadingBg.getVisibility() == View.VISIBLE) {
            mProgress.setVisibility(View.GONE);
            ObjectAnimator bgAnim = ObjectAnimator.ofFloat(mLoadingBg, View.ALPHA, 0f);
            bgAnim.setDuration(500);
            bgAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoadingBg.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            bgAnim.start();
        }

        ObjectAnimator iAnim = ObjectAnimator.ofFloat(mTxtI, "alpha", 1f);
        ObjectAnimator knowAnim = ObjectAnimator.ofFloat(mTxtKnow, "alpha", 1f);
        ObjectAnimator naijaAnim = ObjectAnimator.ofFloat(mTxtNaija, "alpha", 1f);

        AnimatorSet set = new AnimatorSet();

        set.playSequentially(iAnim.setDuration(1000), knowAnim.setDuration(1000), naijaAnim.setDuration(1000));

        ObjectAnimator controlAnim = ObjectAnimator.ofFloat(mControl, "alpha", 1f);

        ValueAnimator naijaTextColoranim = getAnimator("#FFFFFFFF", "#FF388E3C", mTxtNaija);
        ValueAnimator iTextColoranim = getAnimator("#FFFFFFFF", "#FF388E3C", mTxtI);

        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(controlAnim.setDuration(1500), naijaTextColoranim.setDuration(1500), iTextColoranim.setDuration(1500));

        AnimatorSet set3 = new AnimatorSet();

        set3.playSequentially(set, set2);

        set3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                try {
                    mGoogleApiClient.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        set3.start();
    }

    private void setOnClickListiners() {
        mPlayBtn.setOnClickListener(this);
        mLBBtn.setOnClickListener(this);
        mAchieveBtn.setOnClickListener(this);
    }


    private void init(Realm realm) {
        SharedPreferences.Editor editor = sp.edit();

        if (bootstrapDone(realm)) {
            editor.putBoolean(Common.FIRST_RUN_KEY, false);
            editor.apply();

            doAnimations();

            setOnClickListiners();

        } else {
            mProgress.setVisibility(View.GONE);
//            retryBootStrap(realm);
        }


    }

    private void retryBootStrap(Realm realm) {
        try {
            fillCategoriesAndQuizzes(realm);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean bootstrapDone(Realm realm) {
        boolean bootDone;
        try {
            fillCategoriesAndQuizzes(realm);
            bootDone = true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            bootDone = false;
        }
        return bootDone;
    }

    private void fillCategoriesAndQuizzes(Realm realm) throws IOException, JSONException {
        // Clear DB first
        RealmResults<Category> c = realm.where(Category.class).findAll();
        realm.beginTransaction();
        c.clear();
        realm.commitTransaction();

        JSONArray jsonArray = new JSONArray(readCategoriesFromResources());
        JSONObject category;
        for (int i = 0; i < jsonArray.length(); i++) {
            category = jsonArray.getJSONObject(i);
            final String categoryId = category.getString(JsonAttributes.ID);
            fillCategory(realm, category, categoryId);
            final JSONArray quizzes = category.getJSONArray(JsonAttributes.QUIZZES);
            fillQuizzesForCategory(realm, quizzes, categoryId);
        }
    }

    private void fillCategory(Realm realm, JSONObject category, String categoryId) throws JSONException {
        Category cat = new Category();
        cat.setCatId(categoryId);
        cat.setTitle(category.getString(JsonAttributes.NAME));
        cat.setTheme(category.getString(JsonAttributes.THEME));
        cat.setSolved("");
        cat.setScores("0");

        realm.beginTransaction();
        realm.copyToRealm(cat);
        realm.commitTransaction();

    }

    private void fillQuizzesForCategory(Realm realm, JSONArray quizzes, String categoryId) throws JSONException {
        JSONObject quiz;
        for (int i = 0; i < quizzes.length(); i++) {
            quiz = quizzes.getJSONObject(i);

            Category c = realm.where(Category.class).equalTo("catId", categoryId).findFirst();

            realm.beginTransaction();
            Quiz q = new Quiz();
            q.setType(quiz.getString(JsonAttributes.TYPE));
            q.setQuestion(quiz.getString(JsonAttributes.QUESTION));
            q.setAnswer(quiz.getString(JsonAttributes.ANSWER));
            q.setOptions(quiz.getString(JsonAttributes.OPTIONS));
            q.setSolved(false);
            c.getQuizzes().add(q);
            realm.commitTransaction();
        }
    }

    private String readCategoriesFromResources() throws IOException {
        StringBuilder categoriesJson = new StringBuilder();
        InputStream rawCategories = mResources.openRawResource(R.raw.cat);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawCategories));
        String line;

        while ((line = reader.readLine()) != null) {
            categoriesJson.append(line);
        }
        return categoriesJson.toString();
    }

    @NonNull
    private ValueAnimator getAnimator(String mFrom, String mTo, final TextView tv) {
        final float[] from = new float[3],
                to = new float[3];

        Color.colorToHSV(Color.parseColor(mFrom), from);   // from white
        Color.colorToHSV(Color.parseColor(mTo), to);     // to red

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(300);                              // for 300 ms

        final float[] hsv = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                tv.setTextColor(Color.HSVToColor(hsv));
            }
        });
        return anim;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_play:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.btn_leader_board:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                            getResources().getString(R.string.lb)), 9021);
                } else {
                    showSignInBar();
                }
                break;
            case R.id.btn_achievements:
                break;
        }
    }

    private void showSignInBar() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mSignUpBar, View.ALPHA, 1f);
        anim.setDuration(1000);
        anim.start();
    }

    private void showSignOutBar() {

        mSignOutBar.setVisibility(View.VISIBLE);

        ObjectAnimator anim = ObjectAnimator.ofFloat(mSignUpBar, View.ALPHA, 0f);
        anim.setDuration(500);

        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mSignOutBar, View.ALPHA, 1f);

        AnimatorSet animSet = new AnimatorSet();

        animSet.playTogether(anim, anim2);

        animSet.start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        showSignOutBar();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            return;
        }
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error));
        }

        showSignInBar();
    }
}
