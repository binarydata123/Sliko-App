package app.sliko.owner.events;

public class SuccessFullyStadiumCreated {
    public boolean status;

    public SuccessFullyStadiumCreated(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
