package xtremweb.serv.dt.test;

import java.rmi.RemoteException;

import org.junit.Test;

import xtremweb.api.transman.TransferStatus;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.serv.dt.Callbackdt;
import junit.framework.TestCase;

/**
 * A test of Callback dt class
 * @author jsaray
 *
 */
public class CallbackdtTest extends TestCase {
    
    /**
     * Set Up
     */
    public void setUp()
    {
	
    }
    
    /**
     * Test class
     */
   @Test 
    public void testClass()
    {
	Callbackdt dt;
	
	dt = new Callbackdt();
	Transfer t1 = new Transfer();
	t1.setstatus(TransferStatus.PENDING);
	
	Transfer t2 = new Transfer();
	t2.setstatus(TransferStatus.PENDING);
	
	Transfer t3 = new Transfer();
	t3.setstatus(TransferStatus.PENDING);
	
	String uid1;
	try {
	    uid1 = dt.putTransfer(t1);
	    String uid2 = dt.putTransfer(t2);
	    String uid3 = dt.putTransfer(t3);
	
	    boolean b1 = dt.poolTransfer(uid1);
	    assertFalse(b1);
	    boolean b2 = dt.poolTransfer(uid2);
	    assertFalse(b2);
	    boolean b3 = dt.poolTransfer(uid3);
	    assertFalse(b3);
	
	    dt.setTransferStatus(uid1,TransferStatus.COMPLETE);
	    dt.setTransferStatus(uid2, TransferStatus.COMPLETE);
	    dt.setTransferStatus(uid3, TransferStatus.COMPLETE);
	    
	    boolean b4 = dt.poolTransfer(uid1);
	    assertTrue(b4);
	    boolean b5 = dt.poolTransfer(uid2);
	    assertTrue(b5);
	    boolean b6 = dt.poolTransfer(uid3);
	    assertTrue(b6);
	    
	    
	
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }
}
