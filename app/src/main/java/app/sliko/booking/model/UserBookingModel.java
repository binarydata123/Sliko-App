package app.sliko.booking.model;

public class UserBookingModel {
    private String time;
    private String show_slot;
    private String booked_status;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getShow_slot() {
        return show_slot;
    }

    public void setShow_slot(String show_slot) {
        this.show_slot = show_slot;
    }

    public String getBooked_status() {
        return booked_status;
    }

    public void setBooked_status(String booked_status) {
        this.booked_status = booked_status;
    }
}
