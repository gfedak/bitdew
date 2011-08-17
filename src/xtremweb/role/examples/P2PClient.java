package xtremweb.role.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Vector;

import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.api.transman.TransferManager;
import xtremweb.api.transman.TransferManagerException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.iface.InterfaceRMIdt;
import xtremweb.core.obj.dc.Data;
import xtremweb.role.examples.obj.SongBitdew;
import xtremweb.serv.dt.OOBTransfer;

public class P2PClient {

	/**
	 * 
	 */
	private BitDew bitdew;
	private BitDew bitdewlocal;
	private InterfaceRMIdc dc ;
	private InterfaceRMIdt dt ;
	private InterfaceRMIdr dr ;
	private InterfaceRMIds ds ;
	/**
	 * 
	 */
	private TransferManager tm;
	
	/**
	 * 
	 * @param bootstrap
	 */
	public P2PClient(String bootstrap) {
		
		
		try {
			dc = (InterfaceRMIdc) ComWorld.getComm(bootstrap, "rmi", 4325, "dc");
			dt = (InterfaceRMIdt) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "dt");
			ds = (InterfaceRMIds) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "ds");
			
		
		tm = new TransferManager(dt);
		bitdew = new BitDew(dc,dr,dt,ds);
		tm.start();
		} catch (ModuleLoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/** args[0]  bootstrapnode
	 * args[1] term to search (if get)
	 */
	public static void main(String[] args) {
		P2PClient p2p = new P2PClient(args[0]);
		p2p.get(args[1]);
	}
	
	/**
	 * 
	 * @param term
	 */
	public void get(String term) {	
		try {
			System.out.println("Search for songs artist or title : ");
			BufferedReader stdIn = null;
			
			System.out.println("entro al get jijuepuerca vida ");
			List results;
			results = bitdew.ddcSearch(term);
			System.out.println("the value of results is " + results + " and the size is " + results.size());
			for (int i = 1; i <= results.size(); i++) {
				SongBitdew sbd = (SongBitdew) results.get(i-1);
				System.out.println(" Results for your query : " + i+ ". " +sbd.getFilename() + " ");
			}
			
			System.out.println("Please write the song number you wish to download : ");		
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			String number = stdIn.readLine();		
			int nu = Integer.parseInt(number);
			
			String md5 = ((SongBitdew)results.get(nu-1)).getMd5();
			List ips = bitdew.ddcSearch(md5);
			
			for(int i = 0 ; i < ips.size() ; i++ )
			{
				System.out.println("IP  "  + i + " is" + ips.get(i));
			}
			
			if (ips != null && ips.size()!=0){
				dr = (InterfaceRMIdr) ComWorld.getComm((String)ips.get(0), "rmi", 4325, "dr");
				dc = (InterfaceRMIdc) ComWorld.getComm((String)ips.get(0), "rmi", 4325, "dc");
			}
			else
				throw new BitDewException("There is not ip for that md5 ! ");
			bitdew = new BitDew(dc,dr,dt,ds);
			download(results,nu);
		} catch (BitDewException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ModuleLoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @param songname
	 * @param num
	 */
	public void download(List songname,int num) {
		try {
		SongBitdew sbd = (SongBitdew)songname.get(num-1);
		File file = new File(sbd.getFilename());
		String md5 = sbd.getMd5();
		System.out.println("md5 ids " + md5);
		Data d = bitdew.getDataFromMd5(md5);	
		d.setoob("http");
		OOBTransfer oob;		
		oob = bitdew.get(d,file);
		tm.registerTransfer(oob);
		tm.waitFor(d);
		System.out.println("File : " + sbd.getFilename() +" was successfully downloaded ");
		} catch (BitDewException e) {
			e.printStackTrace();
		} catch (TransferManagerException e) {
			e.printStackTrace();
		}
	}
}
