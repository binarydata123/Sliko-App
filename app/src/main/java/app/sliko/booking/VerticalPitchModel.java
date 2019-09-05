package app.sliko.booking;

public class VerticalPitchModel {
    private  String pitchName;
    private  String pitchId;
    private String userId;
    private  String stadiumId;
    private String pitchDescription;
    private  String pitchPrice;

    public String getPitchName() {
        return pitchName;
    }

    public void setPitchName(String pitchName) {
        this.pitchName = pitchName;
    }

    public String getPitchId() {
        return pitchId;
    }

    public void setPitchId(String pitchId) {
        this.pitchId = pitchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getPitchDescription() {
        return pitchDescription;
    }

    public void setPitchDescription(String pitchDescription) {
        this.pitchDescription = pitchDescription;
    }

    public String getPitchPrice() {
        return pitchPrice;
    }

    public void setPitchPrice(String pitchPrice) {
        this.pitchPrice = pitchPrice;
    }
}
