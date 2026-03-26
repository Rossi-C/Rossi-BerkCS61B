package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Stage implements Serializable {

    /** Map for holding which files are added to the staging area.
     *  Key is the File string and Value is the Blob string.*/
    private Map<String, String> addFiles;

    /** Map for holding which files are to be removed from the staging area.
     *  Key is the File string and Value is the Blob string.*/
    private Map<String, String> remFiles;

    public Stage() {
        addFiles = new TreeMap<>();
        remFiles = new TreeMap<>();
    }

    public void saveStage() {
        writeObject(index, this);
    }

    public Map<String, String> getAddFiles() {
        return addFiles;
    }

    public Map<String, String> getRemFiles() {
        return remFiles;
    }

    public String putAddFile(String file, String blobID) {
        return addFiles.put(file, blobID);
    }

    public String putRemFile(String file, String blobID) {
        return remFiles.put(file, blobID);
    }

    public void removeAddFile(String file) {
        addFiles.remove(file);
    }

    public void removeRemFile(String file) {
        remFiles.remove(file);
    }

    public boolean containsKeyAddFile(String file) {
        return addFiles.containsKey(file);
    }

    public void clear() {
        addFiles.clear();
        remFiles.clear();
    }

    public boolean isEmpty() {
        if (addFiles.isEmpty() && remFiles.isEmpty()) {
            return true;
        }
        return false;
    }
}
