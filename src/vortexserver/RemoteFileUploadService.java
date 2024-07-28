package vortexserver;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Diron Gunasekara
 */
public class RemoteFileUploadService {
    public void uploadFile(String fileName, RemoteInputStream remoteFileData) {
        try {
            InputStream fileData = RemoteInputStreamClient.wrap(remoteFileData);
            // ... copy fileData to local storage ...
        } catch (IOException ex) {
            Logger.getLogger(RemoteFileUploadService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
