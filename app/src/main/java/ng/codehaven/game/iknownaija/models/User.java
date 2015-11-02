package ng.codehaven.game.iknownaija.models;

import io.realm.RealmObject;

/**
 * Created by Thompson on 02/11/2015.
 */
public class User extends RealmObject {
    private int score;
    public User(){}

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}