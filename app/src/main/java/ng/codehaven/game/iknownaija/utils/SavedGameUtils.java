package ng.codehaven.game.iknownaija.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.Snapshots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import ng.codehaven.game.iknownaija.models.Category;
import ng.codehaven.game.iknownaija.models.JsonAttributes;
import ng.codehaven.game.iknownaija.models.Quiz;


public class SavedGameUtils {

    public static final String TAG = SavedGameUtils.class.getSimpleName();

    Realm realm;

    byte[] mSaveGameData;

    public SavedGameUtils(Realm realm) {
        this.realm = realm;
    }

    public void addCategory(JSONObject category, String categoryId) throws JSONException {
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

    public void addQuizz(Category c, JSONObject quiz) throws JSONException {
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

    public void compareWithLocalData(byte[] mSaveGameData) throws JSONException {
        JSONArray ja = new JSONArray(new String(mSaveGameData));
        JSONObject category;

        for (int i = 0; i < ja.length(); i++) {
            category = ja.getJSONObject(i);
            final String categoryId = category.getString(JsonAttributes.ID);
            Category c = realm.where(Category.class).equalTo(Category.CAT_ID, categoryId).findFirst();
            if (c == null) {
                addCategory(category, categoryId);
            } else {
                final JSONArray quizzes = category.getJSONArray(JsonAttributes.QUIZZES);
                JSONObject quiz;
                for (int ii = 0; i < quizzes.length(); ii++) {
                    quiz = quizzes.getJSONObject(ii);
                    Quiz q = realm.where(Quiz.class).equalTo(Quiz.QUESTION, quiz.getString(JsonAttributes.QUESTION)).findFirst();
                    if (q == null) {
                        addQuizz(c, quiz);
                    }
                }
            }

        }
    }

    public AsyncTask<Void, Void, Integer> loadFromSnapshot(final GoogleApiClient mGoogleApiClient) {
        return new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                Snapshots.OpenSnapshotResult result = null;
                int code = -9000;
                if (mGoogleApiClient.isConnected()) {
                    result = Games.Snapshots.open(mGoogleApiClient, "savedGame", true).await();
                    code = result.getStatus().getStatusCode();
                    if (result.getStatus().isSuccess()) {
                        Snapshot snapshot = result.getSnapshot();
                        try {
                            mSaveGameData = snapshot.getSnapshotContents().readFully();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "Error while loading " + result.getStatus().getStatusCode());
                    }
                }
                return code;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (integer != -9000 && mSaveGameData != null) {
                    try {
                        compareWithLocalData(mSaveGameData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

}
