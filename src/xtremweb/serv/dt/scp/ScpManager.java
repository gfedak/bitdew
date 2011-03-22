/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package xtremweb.serv.dt.scp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import xtremweb.core.log.Logger;
import xtremweb.core.log.LoggerFactory;
import xtremweb.serv.dt.ftp.FtpTransfer;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * This class interfaces bitdew with java library JSch
 * 
 * @author jsaray
 * 
 */
public class ScpManager {

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
	 * Logger
	 */
	protected static Logger log = LoggerFactory.getLogger(FtpTransfer.class);

	/**
	 * Scp manager constructor
	 * 
	 * @param user
	 *            the user
	 * @param host
	 *            the host
	 * @param lfile
	 *            local file path
	 * @param rfile
	 *            remote file path
	 */
	public ScpManager(String user, String host, String lfile, String rfile,
			String pkpath, String knownhosts, String passph) {
		this.user = user;
		this.host = host;
		this.lfile = lfile;
		this.rfile = rfile;
		PRIVATE_KEY = pkpath;
		KNOWN_HOSTS = knownhosts;
		PASSPHRASE = passph;
	}

	/**
	 * Connects JSch to host
	 */
	public void connect() {
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
	 * Disconnect session
	 */
	public void disconnect() {
		session.disconnect();
	}

	/**
	 * Securely send a file
	 */
	public void send() {
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

	public int checkAck(InputStream in) throws IOException {
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

	/**
	 * Securely receive a file
	 */
	public File receive() {
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
		return f;
	}
}
