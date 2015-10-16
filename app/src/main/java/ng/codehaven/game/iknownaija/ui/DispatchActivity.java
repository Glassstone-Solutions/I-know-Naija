package ng.codehaven.game.iknownaija.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.game.iknownaija.Common;
import ng.codehaven.game.iknownaija.R;
import ng.codehaven.game.iknownaija.ui.views.IntroView;

public class DispatchActivity extends AppCompatActivity {

    SharedPreferences sp;

//    ProgressBar mProgress;

    @InjectView(R.id.introView) IntroView mIntroView;
    @InjectView(R.id.view_control)
    LinearLayout mControl;

    List<ViewStack> viewStackList;

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        ButterKnife.inject(this);

        mControl.setAlpha(0);
        viewStackList = new ArrayList<>();

        viewStackList.add(new ViewStack(mControl, 1000, 0f, 1f));

        mIntroView.setSvgResource(R.raw.test);
        mIntroView.setOnReadyListener(new IntroView.OnReadyListener() {
            @Override
            public void onReady() {
                mIntroView.stopWaitAnimation();
                for (ViewStack v : viewStackList){
                    animator(v.getV(), v.getDuration(), v.getFrom(), v.getTo());
                }
            }
        });

//        mProgress = (ProgressBar) findViewById(R.id.loadingProgress);

//        try {
//            SVG svg = SVG.getFromResource(this, R.raw.nigeria_location_map);
//
//
//            if (svg.getDocumentWidth() != -1){
//                Bitmap  newBM = Bitmap.createBitmap((int)Math.ceil(svg.getDocumentWidth()), (int)Math.ceil(svg.getDocumentHeight()), Bitmap.Config.ARGB_8888);
//                Canvas  bmcanvas = new Canvas(newBM);
//
//                // Clear background to white
//                bmcanvas.drawRGB(255, 255, 255);
//
//                // Render our document onto our canvas
//                svg.renderToCanvas(bmcanvas);
//            }
//        } catch (SVGParseException e) {
//            e.printStackTrace();
//        }
    }


    private void animator (final View v, int duration, float from, float to){
        ObjectAnimator oa = ObjectAnimator.ofFloat(v, "alpha", from, to);
        oa.setDuration(duration);
        oa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        oa.start();
    }

    private class ViewStack{
        View v;
        int duration;
        float from;
        float to;

        public ViewStack(View v, int duration, float from, float to) {
            this.v = v;
            this.duration = duration;
            this.from = from;
            this.to = to;
        }

        public View getV() {
            return v;
        }

        public void setV(View v) {
            this.v = v;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public float getFrom() {
            return from;
        }

        public void setFrom(float from) {
            this.from = from;
        }

        public float getTo() {
            return to;
        }

        public void setTo(float to) {
            this.to = to;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        sp = getSharedPreferences(Common.SHARED_PREF, Context.MODE_PRIVATE);
        if (sp.getBoolean(Common.FIRST_RUN_KEY, true)){
            init(this);
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }

    }

    private void init(Context context) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Common.FIRST_RUN_KEY, false);

        if (bootstrapDone()){
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
}
