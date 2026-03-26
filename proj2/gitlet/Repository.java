package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Rossi Clinton
 */
public class Repository implements Serializable {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The commits' directory. */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

    /** The branches' directory. */
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");

    /** The blobs' directory. */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");

    /** The staging area file */
    public static final File index = join(GITLET_DIR, "index");

    /** The repository file */
    public static final File repo = join(GITLET_DIR, "repo");

    /** Current active branch, initialized as master branch */
    public Branch activeBranch;

    /* TODO: fill in the rest of this class. */

    public Repository() {
        if (GITLET_DIR.exists()) {
            File repository = join(GITLET_DIR, "repo");
            if (repo.exists()) {
                Repository repo = readObject(repository, Repository.class);
                this.activeBranch = repo.activeBranch;
            }
        }
    }

    private void saveRepository() {
        writeObject(repo, this);
    }

    public void init() {
        //check if .gitlet is already in cwd
        if (GITLET_DIR.exists() && GITLET_DIR.isDirectory()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        //make directories for .gitlet, commits, branches, and blobs
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        BLOBS_DIR.mkdir();
        //create staging area and save to index file under .gitlet
        Stage initStage = new Stage();
        initStage.saveStage();
        //create initial commit and save as file under commits directory
        Commit initial = new Commit();
        String commitID = initial.getCommitID();
        initial.saveCommit();
        //create master branch and save branch file under branches folder and save repository in repo file under .gitlet
        activeBranch = new Branch("master", commitID);
        activeBranch.saveBranch();
        saveRepository();
    }

    public void add(String fileName) {
        validateGitletDirectory();

        //check if file exists and exit if not
        File fileCopy = join(CWD, fileName);
        if (!fileCopy.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        //read in the file contents, stage, and head commit
        byte[] fileCopyContents = readContents(fileCopy);
        Blob newBlob = new Blob(fileCopyContents);
        String blobID = newBlob.getBlobID();
        Stage currStage = readObject(index, Stage.class);
        Commit headCommit = activeBranch.getHeadCommit();
        //if file is the same as in current commit, remove from staging area. Otherwise, add to staging area
        if (headCommit.getBlobID(fileName) == blobID) {
            currStage.removeAddFile(fileName);
            currStage.removeRemFile(fileName);
        } else {
            currStage.putAddFile(fileName, blobID);
        }
        //save states for stage and new blob
        currStage.saveStage();
        newBlob.saveBlob();
    }

    public void commit(String message) {
        commitWithMerge(message, null);
    }

    private void commitWithMerge(String message, String secondParent){
        validateGitletDirectory();

        //check that commit has a non-blank message
        if (message.length() == 0) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        //Read from my computer the head commit object and the staging area
        Stage currStage = readObject(index, Stage.class);
        Commit headCommit = activeBranch.getHeadCommit();

        //If no files have been staged, abort.
        if (currStage.isEmpty()){
            System.out.println("No Changes added to the commit.");
            System.exit(0);
        }

        //clone the head commit
        //modify its message and timestamp according to user input and current time
        Commit newCommit = new Commit(message, secondParent, headCommit);

        //use the staging area in order to modify the files tracked by the new commit
        //clear the stage and update the active branch's head
        newCommit.commitStagedFiles(currStage);
        currStage.clear();
        activeBranch.updateHead(newCommit.getCommitID());

        //Write back any new objects made or any modified objects read earlier
        newCommit.saveCommit();
        currStage.saveStage();
        saveRepository();
    }

    public void remove(String filename) {
        validateGitletDirectory();

        //read in stage and head commit
        Stage currStage = readObject(index, Stage.class);
        Commit headCommit = activeBranch.getHeadCommit();
        //If the file is neither staged nor tracked by the head commit, print the error message "No reason to remove the file."
        if (!currStage.containsKeyAddFile(filename) && !headCommit.containsKeyBlobFiles(filename)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        //Check if file is staged for addition. If so, unstage it.
        if (currStage.containsKeyAddFile(filename)) {
            currStage.removeAddFile(filename);
        }

        //Check if file is tracked in current commit. If so, stage for removal and remove file from cwd.
        if (headCommit.containsKeyBlobFiles(filename)) {
            String blobID = headCommit.getBlobID(filename);
            currStage.putRemFile(filename, blobID);
            restrictedDelete(filename);
        }

        currStage.saveStage();
    }

    public void log() {
        validateGitletDirectory();

        //get current head commit and set currParent to arbitrary string to ensure a log if only the init commit will be printed
        Commit currCommit = activeBranch.getHeadCommit();
        String currParent = "first";
        while (currParent != null) {
            currCommit.createCommitLogOutput();
            //get new parent commit id and if not null, set current commit equal to the parent commit
            currParent = currCommit.getParent();
            if (currParent != null) {
                currCommit = getCommit(currParent);
            }
        }
    }

    public void globalLog() {
        validateGitletDirectory();

        List<String> commitList = plainFilenamesIn(COMMITS_DIR);
        Commit currCommit;
        for (String commit : commitList) {
            currCommit = getCommit(commit);
            currCommit.createCommitLogOutput();
        }
    }

    public void find(String commitMessage) {
        validateGitletDirectory();

        List<String> commitIds = new ArrayList<>();
        List<String> commitList = plainFilenamesIn(COMMITS_DIR);
        Commit currCommit;
        for (String commit : commitList) {
            currCommit = getCommit(commit);
            if (commitMessage.equals(currCommit.getMessage())) {
                commitIds.add(currCommit.getCommitID());
            }
        }
        if (commitIds.size() == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
        for (String id : commitIds) {
            System.out.println(id);
        }
    }

    public void status() {
        validateGitletDirectory();

        //Reading in branches and stage files
        List<String> branchList = plainFilenamesIn(BRANCHES_DIR);
        Stage currStage = readObject(index, Stage.class);
        Map<String, String> addFiles = currStage.getAddFiles();
        Map<String, String> remFiles = currStage.getRemFiles();

        System.out.println("=== Branches ===");
        for (String branch : branchList) {
            if (branch.equals(activeBranch.getName())) {
                System.out.println("*" + branch);
            } else { System.out.println(branch); }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        addFiles.forEach((file, blobID) -> {
            System.out.println(file);
        });
        System.out.println();

        System.out.println("=== Removed Files ===");
        remFiles.forEach((file, blobID) -> {
            System.out.println(file);
        });
        System.out.println();
    }

    public void checkout(String fileName) {
        validateGitletDirectory();

        Commit checkoutCommit = activeBranch.getHeadCommit();
        checkout(checkoutCommit.getCommitID(), fileName);
    }

    public void checkout(String commitId, String fileName) {
        validateGitletDirectory();

        Commit checkoutCommit = getCommit(commitId);
        if (checkoutCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        String blobID = checkoutCommit.getBlobID(fileName);
        if (blobID == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File blobFile = join(BLOBS_DIR, blobID);
        byte[] blobFileContents = readContents(blobFile);
        File checkoutFile = join(CWD, fileName);
        writeContents(checkoutFile, blobFileContents);
    }

    public void checkoutBranch(String branchName) {
        validateGitletDirectory();

        if (branchName.equals(activeBranch.getName())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        File branchFile = join(BRANCHES_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        List<String> cwdFiles = plainFilenamesIn(CWD);
        Branch checkoutBranch = getBranch(branchName);
        Commit checkoutCommit = checkoutBranch.getHeadCommit();
        Commit currentCommit = activeBranch.getHeadCommit();

        for (String file : cwdFiles) {
            if (checkoutCommit.containsKeyBlobFiles(file) && !currentCommit.containsKeyBlobFiles(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String file : cwdFiles) {
            if (!checkoutCommit.containsKeyBlobFiles(file) && currentCommit.containsKeyBlobFiles(file)) {
                restrictedDelete(file);
            }
        }

        for (String fileName : checkoutCommit.getKeySet()) {
            String blobID = checkoutCommit.getBlobID(fileName);
            File blobFile = join(BLOBS_DIR, blobID);
            byte[] blobFileContents = readContents(blobFile);
            File checkoutFile = join(CWD, fileName);
            writeContents(checkoutFile, blobFileContents);
        }

        Stage currStage = readObject(index, Stage.class);
        currStage.clear();
        currStage.saveStage();
        activeBranch = checkoutBranch;
        saveRepository();
    }

    public void branch(String branchName) {
        validateGitletDirectory();

        File branchFile = join(BRANCHES_DIR, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Branch newBranch = new Branch(branchName, activeBranch.getHead());
        newBranch.saveBranch();
    }

    public void removeBranch(String branchName) {
        validateGitletDirectory();

        if (branchName.equals(activeBranch.getName())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        File branchFile = join(BRANCHES_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        if (!branchFile.isDirectory()) {
            branchFile.delete();
        }
    }

    public void reset (String commitId) {
        validateGitletDirectory();

        Commit checkoutCommit = getCommit(commitId);
        Commit currCommit = activeBranch.getHeadCommit();
        if (checkoutCommit == null || currCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        List<String> cwdFiles = plainFilenamesIn(CWD);
        for (String file : cwdFiles) {
            if (checkoutCommit.containsKeyBlobFiles(file) && !currCommit.containsKeyBlobFiles(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String file : cwdFiles) {
            if (!checkoutCommit.containsKeyBlobFiles(file) && currCommit.containsKeyBlobFiles(file)) {
                restrictedDelete(file);
            }
        }

        for (String fileName : checkoutCommit.getKeySet()) {
            checkout(commitId, fileName);
        }
        activeBranch.updateHead(commitId);
        Stage currStage = readObject(index, Stage.class);
        currStage.clear();
        currStage.saveStage();
        saveRepository();
    }

    public void merge(String branchName) {
        validateGitletDirectory();
        validateMerge(branchName);

        Branch givenBranch = getBranch(branchName);
        Commit givenBranchHeadCommit = givenBranch.getHeadCommit();
        Commit currBranchHeadCommit = activeBranch.getHeadCommit();
        Commit splitPointCommit = getSplitPointCommit(givenBranchHeadCommit, currBranchHeadCommit, branchName);

        Set<String> potentialMergeFiles = new HashSet<>();
        potentialMergeFiles.addAll(givenBranchHeadCommit.getKeySet());
        potentialMergeFiles.addAll(currBranchHeadCommit.getKeySet());
        potentialMergeFiles.addAll(splitPointCommit.getKeySet());

        boolean isConflicted = false;
        for (String file : potentialMergeFiles) {
            if (!isConflicted) {
                isConflicted = mergeFile(file, givenBranchHeadCommit, currBranchHeadCommit, splitPointCommit);
            } else {
                mergeFile(file, givenBranchHeadCommit, currBranchHeadCommit, splitPointCommit);
            }
        }

        String commitMessage = "Merged " + branchName
                + " into " + activeBranch.getName() + ".";

        commitWithMerge(commitMessage, givenBranch.getHead());

        if (isConflicted) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private void validateMerge(String branchName) {
        Stage currStage = readObject(index, Stage.class);
        if (!currStage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        File branchPath = join(BRANCHES_DIR, branchName);
        if (!branchPath.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        if (branchName.equals(activeBranch.getName())) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        Branch givenBranch = getBranch(branchName);
        Commit givenBranchHeadCommit = givenBranch.getHeadCommit();
        Commit currBranchHeadCommit = activeBranch.getHeadCommit();

        List<String> cwdFiles = plainFilenamesIn(CWD);
        for (String fileName : cwdFiles) {
            if (givenBranchHeadCommit.containsKeyBlobFiles(fileName) && !currBranchHeadCommit.containsKeyBlobFiles(fileName)){
                System.out.println("There is an untracked file in the way;" + " delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private Map<String, Integer> buildBranchGraph(String commitId) {
        Map<String, Integer> branchGraph = new HashMap<>();

        buildBranchGraphHelper(branchGraph, commitId, 0);
        return branchGraph;
    }

    private void buildBranchGraphHelper(Map<String, Integer> branchGraph, String commitId, int depth) {
        if (commitId == null) {
            return;
        }

        branchGraph.put(commitId, depth);

        Commit currCommit = getCommit(commitId);
        buildBranchGraphHelper(branchGraph, currCommit.getParent(), depth + 1);
        buildBranchGraphHelper(branchGraph, currCommit.getSecondParent(), depth + 1);
    }

    private Commit getSplitPointCommit(Commit givenBranchHeadCommit, Commit currBranchHeadCommit, String givenBranchName) {
        Map<String, Integer> givenBranchCommitGraph = buildBranchGraph(givenBranchHeadCommit.getCommitID());
        Map<String, Integer> currBranchCommitGraph = buildBranchGraph(currBranchHeadCommit.getCommitID());

        String splitPointId = null;
        int minDepth = Integer.MAX_VALUE;
        for (String commitId : currBranchCommitGraph.keySet()) {
            int depth = givenBranchCommitGraph.get(commitId);
            if (givenBranchCommitGraph.containsKey(commitId) && depth < minDepth) {
                minDepth = depth;
                splitPointId = commitId;
            }
        }

        if (splitPointId.equals(givenBranchHeadCommit.getCommitID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }

        if (splitPointId.equals(currBranchHeadCommit.getCommitID())) {
            checkoutBranch(givenBranchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        return getCommit(splitPointId);
    }

    private boolean mergeFile(String fileName, Commit currBranchHeadCommit, Commit givenBranchHeadCommit, Commit splitPointCommit) {

        boolean isConflicted = false;
        boolean isFileInCurr = currBranchHeadCommit.containsKeyBlobFiles(fileName);
        boolean isFileInGiven = givenBranchHeadCommit.containsKeyBlobFiles(fileName);
        boolean isFileInSplit = splitPointCommit.containsKeyBlobFiles(fileName);
        byte[] fileContentInCurr = currBranchHeadCommit.getBlobContents(fileName);
        byte[] fileContentInGiven = givenBranchHeadCommit.getBlobContents(fileName);
        byte[] fileContentInSplit = splitPointCommit.getBlobContents(fileName);

        boolean isFileModifiedInCurr = !Arrays.equals(fileContentInCurr, fileContentInSplit);
        boolean isFileModifiedInGiven = !Arrays.equals(fileContentInGiven, fileContentInSplit);

        if (isFileInCurr && isFileInGiven && isFileInSplit) {
            if (isFileModifiedInGiven && !isFileModifiedInCurr) {
                Stage currStage = readObject(index, Stage.class);
                checkout(givenBranchHeadCommit.getCommitID(), fileName);
                currStage.putAddFile(fileName, currBranchHeadCommit.getCommitID());
                currStage.saveStage();
            } else if (!isFileModifiedInGiven && isFileModifiedInCurr) {
                isConflicted = false;
            } else if (isFileModifiedInGiven && isFileModifiedInCurr) {
                if (Arrays.equals(fileContentInGiven, fileContentInCurr)) {
                    isConflicted = false;
                } else {
                    mergeFileWithConflicts(fileName, fileContentInGiven, fileContentInCurr);
                    isConflicted = true;
                }
            }
        } else if (isFileInCurr && isFileInGiven && !isFileInSplit && !Arrays.equals(fileContentInGiven, fileContentInCurr)){
            mergeFileWithConflicts(fileName, fileContentInGiven, fileContentInCurr);
            isConflicted = true;
        } else if (isFileInCurr && !isFileInGiven && !isFileInSplit) {
            isConflicted = false;
        } else if (!isFileInCurr && isFileInGiven && !isFileInSplit) {
            Stage currStage = readObject(index, Stage.class);
            checkout(givenBranchHeadCommit.getCommitID(), fileName);
            currStage.putAddFile(fileName, currBranchHeadCommit.getCommitID());
            currStage.saveStage();
        } else if (isFileInCurr && !isFileInGiven && isFileInSplit) {
            if (!isFileModifiedInCurr) {
                remove(fileName);
            } else {
                mergeFileWithConflicts(fileName, fileContentInGiven, fileContentInCurr);
                isConflicted = true;
            }
        } else if (!isFileInCurr && isFileInGiven && isFileInSplit) {
            if (!isFileModifiedInGiven) {
                isConflicted = false;
            } else {
                mergeFileWithConflicts(fileName, fileContentInGiven, fileContentInCurr);
                isConflicted = true;
            }
        }
        return isConflicted;
    }

    private void mergeFileWithConflicts(String fileName, byte[] fileContentInGiven, byte[] fileContentInCurr) {
        String givenContentsAsString = new String(fileContentInGiven, StandardCharsets.UTF_8);
        String currContentsAsString = new String(fileContentInCurr, StandardCharsets.UTF_8);

        String conflictedFileContent = "<<<<<<< HEAD" + "\n";
        if (currContentsAsString != null) {
            conflictedFileContent += currContentsAsString;
        }

        conflictedFileContent += "=======" + "\n";
        if (givenContentsAsString != null) {
            conflictedFileContent += givenContentsAsString;
        }
        conflictedFileContent += ">>>>>>>" + "\n";
        byte[] conflictedFileByteContent = conflictedFileContent.getBytes(StandardCharsets.UTF_8);

        File conflictedFile = new File(fileName);
        Utils.writeContents(conflictedFile, conflictedFileContent);


        Blob newBlob = new Blob(conflictedFileByteContent);
        newBlob.saveBlob();

        Stage currStage = readObject(index, Stage.class);
        currStage.putAddFile(fileName, activeBranch.getHeadCommit().getCommitID());
        currStage.saveStage();
    }

    private Commit getCommit(String commitId) {
        File commitPath = join(COMMITS_DIR, commitId);
        if (!commitPath.exists()) {
            return null;
        }
        Commit returnCommit = readObject(commitPath, Commit.class);
        return returnCommit;
    }

    private Branch getBranch(String branchName) {
        File branchPath = join(BRANCHES_DIR, branchName);
        if (!branchPath.exists()) {
            return null;
        }
        Branch returnBranch = readObject(branchPath, Branch.class);
        return returnBranch;
    }

    public void validateGitletDirectory() {
        if (!(GITLET_DIR.exists() && GITLET_DIR.isDirectory())){
            System.out.println("Not in an initialized Gitlet Directory.");
            System.exit(0);
        }
    }
}
