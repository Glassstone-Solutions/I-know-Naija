package ng.codehaven.game.iknownaija.ui;

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
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cat.ppicas.customtypeface.CustomTypeface;
import cat.ppicas.customtypeface.CustomTypefaceFactory;
import io.realm.Realm;
import io.realm.RealmResults;
import ng.codehaven.game.iknownaija.R;
import ng.codehaven.game.iknownaija.models.Category;
import ng.codehaven.game.iknownaija.ui.adapters.CategoriesAdapter;
import ng.codehaven.game.iknownaija.utils.SavedGameUtils;

public class HomeActivity extends BaseActivity implements
        OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, CategoriesAdapter.OnCategoryClickInteraction {

    public static final String TAG = HomeActivity.class.getSimpleName();

    @InjectView(R.id.rv_cat)
    RecyclerView mRecycler;

    /// Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new CustomTypefaceFactory(
                this, CustomTypeface.getInstance()));
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        Realm realm = Realm.getInstance(this);

        SavedGameUtils savedGameUtils = new SavedGameUtils(realm);

        if (getIntent().getStringExtra("cat") != null && !getIntent().getStringExtra("cat").equals("")) {
            Category category = realm.where(Category.class).equalTo(Category.CAT_ID, getIntent().getStringExtra("cat")).findFirst();
            if (!category.getSolved().equals("solved")) {
                realm.beginTransaction();
                category.setSolved("solved");
                realm.commitTransaction();
            }
        }
        RealmResults<Category> categories = realm.where(Category.class).findAll();

        List<Category> cat = new ArrayList<>();

        for (Category c : categories) {
            cat.add(c);
        }

        CategoriesAdapter adapter = new CategoriesAdapter(this, cat);

        adapter.setOnCategoryClickInteraction(this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(adapter);

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

        AsyncTask<Void, Void, Integer> task = savedGameUtils.loadFromSnapshot(mGoogleApiClient);
        task.execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
    public boolean hasUp() {
        return false;
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
            Snackbar.make(mRecycler, "You have solved all quizzes in " + c.getTitle(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).setActionTextColor(getResources().getColor(R.color.colorText)).show();
        }
    }
}
