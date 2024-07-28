package vortexclient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import vortexserver.IRMIServer;

/**
 *
 * @author Diron Gunasekara
 */
public class VortexReceiveStream implements Runnable {
    public IRMIServer _objServer;
    public JLabel _viewportLabel;
    
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);
        
    @Override
    public void run() {
        running.set(true);
        while(running.get()) {
            try {
                byte[] dataVideo = _objServer.getVideo();
                InputStream inputVideo = new ByteArrayInputStream(dataVideo);
                BufferedImage img = ImageIO.read(inputVideo);
                setIcon(img);
                Thread.sleep(5);
            } catch (RemoteException ex) {
                this.stop();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                Logger.getLogger(VortexReceiveStream.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                this.stop();
                Logger.getLogger(VortexReceiveStream.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public VortexReceiveStream(IRMIServer objServer, JLabel viewportLabel) {
        _objServer = objServer;
        _viewportLabel = viewportLabel;
    }
    
    public void setIcon(BufferedImage img) {
        _viewportLabel.setIcon(new ImageIcon(img));
    }
    
    public void start() {
        worker = new Thread(this);
        worker.start();
    }
    
    public void stop() {
        running.set(false);
    }
    
}
