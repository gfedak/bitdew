package xtremweb.core.poxwo;

/**
 * Describe class InterfaceRMIpoxwo
 *
 *
 * Created: Sun Sep 24 11:00:19 2006
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
import java.rmi.*;

public interface InterfaceRMIpoxwo extends Remote{

    public void remotePersist(Poxwo obj) throws RemoteException;


}
