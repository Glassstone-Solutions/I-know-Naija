package ng.codehaven.game.iknownaija.models;

import io.realm.RealmObject;

/**
 * Created by Thompson on 19/10/2015.
 */
public class Quiz extends RealmObject {
    private String type, question, answer, options;
    private boolean solved;

    public Quiz() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
}
