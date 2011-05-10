package xtremweb.role.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.serv.dt.BlockingOOBTransferImpl;
import xtremweb.serv.dt.OOBException;


/**
 * Scp transfer implementation
 * @author jsaray
 *
 */
public class ScpTransfer extends BlockingOOBTransferImpl {
    
    /*! \example ScpTransfer.java 
     * This example shows how to implement a transfer in BitDew.
     * For this purpose, a scp transfer is implemented using Bitdew's multitransfer management toolbox. 
     * <ol> 
     * 
     * <li> We use the <a href="http://www.jcraft.com/jsch/">JSch library</a> that implements transfers in a blocking way, the class provides <em>send()</em> and <em>receive()</em> methods
     * to safely send and receive a file.
     * </li>
     * 
     * <li>Two principal interfaces are provided by bitdew to implement a new transfer: <em>NonBlockingOOBTransfer</em> and <em>BlockingOOBTransfer</em>, moreover, an implementation by default of these classes is provided in 
     * <em>NonBlockingOOBTransferImpl</em> and <em>BlockingOOBTransferImpl</em> respectively. We define a Blocking transfer as those whose we know if the transfer success
     * or the transfer error code, as soon as methods <em>send</em> and <em>receive</em> of implementation finish (commonly in libraries implementing http,ftp,smtp etc). 
     * Jsch implementation works in this way, thats why we choose BlockingOOBTransferImpl interface to build our SCP transfer</li>
     * @code
     * public class ScpTransfer extends BlockingOOBTransferImpl {	
     * @endcode
     * <br>
     * 
     * 
     * On the other hand, we define a Non Blocking transfer as the contrary, that is, those whose we do not know anything about the transfer once the method send or receive finishes. 
     * This is the case of Bittorrent transfers, where once we decide seeding, we don't know if the file arrived to the sender/receiver. Please note that the Blocking/Non-Blocking transfer nature is an implementation
     * detail rather than an intrinsic property of the protocol, that means we could implement a http transfer in a NonBlocking way by using Threads.
     * 
     * 
     * <li>In constructor we initialize  the SCP transfer state and we invoke the parent constructor (BlockingOOBTransferImpl) with the required fields.
     * Common properties of a scp transfer are hosting server, user login, private ssh key path, passphrase and local/remote files routes.</li>
     * @code
     * 
     * public ScpTransfer(Data d,Transfer t,Locator l1, Locator l2, Protocol p1, Protocol p2)
     * {	super(d,t,l1,l2,p1,p2);
     *		this.user = remote_protocol.getlogin();
     *		this.host = remote_protocol.getserver();
     *		this.lfile = local_locator.getref();
     *		this.rfile = remote_locator.getref();
     *		PRIVATE_KEY = remote_protocol.getprivatekeypath();
     *		KNOWN_HOSTS = remote_protocol.getknownhosts();
     *		PASSPHRASE = remote_protocol.getpassphrase();
     * }
     * 
     * @endcode
     * 
     * 
     * <li> Next, five methods needs to be implemented </li>
     * 
     * @code
     * 
     *  public void connect() throws OOBException {}
     *  
     * 	public void blockingSendSenderSide() throws OOBException {}
     * 
     * 	public void blockingReceiveSenderSide() throws OOBException {}
     * 
     * 	public void blockingReceiveReceiverSide() throws OOBException {}
     * 
     * 	public void blockingSendReceiverSide() throws OOBException {}
     * 
     * @endcode 
     * <li> Method connect is used to initialize connection parameters if neccessary, key setup for example can be done here </li>
     * @code
     * public void connect() throws OOBException {
		   JSch jsch = new JSch();
		       try {
			   jsch.addIdentity(PRIVATE_KEY, PASSPHRASE);
			   jsch.setKnownHosts(KNOWN_HOSTS);
			   session = jsch.getSession(user, host, 22);
			   session.connect();
		   } catch (JSchException e) {
		         e.printStackTrace();
		   }
	   }
     * @endcode
     * 
     * <li> Depending on our transfer type, we will choose the methods to implement. In the case of SCP transfer using JSch library, 
     * the sender is able to know if the receiver successfully receive the file or not, only by using the sender method (as it is a synchronous transfer, it will return exceptions if anything
     * fails), in the same way, if we want to receive a file using Jsch, we will be able to know the result of our invokation only by regarding
     * the receive return. For this reason, we only need to implement <em>blockingSendSenderSide</em> and 
     * <em>blockingReceiveReceiverSide</em> methods. This is not the case of all transfers. For example, in a bittorrent transfer (normally implemented 
     * as non-blocking transfer), the sender can not know the download result directly from the send return. In this case implementing sendReceiver side
     * could fix the problem.
     * The following code shows how we can integrate JSch way of sending/receiving a file using Bitdew.  
     * </li>
     * @code 
     *   public void blockingSendSenderSide() throws OOBException {
     *	     FileInputStream fis = null;
	 *		 try {
	 *		     log.debug("enter send scpmanager session is " + session);
	 *		     // exec 'scp -t rfile' remotely
     *  			     String command = "scp -p -t " + rfile;
	 *		     Channel channel = session.openChannel("exec");
	 *	 	     ((ChannelExec) channel).setCommand(command);
	 *		     // get I/O streams for remote scp
	 *		     OutputStream out = channel.getOutputStream();
	 *		     InputStream in = channel.getInputStream();
	 *		     channel.connect();
	 *		     if (checkAck(in) != 0) {
	 *			     System.exit(0);
	 *		     }
	 *			 // send "C0644 filesize filename", where filename should not include
	 *		     // '/'
	 *			 log.debug("lfile value " + lfile);
	 *			 long filesize = (new File(lfile)).length();
	 *			 command = "C0644 " + filesize + " ";
	 *			 if (lfile.lastIndexOf('/') > 0) {
	 *			     command += lfile.substring(lfile.lastIndexOf('/') + 1);
	 *			 } else {
	 *			     command += lfile;
	 *		     }
	 *		     command += "\n";
	 *		     out.write(command.getBytes());
	 *		     out.flush();
	 *		     if (checkAck(in) != 0) {
	 *			     System.exit(0);
	 *		     }
	 *		     // send a content of lfile
	 *		     fis = new FileInputStream(lfile);
			     byte[] buf = new byte[1024];
			     while (true) {
				     int len = fis.read(buf, 0, buf.length);
				     if (len <= 0)
					 break;
				     out.write(buf, 0, len); // out.flush();
			     }
			     fis.close();
			     fis = null;
			     // send '\0'
			     buf[0] = 0;
			     out.write(buf, 0, 1);
			     out.flush();
			     if (checkAck(in) != 0) {
				     System.exit(0);
			     }
			     out.close();
			     channel.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
			}
		}
		log.debug("out send scpmanager");
     *   }
     *   
     *   public void blockingReceiveReceiverSide() throws OOBException {
		File f = null;
		FileOutputStream fos = null;
		try {

			String prefix = null;
			if (new File(lfile).isDirectory()) {
				prefix = lfile + File.separator;
			}
			// exec 'scp -f rfile' remotely
			String command = "scp -f " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] buf = new byte[1024];
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}

				// System.out.println("filesize="+filesize+", file="+file);

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				f = new File(prefix == null ? lfile : prefix + file);
				fos = new FileOutputStream(f);
				int foo;
				while (true) {
					if (buf.length < filesize)
						foo = buf.length;
					else
						foo = (int) filesize;
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
				}
				fos.close();
				fos = null;

				if (checkAck(in) != 0) {
					System.exit(0);
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (fos != null)
					fos.close();
			} catch (Exception ee) {
			}
		}

	}
     * @endcode 
     * 
     *  <li>Because of scp relies on tcp, we are sure that as soon as the <em>blockingSendSenderSide</em>, <em>blockingReceiveReceiverSide</em> methods end, we know
     * the transfer status, BlockingOOBTranfer provide <em>isTransferring</em> method that will return false wen the transfer ends. This method is called next</li> 
     * @code
     * 
     * public boolean poolTransfer()
     * {
     *     return !isTransfering();
     * }
     * @endcode Application code source : </ol>
     */
	/**
	 * Log
	 */
	protected static Logger log = LoggerFactory.getLogger(ScpTransfer.class);

