package ng.codehaven.game.iknownaija.bus.events;

/**
 * Created by Thompson on 30/10/2015.
 */
public class QuizBuss {
    private String category;

    public QuizBuss(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
