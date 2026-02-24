package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet branch object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Rossi Clinton
 */

public class Branch implements Serializable {
    /** name of the branch */
    private String name;

    /** reference (a SHA-1 identifier) to current head commit */
    private String head;

    public Branch(String name, String head) {
        this.name = name;
        this.head = head;
    }

    public void updateHead(String commitID) {
        head = commitID;
        this.saveBranch();
    }

    public String getHead() {
        return head;
    }

    public String getName() {
        return name;
    }

    public void saveBranch() {
        File saveFile = join(BRANCHES_DIR, name);
        writeObject(saveFile, this);
    }

    public File getHeadFile() {
        File headFile = join(COMMITS_DIR, head);
        return headFile;
    }

    public Commit getHeadCommit() {
        File headFile = this.getHeadFile();
        Commit headCommit = readObject(headFile, Commit.class);
        return headCommit;
    }
}
