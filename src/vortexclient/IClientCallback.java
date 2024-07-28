package vortexclient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Diron Gunasekara
 */
public interface IClientCallback extends Remote {
    public void receiveFile(byte[] mydata, String clientpath, int length) throws RemoteException;
}
