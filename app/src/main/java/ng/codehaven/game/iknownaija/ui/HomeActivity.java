package ng.codehaven.game.iknownaija.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.Snapshots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cat.ppicas.customtypeface.CustomTypeface;
import cat.ppicas.customtypeface.CustomTypefaceFactory;
import io.realm.Realm;
import io.realm.RealmResults;
import ng.codehaven.game.iknownaija.R;
import ng.codehaven.game.iknownaija.models.JsonAttributes;
import ng.codehaven.game.iknownaija.models.Quiz;
import ng.codehaven.game.iknownaija.ui.adapters.CategoriesAdapter;
import ng.codehaven.game.iknownaija.models.Category;

public class HomeActivity extends BaseActivity implements
        OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, CategoriesAdapter.OnCategoryClickInteraction {

    public static final String TAG = HomeActivity.class.getSimpleName();

    @InjectView(R.id.rv_cat)RecyclerView mRecycler;

    private byte[] mSaveGameData;

    /// Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    private Realm realm;

    private AsyncTask<Void, Void, Integer> loadFromSnapshot(Context context){
        return new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                Snapshots.OpenSnapshotResult result = Games.Snapshots.open(mGoogleApiClient, "savedGame", true).await();
                if (result.getStatus().isSuccess()){
                    Snapshot snapshot = result.getSnapshot();
                    try {
                        mSaveGameData = snapshot.getSnapshotContents().readFully();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Error while loading "+ result.getStatus().getStatusCode());
                }
                return result.getStatus().getStatusCode();
            }

            /**
             * Runs on the UI thread before {@link #doInBackground}.
             *
             * @see #onPostExecute
             * @see #doInBackground
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p/>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param integer The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (mSaveGameData != null){
                    try {
                        compareWithLocalData(mSaveGameData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void compareWithLocalData(byte[] mSaveGameData) throws JSONException {
        JSONArray ja = new JSONArray(new String(mSaveGameData));
        JSONObject category;

        for (int i = 0; i < ja.length(); i++){
            category = ja.getJSONObject(i);
            final String categoryId = category.getString(JsonAttributes.ID);
            Category c = realm.where(Category.class).equalTo(Category.CAT_ID, categoryId).findFirst();
            if (c == null){
                addCategory(category, categoryId);
            } else {
                final JSONArray quizzes = category.getJSONArray(JsonAttributes.QUIZZES);
                JSONObject quiz;
                for (int ii = 0; i < quizzes.length(); ii++){
                    quiz = quizzes.getJSONObject(ii);
                    Quiz q = realm.where(Quiz.class).equalTo(Quiz.QUESTION, quiz.getString(JsonAttributes.QUESTION)).findFirst();
                    if (q == null){
                        addQuizz(c, quiz);
                    }
                }
            }

        }
    }

    private void addCategory(JSONObject category, String categoryId) throws JSONException {
        Category c;
        c = new Category();
        c.setCatId(categoryId);
        c.setTitle(category.getString(JsonAttributes.NAME));
        c.setTheme(category.getString(JsonAttributes.THEME));
        c.setSolved(category.getString(JsonAttributes.SOLVED));
        c.setScores(category.getString(JsonAttributes.SCORES));
        realm.beginTransaction();
        realm.copyToRealm(c);
        realm.commitTransaction();
    }

    private void addQuizz(Category c, JSONObject quiz) throws JSONException {
        Quiz q;
        q = new Quiz();
        q.setType(quiz.getString(JsonAttributes.TYPE));
        q.setQuestion(quiz.getString(JsonAttributes.QUESTION));
        q.setAnswer(quiz.getString(JsonAttributes.ANSWER));
        q.setOptions(quiz.getString(JsonAttributes.OPTIONS));
        q.setSolved(quiz.getBoolean(JsonAttributes.SOLVED));
        realm.beginTransaction();
        c.getQuizzes().add(q);
        realm.commitTransaction();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new CustomTypefaceFactory(
                this, CustomTypeface.getInstance()));
        super.onCreate(savedInstanceState);


        realm = Realm.getInstance(this);

        if (getIntent().getStringExtra("cat") != null &&!getIntent().getStringExtra("cat").equals("")){
            Category category = realm.where(Category.class).equalTo(Category.CAT_ID, getIntent().getStringExtra("cat")).findFirst();
            if (!category.getSolved().equals("solved")) {
                realm.beginTransaction();
                category.setSolved("solved");
                realm.commitTransaction();
            }
        }
        RealmResults<Category> categories = realm.where(Category.class).findAll();

        List<Category> cat = new ArrayList<>();

        for (Category c : categories){
            cat.add(c);
        }

        CategoriesAdapter adapter = new CategoriesAdapter(this, cat);

        adapter.setOnCategoryClickInteraction(this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(adapter);

        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AsyncTask<Void, Void, Integer> task = loadFromSnapshot(this);
        task.execute();

    }

    @Override
    public int contentId() {
        return R.layout.activity_home;
    }

    @Override
    public boolean hasToolBar() {
        return true;
    }

    @Override
    public boolean hasTitle() {
        return false;
    }

    @Override
    public int getToolBarId() {
        return R.id.toolbar;
    }


    @Override
    public boolean hasFAB() {
        return true;
    }

    @Override
    public int FAB() {
        return R.id.fab;
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
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onCategoryClick(Category c) {
        if (!c.getSolved().equals("solved")) {
            Intent i = new Intent(this, GameActivity.class);
            i.putExtra("category", c.getCatId());

            startActivity(i);
        } else {
            Snackbar.make(mFab, "You have solved all quizzes in "+c.getTitle(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