	/**
	 * User attempting to stablish a scp connection
	 */
	private String user;

	/**
	 * Host the user wants to connect to
	 */
	private String host;

	/**
	 * Name of Local file
	 */
	private String lfile;

	/**
	 * Name of remote file
	 */
	private String rfile;

	/**
	 * Session between user and host
	 */
	private Session session;

	/**
	 * Path to private key
	 */
	private String PRIVATE_KEY;

	/**
	 * Private key passphrase
	 */
	private String PASSPHRASE;

	/**
	 * Known Hosts path
	 */
	private String KNOWN_HOSTS;

	/**
	 * Scp transfer contructor
	 * 
	 * @param d
	 *            data, the data to transfer
	 * @param t
	 *            transfer, the transfer
	 * @param l1
	 *            Local locator
	 * @param l2
	 *            Remote locator
	 * @param p1
	 *            Local protocol
	 * @param p2
	 *            Remote protocol
	 */
	public ScpTransfer(Data d,Transfer t,Locator l1, Locator l2, Protocol p1, Protocol p2)
    {	super(d,t,l1,l2,p1,p2);
    	this.user = remote_protocol.getlogin();
    	this.host = remote_protocol.getserver();
    	this.lfile = local_locator.getref();
    	System.out.println("lfile is " + lfile);
    	this.rfile = remote_locator.getref();
    	System.out.println("rfile is " + rfile);
    	PRIVATE_KEY = remote_protocol.getprivatekeypath();
    	KNOWN_HOSTS = remote_protocol.getknownhosts();
    	PASSPHRASE = remote_protocol.getpassphrase();
    }

