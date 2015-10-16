package ng.codehaven.game.iknownaija.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import ng.codehaven.game.iknownaija.R;

public class HomeActivity extends BaseActivity implements
        OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}
