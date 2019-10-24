package app.sliko.events;

public class SuccessFullPaymentEvent {
    public boolean isPaymentDone;

    public SuccessFullPaymentEvent(boolean isPaymentDone) {
        this.isPaymentDone = isPaymentDone;
    }

    public boolean isPaymentDone() {
        return isPaymentDone;
    }
}
