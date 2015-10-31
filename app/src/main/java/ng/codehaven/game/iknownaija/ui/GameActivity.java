package ng.codehaven.game.iknownaija.ui;

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
import ng.codehaven.game.iknownaija.ui.adapters.AnswerAdapter;
import ng.codehaven.game.iknownaija.ui.adapters.AnswerAdapter.AnswerInterface;

public class GameActivity extends BaseActivity implements OnClickListener, AnswerInterface {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new CustomTypefaceFactory(
                this, CustomTypeface.getInstance()));
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);

        cat = getIntent().getStringExtra("category");

        realm = Realm.getInstance(this);

        Category c = realm.where(Category.class).equalTo(Category.CAT_ID, cat).findFirst();

        RealmList<Quiz> question = c.getQuizzes();

        q = null;

        for (Quiz qq : question) {
            if (!qq.isSolved()) {
                q = qq;
                break;
            }
        }

        if (q != null) {
            mQuestion.setText(q.getQuestion());
        } else {
            Intent i = new Intent(this, HomeActivity.class);
            i.putExtra("solved", true);
            i.putExtra("cat", cat);
            startActivity(i);
            finish();
        }

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            adapter = new AnswerAdapter(this, new JSONArray(q.getOptions()));
            adapter.setAnswerInterface(this);
        } catch (JSONException e) {
            e.printStackTrace();
            adapter = null;
        }
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(adapter);

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

        timer = new CountDownTimer(mCount, 1000) {
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
    }

    private void doCorrectAnim(int position) {
        adapter.updateAnswer(position);
        int score = mCount * 5;
        mCountdownView.setText(String.format("%s points", String.valueOf(score / 1000)));
        mCountdownView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
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

}
