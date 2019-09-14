package app.sliko.events;

public class ProfileUploadedSuccessEvent {
    public boolean status;

    public ProfileUploadedSuccessEvent(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
