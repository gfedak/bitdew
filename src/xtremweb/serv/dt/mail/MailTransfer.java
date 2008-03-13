package xtremweb.serv.dt.mail;

/**
 * Describe class MailTransfer here.
 *
 *
 *
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.core.log.*;
import xtremweb.core.uid.*;
import xtremweb.serv.dt.*;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.core.obj.dc.Data;
import xtremweb.serv.dc.DataUtil;
import xtremweb.core.obj.dc.Locator;

import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.security.*;
import java.util.Properties;

import com.sun.mail.smtp.*;

public class MailTransfer 
    extends BlockingOOBTransferImpl 
    implements BlockingOOBTransfer, OOBTransfer {


    String to = "yo.superyo@gmail.com"; 
    String subjectHeader = "BitDew ";
    String from = "admin@bitdew.com"; 
    String url = null;
    String smtpMailhost = "smtp.gmail.com";
    String popMailhost = "pop.gmail.com";
    int popPort = 995;
    String folderName = "INBOX";
    String mailer = "smtpOOBTransfer";
    String smtpProtocol = "smtps"; 
    String popProtocol = "pop3s"; 
    String host = "smtp.gmail.com";
    String user = "yo.superyo@gmail.com";
    String password = "1245superyo";
    String smtpUser = "sender.smtpTransfer@gmail.com";
    String smtpPassword = "smtpPassword";
    String record = "Store1";	// name of folder in which to record mail
    boolean debug = true;
    boolean verbose = true;
    boolean auth = true;
    boolean ssl = true;
    String tmpDir = "tmp";    
    Session session;
    
    protected static  Logger log = LoggerFactory.getLogger(MailTransfer.class);

    /**
     * Creates a new <code>MailTransfer</code> instance.
     *
     */
    public MailTransfer(Data d, Transfer t, Locator rl, Locator ll, Protocol rp,  Protocol lp ) {
	super(d,t,rl,ll,rp,lp);
	transfer.setoob(this.getClass().toString());
    } 

    public String toString() {
	return "smtp|pop://[" + remote_protocol.getlogin() + ":" +  remote_protocol.getpassword() +  "]@" + remote_locator.getdrname() + ":" +  remote_protocol.getport();
    }

    public void connect ()  throws OOBException {
	Properties props = System.getProperties();

	props.put("mail.smtps.host", smtpMailhost);
	props.put("mail.smtps.auth", "true");

	session = Session.getInstance(props, null);
	if (debug)
	    session.setDebug(true);

    }

    public void blockingSendSenderSide      () throws OOBException {
	try {
	    Message msg = new MimeMessage(session);

	    msg.setFrom(new InternetAddress(from));
	    msg.setRecipients(Message.RecipientType.TO,
			      InternetAddress.parse(to, false));

	    //setting the subject
	    String subject  = subjectHeader + data.getuid();
	    msg.setSubject( subject );
	    log.debug("setting subject to " +  subject);
	    
	    //setting the message body
	    BodyPart messageBodyPart = new MimeBodyPart();
	    
	    Multipart multipart = new MimeMultipart();
	    
	    DataSource source = new FileDataSource(local_locator.getref());
	    messageBodyPart.setDataHandler(new DataHandler(source));
	    messageBodyPart.setFileName(local_locator.getref());
	    multipart.addBodyPart(messageBodyPart);
	    
	    // Put parts in message
	    msg.setContent(multipart);
	    SMTPTransport transport = (SMTPTransport) session.getTransport(smtpProtocol);
	    
	    //	try {
	    transport.connect(smtpMailhost, smtpUser, smtpPassword);	    
	    transport.sendMessage(msg, msg.getAllRecipients());

	    log.debug("\nMail was sent successfully.");

	    log.debug("Response: " + transport.getLastServerResponse());
	    transport.close();

	} catch (SendFailedException sfe ) {
	    log.debug("Mail has not been sent correctly " + sfe );
	    throw new OOBException("cannot send mail " + toString() + "/" + remote_locator.getref() );
	} catch (MessagingException me ) {
	    log.debug("Cannot create a correct mail message " + me );
	    throw new OOBException("cannot send mail " + toString() + "/" + remote_locator.getref() );	
	}
    }

    public void blockingSendReceiverSide    () throws OOBException {
	//void
    }

    public void blockingReceiveSenderSide   () throws OOBException {
	//void
    }

    public void blockingReceiveReceiverSide () throws OOBException {
	
	try {
	    URLName urln = new URLName(popProtocol, popMailhost, popPort, folderName, user, password);
	    Store store = session.getStore( urln);
	    log.debug("connecting to " + urln) ;
	    store.connect();
      
	    Folder folder = store.getFolder("INBOX");
	    folder.open(Folder.READ_ONLY);

	    Message msg[] = folder.getMessages();
	    
	    FetchProfile fp=new FetchProfile();
	    fp.add("Subject");
	    
	    folder.fetch(msg,fp);
	    // Get directory
	    log.debug("Total message :  " + folder.getMessageCount() );
	    
	    for (int i=0, n=msg.length; i<n; i++) {
		log.debug("MSG: " + i + ": " + "\t" + msg[i].getSubject());
		if (msg[i].getSubject().equals(subjectHeader + data.getuid())) {
		    log.debug("MessageType " + msg[i].getContentType() );
		    
		    Object o = msg[i].getContent();
		    if (o instanceof Multipart) {
			Multipart mp = (Multipart) o;
			log.debug("Multipart " + mp.getCount() );
			
			for (int j = 0; j < mp.getCount(); j++) {
			    Part p = mp.getBodyPart(j);
			    if  (p instanceof MimeBodyPart) {
				log.debug("Saving attachment to " + local_locator.getref() );
				((MimeBodyPart) p).saveFile(local_locator.getref());
				//			System.out.println);
			    }
			}
		    }
		}
	    }
	    store.close();
	} catch (MessagingException me ) {
	    log.debug("Cannot fetch mail " + me );
	    throw new OOBException("cannot fetch mail " + toString() + "/" + remote_locator.getref() );
	} catch (IOException ioe ) {
	    log.debug("Cannot fetch mail " + ioe );
	    throw new OOBException("cannot fetch mail " + toString() + "/" + remote_locator.getref() );
	}
    }

    public void disconnect() throws OOBException {
    }

    public static void main(String [] args) {
      
	Data data = DataUtil.fileToData(new File("/tmp/localcopy"));

	//Preparer le local
	Protocol local_proto = new Protocol();
	local_proto.setname("local");
	
	Locator local_locator = new Locator();
	local_locator.setdatauid(data.getuid());
	local_locator.setdrname("localhost");
	local_locator.setprotocoluid(local_proto.getuid());
	local_locator.setref("/tmp/localcopy");
	
	// Preparer le proto pour l'acces remote 
	// FIXME
	Protocol remote_proto = new Protocol();
	remote_proto.setname("mail");
	remote_proto.setport(21);
	remote_proto.setlogin("anonymous");
	remote_proto.setpassword("fedak@lri.fr");
	
	Locator remote_locator = new Locator();
	remote_locator.setdatauid(data.getuid());
	remote_locator.setdrname("localhost");
	remote_locator.setprotocoluid(remote_proto.getuid());
	remote_locator.setref("test.ps");

	//prepare the transfer
	Transfer t = new Transfer();
	t.setlocatorremote(remote_locator.getuid());
	t.setlocatorlocal(local_locator.getuid());
	
	MailTransfer mt = new MailTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);	

	try {
	    mt.persist();
	    mt.connect();    
	    mt.blockingSendSenderSide();
	    mt.disconnect();
	} catch(OOBException oobe) {
	    System.out.println(oobe);
	}

	remote_locator.setref(data.getuid());
	remote_proto.setpath("pub/incoming");

	mt = new MailTransfer(data, t, remote_locator, local_locator, remote_proto, local_proto);

	try {
	    mt.connect();	    
	    mt.blockingReceiveReceiverSide();
	    mt.disconnect();
	} catch(OOBException oobe) {
	    System.out.println(oobe);
	}	
    }
}
