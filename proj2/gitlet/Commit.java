package gitlet;


import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import static gitlet.Utils.serialize;
import static gitlet.Utils.sha1;

/** Represents a gitlet commit object. */
public class Commit implements Serializable {

    /** The message of this Commit. */
    private String message;

    /** The time the Commit was created in string format*/
    private Instant timeStamp;

    /** The parent Commit to this Commit. The parent name will be the file that holds the parent commit*/
    private String parent;

    /** The 2nd parent Commit for merges **/
    private String secondParent;

    /** Map for holding which files the commit is tracking.
     *  Key is the File string and Value is the Blob string.*/
    private Map<String, String> blobFiles = new TreeMap<>();

    /** time zone to be used for instant conversion/formatting*/
    private ZoneId zoneId = ZoneId.of("America/New_York");

    /** custom DateTimeFormatter
     * Example: Thu Nov 9 20:00:05 2017 -0800 */
    // = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss uuuu Z");
    private transient DateTimeFormatter logFormatter;
    private final String timeStampPattern = "EEE MMM d HH:mm:ss uuuu Z";

    public Commit(String message, String secondParent, Commit prevCommit) {
        this.message = message;
        this.parent = prevCommit.getCommitID();
        this.secondParent = secondParent;
        this.timeStamp = Instant.now();
        copyParentBlobFiles(prevCommit.blobFiles);
    }

    /**Constructor for Initial commit that does not take in a parent argument*/
    public Commit() {
        this.message = "initial commit";
        this.parent = null;
        this.timeStamp = Instant.EPOCH;
        this.secondParent = null;
    }

    /** getter method for Message */
    public String getMessage() {
        return this.message;
    }

    /** getter method for timeStamp */
    public Instant getTimeStamp() {
        return this.timeStamp;
    }

    /** getter method for Parent */
    public String getParent() {
        return this.parent;
    }

    /** getter method for second parent */
    public String getSecondParent() { return this.secondParent; }

    public String getBlobID(String file) {
        return blobFiles.get(file);
    }

    public String getCommitID() {
        return sha1(serialize(this));
    }

    public byte[] getBlobContents(String fileName) {
        if (!blobFiles.containsKey(fileName)) {
            return null;
        }
        String blobId =  getBlobID(fileName);
        byte[] fileContents = Blob.getFileContents(blobId);
        return fileContents;
    }

    public Set<String> getKeySet() { return blobFiles.keySet(); }

    public void saveCommit() {
        File saveFile = join(COMMITS_DIR, getCommitID());
        writeObject(saveFile, this);
    }

    public boolean containsKeyBlobFiles(String file) {
        return blobFiles.containsKey(file);
    }

    private void copyParentBlobFiles(Map<String, String> parentBlobFiles) {
        parentBlobFiles.forEach((file, blobID) -> {
           blobFiles.put(file, blobID);
        });
    }

    public void removeFile(String fileName) {
        blobFiles.remove(fileName);
    }

    public void commitStagedFiles(Stage currStage) {
        Map<String, String> addFiles = currStage.getAddFiles();
        Map<String, String> remFiles = currStage.getRemFiles();

        addFiles.forEach((file, blobID) -> {
            blobFiles.put(file, blobID);
        });

        remFiles.forEach((file, blobID) -> {
            blobFiles.remove(file);
        });
    }

    public void createCommitLogOutput() {
        //converting our instant timestamp into a ZonedDateTime using zoneId
        ZonedDateTime zonedDateTime = this.timeStamp.atZone(zoneId);

        //Formatting the ZonedDateTime
        getLogFormatter();
        String customTimestampString = logFormatter.format(zonedDateTime);

        //spit out the commit id, merge ids(if applicable) date, and message for currCommit formatted properly
        if (this.secondParent == null) {
            String logMessage = String.format("=== %ncommit %s %nDate: %s %n%s %n", this.getCommitID(), customTimestampString, this.message);
            System.out.println(logMessage);
        } else {
            String logMessage = String.format("=== %ncommit %s %nMerge: %s %s %nDate: %s %n%s %n", this.getCommitID(), this.parent.substring(0, 8), this.secondParent.substring(0, 8), customTimestampString, this.message);
            System.out.println(logMessage);
        }
    }

    private DateTimeFormatter getLogFormatter() {
        if (this.logFormatter == null) {
            // Re-initialize the formatter if it's null (e.g., after deserialization)
            this.logFormatter = DateTimeFormatter.ofPattern(timeStampPattern);
        }
        return this.logFormatter;
    }

}
