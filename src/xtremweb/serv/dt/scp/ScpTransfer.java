package xtremweb.serv.dt.scp;


import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.serv.dt.BlockingOOBTransferImpl;
import xtremweb.serv.dt.OOBException;
import xtremweb.serv.dt.ftp.FtpTransfer;

public class ScpTransfer extends BlockingOOBTransferImpl {
    protected static  Logger log = LoggerFactory.getLogger(ScpTransfer.class);
    private ScpManager scpm;
    
    public ScpTransfer(Data d,Transfer t,Locator l1, Locator l2, Protocol p1, Protocol p2)
    {	super(d,t,l1,l2,p1,p2);//String user, String host, String lfile, String rfile
    	log.debug("Crucial data for SCPTransfer : login " +  remote_protocol.getlogin() + " \n");
    	log.debug("                               host  " +  remote_protocol.getserver() + " \n");
    	log.debug("                               localfile address " + local_locator.getref());
    	log.debug("                               remotefile address " + remote_locator.getref());
	scpm = new ScpManager(remote_protocol.getlogin(),remote_protocol.getserver(),local_locator.getref(),remote_locator.getref());
    }
    
    public String toString()
    {
	return "Send : " + remote_protocol.getserver() +" user : "+ remote_protocol.getlogin() + " key: "+ remote_protocol.getpassword();
    }
    
    @Override
    public void blockingSendSenderSide() throws OOBException {
	try {log.debug("enter blocksendsenderside scptransfer");
	    scpm.send();
	    log.debug("out blocksendsenderside scptransfer");
	} catch (Exception e) {
	    System.out.println("Error in blockingSendSenderSide, scptransfer");
	    e.printStackTrace();
	}
    }
    @Override
    public void blockingSendReceiverSide() throws OOBException {
    }

    @Override
    public void blockingReceiveSenderSide() throws OOBException {
    }

    @Override
    public void blockingReceiveReceiverSide() throws OOBException {
	scpm.receive();
    }

    @Override
    public void connect() throws OOBException {
	scpm.connect();
    }
    
    @Override
    public void disconnect() throws OOBException {
	scpm.disconnect();

    }

}
