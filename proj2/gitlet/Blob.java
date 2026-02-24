package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Blob implements Serializable {

     private byte[] fileContents;

     public Blob(byte[] fileContents) {
         this.fileContents = fileContents;
     }

     public void saveBlob() {
         File saveFile = join(BLOBS_DIR, getBlobID());
         if (!saveFile.exists()) {
             writeContents(saveFile, fileContents);
         }
     }

     public static byte[] getFileContents(String blobId) {
         File blobFile = join(BLOBS_DIR, blobId);
         if (!blobFile.exists()) {
             return null;
         }

         Blob blob = Utils.readObject(blobFile, Blob.class);

         return blob.fileContents;
     }

    public String getBlobID() {
         return sha1(serialize(this));
    }
}
