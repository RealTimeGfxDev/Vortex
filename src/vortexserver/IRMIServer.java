package vortexserver;
import java.rmi.*;
import vortexclient.IClientCallback;
/**
 *
 * @author Diron Gunasekara
 */
public interface IRMIServer extends Remote {
    public byte[] getVideo() throws RemoteException;
    public void uploadFileToServer(byte[] mydata, String fileName, int length) throws RemoteException;
    public void registerClient(String id, IClientCallback client) throws RemoteException;
    public void unregisterClient(String id) throws RemoteException;
}

