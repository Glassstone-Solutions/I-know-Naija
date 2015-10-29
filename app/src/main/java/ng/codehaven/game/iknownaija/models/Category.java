package ng.codehaven.game.iknownaija.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Category extends RealmObject {

    public static final String CAT_TITLE = "title";

    public static final String CAT_DESC = "desc";
    public static String CAT_ID = "catId";

    @PrimaryKey
    private String title;

    private String catId;

    private String desc;

    private String theme;

    private String solved;

    private String scores;

    private RealmList<Quiz> quizzes;

    public Category() {
    }

    public Category(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public RealmList<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(RealmList<Quiz> quizzes) {
        this.quizzes = quizzes;
    }
}
