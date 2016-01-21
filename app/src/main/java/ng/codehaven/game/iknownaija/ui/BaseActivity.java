package ng.codehaven.game.iknownaija.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import io.realm.Realm;


public abstract class BaseActivity extends AppCompatActivity {

    public abstract int contentId();

    public abstract boolean hasToolBar();

    public abstract boolean hasTitle();

    public abstract boolean hasUp();

    public abstract int getToolBarId();

    public abstract boolean hasFAB();
    public abstract int FAB();

    protected FloatingActionButton mFab;
    protected Toolbar mToolbar;
    protected SharedPreferences sp;
    protected Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        setContentView(contentId());

        ButterKnife.inject(this);

        if (hasToolBar()) {
            mToolbar = getToolBar(getToolBarId());
            setSupportActionBar(mToolbar);

            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(hasUp());
            getSupportActionBar().setDisplayShowTitleEnabled(hasTitle());

        }

        if (hasFAB()){
            mFab = (FloatingActionButton) findViewById(FAB());
        }
    }

    private Toolbar getToolBar(int id){
        return (Toolbar) findViewById(id);
    }
}
