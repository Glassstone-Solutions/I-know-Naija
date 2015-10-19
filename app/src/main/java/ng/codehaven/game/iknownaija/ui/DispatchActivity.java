package ng.codehaven.game.iknownaija.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cat.ppicas.customtypeface.CustomTypeface;
import cat.ppicas.customtypeface.CustomTypefaceFactory;
import ng.codehaven.game.iknownaija.Common;
import ng.codehaven.game.iknownaija.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new CustomTypefaceFactory(
                this, CustomTypeface.getInstance()));

        super.onCreate(savedInstanceState);

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


        doAnimations();

        setOnClickListiners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sp = getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);
        if (sp.getBoolean(Common.FIRST_RUN_KEY, true)) {
            init(this);
        } else {
            startActivity(new Intent(this, HomeActivity.class));
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

    private void init(Context context) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Common.FIRST_RUN_KEY, false);

        if (bootstrapDone()) {
            editor.apply();
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            retryBootStrap();
        }


    }

    private void retryBootStrap() {
//        mProgress.setVisibility(View.GONE);
    }

    private boolean bootstrapDone() {
        return false;
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
}
