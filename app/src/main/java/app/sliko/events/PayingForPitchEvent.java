package app.sliko.events;

public class PayingForPitchEvent {
    String time; int withThisGetPitchPosition;
    boolean status;

    public PayingForPitchEvent(String time, int withThisGetPitchPosition, boolean status) {
        this.time = time;
        this.withThisGetPitchPosition = withThisGetPitchPosition;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public int getWithThisGetPitchPosition() {
        return withThisGetPitchPosition;
    }
}
