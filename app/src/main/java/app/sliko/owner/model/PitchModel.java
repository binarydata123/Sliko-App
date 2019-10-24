package app.sliko.owner.model;

import java.util.ArrayList;

public class PitchModel {

    private String id = "";
    private String stadium_id = "";
    private String user_id = "";
    private String pitch_name = "";
    private String description = "";
    private String price = "";
    private String availability = "";
    private String status = "";
    private String deleted = "";
    private String created_by = "";
    private String updated_by = "";
    private String created_at = "";
    private String updated_at = "";
    private String pitch_review_avg = "";
    private String booking_status = "";
    private String process_booking = "0";
    private String complete_booking = "0";
    private String pitch_id = "0";
    private String pitch_type = "0";
    private ArrayList<String> pitch_gallery = null;

    public String getPitch_type() {
        return pitch_type;
    }

    public void setPitch_type(String pitch_type) {
        this.pitch_type = pitch_type;
    }

    public String getPitch_id() {
        return pitch_id;
    }

    public void setPitch_id(String pitch_id) {
        this.pitch_id = pitch_id;
    }

    public String getProcess_booking() {
        return process_booking;
    }

    public void setProcess_booking(String process_booking) {
        this.process_booking = process_booking;
    }

    public String getComplete_booking() {
        return complete_booking;
    }

    public void setComplete_booking(String complete_booking) {
        this.complete_booking = complete_booking;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStadium_id() {
        return stadium_id;
    }

    public void setStadium_id(String stadium_id) {
        this.stadium_id = stadium_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPitch_name() {
        return pitch_name;
    }

    public void setPitch_name(String pitch_name) {
        this.pitch_name = pitch_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPitch_review_avg() {
        return pitch_review_avg;
    }

    public void setPitch_review_avg(String pitch_review_avg) {
        this.pitch_review_avg = pitch_review_avg;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public ArrayList<String> getPitch_gallery() {
        return pitch_gallery;
    }

    public void setPitch_gallery(ArrayList<String> pitch_gallery) {
        this.pitch_gallery = pitch_gallery;
    }
}
