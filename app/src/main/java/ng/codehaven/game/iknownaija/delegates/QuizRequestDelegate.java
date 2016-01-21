package ng.codehaven.game.iknownaija.delegates;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import ng.codehaven.game.iknownaija.bus.events.QuizBuss;

/**
 * Created by Thompson on 30/10/2015.
 */
public class QuizRequestDelegate {

    private static final String TAG = QuizRequestDelegate.class.getSimpleName();

    private Context context;
    private Bus bus;

    public QuizRequestDelegate(Context context, Bus bus) {
        this.context = context;
        this.bus = bus;
    }

    @Subscribe
    public void getQuiz(QuizBuss quizBuss){
        
    }
}
