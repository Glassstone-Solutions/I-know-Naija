package ng.codehaven.game.iknownaija.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cat.ppicas.customtypeface.CustomTypeface;
import cat.ppicas.customtypeface.CustomTypefaceFactory;
import io.realm.Realm;
import io.realm.RealmResults;
import ng.codehaven.game.iknownaija.R;
import ng.codehaven.game.iknownaija.ui.adapters.CategoriesAdapter;
import ng.codehaven.game.iknownaija.models.Category;

public class HomeActivity extends BaseActivity implements
        OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, CategoriesAdapter.OnCategoryClickInteraction {

    @InjectView(R.id.rv_cat)RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new CustomTypefaceFactory(
                this, CustomTypeface.getInstance()));
        super.onCreate(savedInstanceState);


        Realm realm = Realm.getInstance(this);

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
