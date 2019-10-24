package app.sliko.events;

public class ScrollEvent {
    public int dx;
    public int dy;

    public ScrollEvent(int dx,int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
}
