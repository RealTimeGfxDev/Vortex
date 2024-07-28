package vortexserver;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import vortexclient.ServerOpen;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import vortexclient.IClientCallback;
import vortexclient.RandomString;

/**
 *
 * @author Diron Gunasekara
 */
public class RMIServer implements IRMIServer, Serializable{
    private String _host;
    private int _port;
    private Registry reg;
    private IRMIServer stubServer;
    HashMap<String, IClientCallback> hmap;
    
    public RMIServer() {
        super();
    }
    
    public RMIServer(String host, int port) {
        _host = host;
        _port = port;
        hmap = new HashMap<>();
    }
    
    public void open() throws RemoteException {
        String name = "rmi://" + _host + ":" + _port + "/MasterServer";
        System.setProperty("java.rmi.server.hostname", _host);
        reg = LocateRegistry.createRegistry(_port);
        stubServer = (IRMIServer) UnicastRemoteObject.exportObject(this, _port);
        reg.rebind(name, stubServer);
    }
    
    public void close() throws RemoteException {
        try 
        {
            reg.unbind("rmi://" + _host + ":" + _port + "/MasterServer");
            UnicastRemoteObject.unexportObject(this, true);
            UnicastRemoteObject.unexportObject(reg, true);
        } catch (NotBoundException | AccessException ex) {
            Logger.getLogger(ServerOpen.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    @Override
    public byte[] getVideo() throws RemoteException {
        try {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice screen = env.getDefaultScreenDevice();
            Robot robot = new Robot(screen);
            Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage img = robot.createScreenCapture(new Rectangle(0,0,dimensions.width,dimensions.height));
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ImageIO.write(img, "png", bytes);
            bytes.flush();
            byte[] data = bytes.toByteArray();
            bytes.close();
            return data;
        }
        catch(AWTException | HeadlessException | IOException e) {
            System.out.println(e);
            return null;
        }
    }

    /*@Override
    public byte[] getAudio() throws RemoteException {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        TargetDataLine microphone;
        //SourceDataLine speakers;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            microphone = AudioSystem.getTargetDataLine(format);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            int bytesRead = 0;
            //DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            //speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            //speakers.open(format);
            //speakers.start();
            //while (bytesRead < 100000) {
                numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                bytesRead += numBytesRead;
                // write the mic data to a stream for use later
                out.write(data, 0, numBytesRead); 
                // write mic data to stream for immediate playback
                //speakers.write(data, 0, numBytesRead);
            //}
            //speakers.drain();
            //speakers.close();
            microphone.close();
            return out.toByteArray();
        } catch (LineUnavailableException e) {
            return null;
        }
    }*/

    @Override
    public void uploadFileToServer(byte[] mydata, String fileName, int length) throws RemoteException {
        try {
            String pathFiles = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Vortex" + File.separator + "Files" + File.separator + fileName;
            File serverPathFile = new File(pathFiles);
            serverPathFile.getParentFile().mkdirs();
            try (FileOutputStream out = new FileOutputStream(serverPathFile)) {
                byte [] data = mydata;
                out.write(data);
                out.flush();
                System.out.println("DEBUG-SERVER: Done writing data...");
            }
	 
        } catch (IOException e) {
            e.printStackTrace();
	}
    }

    @Override
    public void registerClient(String id, IClientCallback client) throws RemoteException {
        hmap.put(id, client);
    }
    
    @Override
    public void unregisterClient(String id) throws RemoteException {
        hmap.remove(id);
    }
    
    public int totalClients() throws RemoteException {
        return hmap.size();
    }
    
    public HashMap<String, IClientCallback> getClients() throws RemoteException {
        return hmap;
    }
}
