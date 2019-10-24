package app.sliko.events;

public class RefreshEvent {
    public boolean status;

    public RefreshEvent(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
