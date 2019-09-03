package app.sliko.owner.model;

public class AvailabilityModel {
    private String time;
    private boolean isPicked;

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
