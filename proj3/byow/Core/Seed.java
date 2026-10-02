package byow.Core;

import java.io.Serializable;

import static byow.Core.Engine.BYOW_DIR;
import static byow.Core.Engine.SEED_FILE;
import static byow.Core.SerializationUtils.*;

public class Seed implements Serializable {
    private String seed;

    public Seed(String seed) {
        this.seed = seed;
    }

    public String getSeed() {
        return seed;
    }

    public void addToSeed(String input) {
        seed = seed + input;
    }

    public void saveSeed() {
        if (!BYOW_DIR.exists()) {
            BYOW_DIR.mkdir();
        }
        writeObject(SEED_FILE, this);
    }
}