	/**
	 * String representation
	 */
	public String toString() {
		return "Send : " + remote_protocol.getserver() + " user : "
				+ remote_protocol.getlogin() + " key: "
				+ remote_protocol.getpassword();
	}

	/**
	 * Securely send a file
	 */
	public void blockingSendSenderSide() throws OOBException {
		FileInputStream fis = null;
		try {
			log.debug("enter send scpmanager session is " + session);
			// exec 'scp -t rfile' remotely
			String command = "scp -p -t " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			// send "C0644 filesize filename", where filename should not include
			// '/'
			log.debug("lfile value " + lfile);
			long filesize = (new File(lfile)).length();
			command = "C0644 " + filesize + " ";
			if (lfile.lastIndexOf('/') > 0) {
				command += lfile.substring(lfile.lastIndexOf('/') + 1);
			} else {
				command += lfile;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}

			// send a content of lfile
			fis = new FileInputStream(lfile);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			out.close();
			channel.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
			}
		}
		log.debug("out send scpmanager");
	}

	/**
	 * This method is unimplemented due to the scp transfer's nature
	 */
	public void blockingReceiveSenderSide() throws OOBException {
	}

	/**
	 * Securely connect
	 */
	public void blockingReceiveReceiverSide() throws OOBException {
		File f = null;
		FileOutputStream fos = null;
		try {

			String prefix = null;
			if (new File(lfile).isDirectory()) {
				prefix = lfile + File.separator;
			}
			// exec 'scp -f rfile' remotely
			String command = "scp -f " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] buf = new byte[1024];
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}

				// System.out.println("filesize="+filesize+", file="+file);

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				f = new File(prefix == null ? lfile : prefix + file);
				fos = new FileOutputStream(f);
				int foo;
				while (true) {
					if (buf.length < filesize)
						foo = buf.length;
					else
						foo = (int) filesize;
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
				}
				fos.close();
				fos = null;

				if (checkAck(in) != 0) {
					System.exit(0);
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (fos != null)
					fos.close();
			} catch (Exception ee) {
			}
		}

	}

	/**
	 * Securely connect to the host
	 */
	public void connect() throws OOBException {
		log.debug("enter to connect ");
		JSch jsch = new JSch();
		try {
			jsch.addIdentity(PRIVATE_KEY, PASSPHRASE);
			jsch.setKnownHosts(KNOWN_HOSTS);
			log.debug("user " + user + "host : " + host);
			session = jsch.getSession(user, host, 22);
			session.connect();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Securely disconnect from the host
	 */
	public void disconnect() throws OOBException {
		session.disconnect();
	}

	/**
	 * Pool the transfer (blocking transfer, so it returns inmediatly
	 */
	public boolean poolTransfer() {
		return !isTransfering();
	}

	public void blockingSendReceiverSide() throws OOBException {
	}

	private int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}
}