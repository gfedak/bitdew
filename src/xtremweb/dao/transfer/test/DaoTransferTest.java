package xtremweb.dao.transfer.test;

import java.util.Collection;

import org.junit.Test;

import junit.framework.TestCase;
import xtremweb.api.transman.TransferStatus;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.dao.DaoFactory;
import xtremweb.dao.transfer.DaoTransfer;

public class DaoTransferTest extends TestCase{
    
 
    public void setUp()
    {
	
    }
    
    @Test
    public void testAll()
    {
	Transfer t1 =  new Transfer();
	t1.setstatus(TransferStatus.READY);
	Transfer t2 = new Transfer();
	t2.setstatus(TransferStatus.ABORTED);
	Transfer t3 =new Transfer();
	t3.setstatus(TransferStatus.COMPLETE);
	
	Transfer t4 =new Transfer();
	t4.setstatus(TransferStatus.INVALID);
	Transfer t5 =new Transfer();
	t5.setstatus(TransferStatus.STALLED);
	
	Transfer t6 = new Transfer();
	
	t6.setstatus(TransferStatus.READY);
	
	DaoTransfer dao = (DaoTransfer)DaoFactory.getInstance("xtremweb.dao.transfer.DaoTransfer");
	
	dao.makePersistent(t1,true);
	dao.makePersistent(t2,true);
	dao.makePersistent(t3,true);
	dao.makePersistent(t4,true);
	dao.makePersistent(t5,true);
	dao.makePersistent(t6,true);
	dao.beginTransaction();
	Collection col = dao.getTransfersDifferentStatus(TransferStatus.READY);
	assertNotNull(col);
	assertEquals(col.size(),4);
	dao.commitTransaction();
	
	dao.beginTransaction();
	Collection byst = (Collection) dao.getTransfersByStatus(TransferStatus.READY, false, null);
	assertNotNull(byst);
	assertEquals(byst.size(),2);
	
	
	Long count  = (Long) dao.getTransfersByStatus(TransferStatus.READY, true, "uid");
	assertNotNull(count);
	assertEquals(count,new Long(2));
	dao.commitTransaction();
	
	Data d1 = new Data();
	dao.makePersistent(d1, true);
	dao.beginTransaction();
	//t2 = (Transfer)dao.detachCopy(t2);
	//t3 = (Transfer)dao.detachCopy(t3);
	t2.setdatauid(d1.getuid());
	t3.setdatauid(d1.getuid());
	dao.commitTransaction();
	dao.makePersistent(t2, true);
	dao.makePersistent(t3, true);
	dao.beginTransaction();
	Collection c = dao.getTransfersByDataUid(d1.getuid());
	dao.commitTransaction();
	assertNotNull(c);
	assertEquals(c.size(),2);
	
    }
    
    
    
    
    
    
    @Test
    public void tearDown()
    {
	
    }

}
