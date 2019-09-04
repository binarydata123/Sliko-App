package app.sliko.owner.events;

public class StadiumExistEventOrNot {
    public boolean responseSuccess;
    public String reponse;

    public StadiumExistEventOrNot(boolean responseSuccess, String reponse) {
        this.responseSuccess = responseSuccess;
        this.reponse = reponse;
    }

    public boolean isResponseSuccess() {
        return responseSuccess;
    }

    public String getReponse() {
        return reponse;
    }
}
