package ng.codehaven.game.iknownaija.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.InjectView;
import cat.ppicas.customtypeface.CustomTypeface;
import cat.ppicas.customtypeface.CustomTypefaceFactory;
import io.realm.Realm;
import io.realm.RealmList;
import ng.codehaven.game.iknownaija.Common;
import ng.codehaven.game.iknownaija.R;
import ng.codehaven.game.iknownaija.bus.BusProvider;
import ng.codehaven.game.iknownaija.bus.events.QuizBuss;
import ng.codehaven.game.iknownaija.models.Category;
import ng.codehaven.game.iknownaija.models.Quiz;
import ng.codehaven.game.iknownaija.models.User;
import ng.codehaven.game.iknownaija.ui.adapters.AnswerAdapter;
import ng.codehaven.game.iknownaija.ui.adapters.AnswerAdapter.AnswerInterface;
import ng.codehaven.game.iknownaija.utils.NetworkHelper;

public class GameActivity extends BaseActivity implements OnClickListener, AnswerInterface, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = GameActivity.class.getSimpleName();

    // The AppState slot we are editing.  For simplicity this sample only manipulates a single
    // Cloud Save slot and a corresponding Snapshot entry,  This could be changed to any integer
    // 0-3 without changing functionality (Cloud Save has four slots, numbered 0-3).
    private static final int APP_STATE_KEY = 0;

    // Request code used to invoke sign-in UI.
    private static final int RC_SIGN_IN = 9001;

    // Request code used to invoke Snapshot selection UI.
    private static final int RC_SELECT_SNAPSHOT = 9002;

    /// Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    // Progress Dialog used to display loading messages.
    private ProgressDialog mProgressDialog;

    // True when the application is attempting to resolve a sign-in error that has a possible
    // resolution,
    private boolean mIsResolving = false;

    // True immediately after the user clicks the sign-in button/
    private boolean mSignInClicked = false;

    // True if we want to automatically attempt to sign in the user at application start.
    private boolean mAutoStartSignIn = true;

    @InjectView(R.id.txt_countdown)
    TextView mCountdownView;
    @InjectView(R.id.txt_question)
    TextView mQuestion;
    @InjectView(R.id.view_answers)
    RecyclerView mRecycler;
    @InjectView(R.id.fab)
    FloatingActionButton mFab;
    int mCount = 14;
    int tick = 15000;
    boolean counterStarted, answered, timerDone;
    CountDownTimer timer;
    String cat;
    Quiz q;
    AnswerAdapter adapter = null;
    private Bus mBus = BusProvider.getInstance();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new CustomTypefaceFactory(
                this, CustomTypeface.getInstance()));
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);

        cat = getIntent().getStringExtra("category");

        realm = Realm.getInstance(this);

        user = realm.where(User.class).findFirst();

        Category c = realm.where(Category.class).equalTo(Category.CAT_ID, cat).findFirst();

        RealmList<Quiz> question = c.getQuizzes();

        q = null;

        for (Quiz qq : question) {
            if (!qq.isSolved()) {
                q = qq;
                break;
            }
        }

        assert getSupportActionBar() != null;
        assert q != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (q != null) {
            mQuestion.setText(q.getQuestion());
            try {
                adapter = new AnswerAdapter(this, new JSONArray(q.getOptions()));
                adapter.setAnswerInterface(this);
            } catch (JSONException e) {
                e.printStackTrace();
                adapter = null;
            }
            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mRecycler.setAdapter(adapter);
        } else {
            Intent i = new Intent(this, HomeActivity.class);
            i.putExtra("solved", true);
            i.putExtra("cat", cat);
            startActivity(i);
            finish();
        }



        // Build API client with access to Games, AppState, and SavedGames.
        // It is very important to add Drive or the SavedGames API will not work
        // Make sure to also go to console.developers.google.com and enable the Drive API for your
        // project
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES) // Games
                .addScope(Drive.SCOPE_APPFOLDER) // SavedGames
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
//        updateUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        counterStarted = sp.getBoolean(Common.TICKER_STARTED, false);

        if (counterStarted && cat.equals(sp.getString("cat", ""))) {
            mCount = sp.getInt(Common.TICKER, 15000);
        } else {
            mCount = 15000;
        }

        timer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCountdownView.setText(String.valueOf(millisUntilFinished / 1000));
                mCount = (int) millisUntilFinished;
            }

            @Override
            public void onFinish() {
                timerDone = true;
            }
        };

        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cat", getIntent().getStringExtra("category"));
        if (timerDone) {
            editor.putBoolean(Common.TICKER_STARTED, false);
            editor.putInt(Common.TICKER, 15000);
        } else {
            editor.putBoolean(Common.TICKER_STARTED, true);
            editor.putInt(Common.TICKER, mCount);
        }
        editor.apply();
    }

    @Override
    public int contentId() {
        return R.layout.activity_game;
    }

    @Override
    public boolean hasToolBar() {
        return true;
    }

    @Override
    public boolean hasTitle() {
        return true;
    }

    @Override
    public int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    public boolean hasFAB() {
        return false;
    }

    @Override
    public int FAB() {
        return 0;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onAnswerClick(int position) {
        timer.cancel();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cat", getIntent().getStringExtra("category"));
        editor.putBoolean(Common.TICKER_STARTED, false);
        editor.putInt(Common.TICKER, 15000);
        editor.apply();

        if (!answered) {
            try {
                JSONArray j = new JSONArray(q.getAnswer());
                if (j.getInt(0) == position) {
                    doCorrectAnim(position);
                    Log.e("CORRECT", "Corret");
                } else {
                    doFailAnimation(position, j.getInt(0));
                    Log.e("BUST", "Corret");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            realm.beginTransaction();
            q.setSolved(true);
            realm.commitTransaction();
            answered = true;
        }

    }

    private void doFailAnimation(int clickedPosition, int answerPosition) {
        adapter.updateAnswerWithFail(clickedPosition, answerPosition);
        int score = 0;
        mCountdownView.setText(String.valueOf(score));
        doFabAnim(score);
    }

    private void doCorrectAnim(int position) {
        adapter.updateAnswer(position);
        int score = mCount * 5;
        mCountdownView.setText(String.format("%s points", String.valueOf(score / 1000)));
        mCountdownView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        doFabAnim(score);
    }

    private void doFabAnim(int score) {
        if (NetworkHelper.isOnline(this)){
            updateScoreOnline(score);
        } else {
            updateScore(score);
        }
        mFab.animate()
                .scaleX(1)
                .scaleY(1)
                .setStartDelay(400)
                .start();
        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBus.post(new QuizBuss(cat));
                Intent i = new Intent(GameActivity.this, GameActivity.class);
                i.putExtra("category", cat);
                startActivity(i);
            }
        });
    }

    private void updateScore(int score) {
        int currentScore = user.getScore();
        int newScore = currentScore + score;

        realm.beginTransaction();
        user.setScore(newScore);
        realm.commitTransaction();
    }

    private void updateScoreOnline(int score) {
        updateScore(score);
        int getScore = user.getScore();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
