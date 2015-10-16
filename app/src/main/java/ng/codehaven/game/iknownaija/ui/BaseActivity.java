package ng.codehaven.game.iknownaija.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public abstract class BaseActivity extends AppCompatActivity {

    public abstract int contentId();

    public abstract boolean hasToolBar();

    public abstract boolean hasTitle();

    public abstract int getToolBarId();

    public abstract boolean hasFAB();
    public abstract int FAB();

    protected FloatingActionButton mFab;
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        setContentView(contentId());

        if (hasToolBar()) {
            mToolbar = getToolBar(getToolBarId());
            setSupportActionBar(mToolbar);

            assert getSupportActionBar() != null;
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
