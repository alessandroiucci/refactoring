package theater;
/**
 * Represents a play.
 */

public class Play {

    public String name;
    public String type;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
