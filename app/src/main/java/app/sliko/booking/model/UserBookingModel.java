package app.sliko.booking.model;

public class UserBookingModel {
    private String time;
    private String id;
    private String booked_status;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBooked_status() {
        return booked_status;
    }

    public void setBooked_status(String booked_status) {
        this.booked_status = booked_status;
    }
}
