package theater;

/**
 * Class representing a performance of a play..
 */
public class Performance {

    private String playID;
    private int audience;

    public String getPlayID() {
        return playID;
    }

    public int getAudience() {
        return audience;
    }

    public Performance(String playID, int audience) {
        this.playID = playID;
        this.audience = audience;
    }
}
