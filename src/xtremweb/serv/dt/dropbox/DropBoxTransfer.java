package xtremweb.serv.dt.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

import xtremweb.core.conf.ConfigurationException;
import xtremweb.core.conf.ConfigurationProperties;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.serv.dt.BlockingOOBTransferImpl;
import xtremweb.serv.dt.OOBException;
import xtremweb.serv.dt.bittorrent.HttpTools;
import xtremweb.serv.dt.bittorrent.exception.HttpToolsException;

public class DropBoxTransfer extends BlockingOOBTransferImpl {
    private Properties props;
    private DropboxAPI api;
    
    
    public DropBoxTransfer(Data d, Transfer t, Locator rl, Locator ll,Protocol rp, Protocol lp) {
	super(d, t, rl, ll, rp, lp);
    }

    public void connect() throws OOBException {
	
	try {
	    props = ConfigurationProperties.getProperties();	
	    String key = props.getProperty("xtremweb.serv.dr.dropbox.app-key");
	    String secret =props.getProperty("xtremweb.serv.dr.dropbox.app-secret");
	    System.out.println("Key is " + key + " secret is " + secret);
	    AppKeyPair pair = new AppKeyPair(key, secret);
	    WebAuthSession was = new WebAuthSession(pair, AccessType.APP_FOLDER);
	    WebAuthInfo info = was.getAuthInfo();
	    RequestTokenPair tpair = info.requestTokenPair;
	    System.out.println("the key generated is " + tpair.key + "the secret generated is " + tpair.secret);
	    String redirecturl = info.url;
	    System.out.println("the redirect url is " + redirecturl);
	    Thread.sleep(45000);
	    was.retrieveWebAccessToken(tpair);
	    api = new DropboxAPI(was);
	    System.out.println("PSOOOOOOOO LA PRUEBA DE FUEGO !!!!");
	} catch (ConfigurationException e) {
	    e.printStackTrace();
	} catch (DropboxException e) {
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    public boolean poolTransfer() {
	return !isTransfering();
    }

    @Override
    public void disconnect() throws OOBException {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void blockingSendSenderSide() throws OOBException {
	String path = props.getProperty("xtremweb.serv.dr.dropbox.path") + local_locator.getdatauid();
	System.out.println("the locator is " + local_locator.getref());
	File f = new File(local_locator.getref());
	FileInputStream fis;
	try {
	    fis = new FileInputStream(f);
	    api.putFile(path,fis,f.length(),null,null);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    throw new OOBException("The file was not found " + e.getMessage());
	    
	} catch (DropboxException e) {
	    e.printStackTrace();
	    throw new OOBException("There was an exception using the DropBox API " + e.getMessage());
	    
	}
    }

    @Override
    public void blockingSendReceiverSide() throws OOBException {
	
    }

    @Override
    public void blockingReceiveSenderSide() throws OOBException {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void blockingReceiveReceiverSide() throws OOBException {
	String path = props.getProperty("xtremweb.serv.dr.dropbox.path") + data.getuid();
	System.out.println("the path is " +path);
	try {
	    OutputStream fos = new FileOutputStream(new File(local_locator.getref()));
	    api.getFile(path ,null , fos, null);
	    fos.close();
	} catch (DropboxException e) {
	    e.printStackTrace();
	    throw new OOBException("There was a dropbox exception : " + e.getMessage());
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    throw new OOBException("File not found exception " + e.getMessage());
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new OOBException("IO exception " + e.getMessage());
	}
	
	
    }

}
