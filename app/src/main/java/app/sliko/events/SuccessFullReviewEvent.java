package app.sliko.events;

public class SuccessFullReviewEvent {
    public boolean isReviewGiven;

    public SuccessFullReviewEvent(boolean isReviewGiven) {
        this.isReviewGiven = isReviewGiven;
    }

    public boolean isReviewGiven() {
        return isReviewGiven;
    }
}
