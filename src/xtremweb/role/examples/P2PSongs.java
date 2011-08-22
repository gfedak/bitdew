package xtremweb.role.examples;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import xtremweb.api.bitdew.BitDew;
import xtremweb.api.bitdew.BitDewException;
import xtremweb.core.com.idl.ComWorld;
import xtremweb.core.com.idl.ModuleLoaderException;
import xtremweb.core.iface.InterfaceRMIdc;
import xtremweb.core.iface.InterfaceRMIdr;
import xtremweb.core.iface.InterfaceRMIds;
import xtremweb.core.iface.InterfaceRMIdt;
import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.role.examples.obj.SongBitdew;
import xtremweb.serv.dc.DataUtil;

public class P2PSongs {
	

	private InterfaceRMIdc dc ;
	private InterfaceRMIdt dt ;
	private InterfaceRMIdr dr;
	private InterfaceRMIds ds;
	private InterfaceRMIdc ddc;
	private BitDew bitdew;
	private BitDew bitdewddc;
	public P2PSongs(String bootstrap)
	{	
		try {
			dc = (InterfaceRMIdc) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "dc");
			dt = (InterfaceRMIdt) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "dt");
			dr = (InterfaceRMIdr) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "dr");
			ds = (InterfaceRMIds) ComWorld.getComm(InetAddress.getLocalHost().getHostAddress(), "rmi", 4325, "ds");
			
			ddc = (InterfaceRMIdc) ComWorld.getComm(bootstrap, "rmi", 4325, "dc");
			bitdewddc = new BitDew( ddc,dr,dt,ds );
			bitdew = new BitDew( dc,dr,dt,ds );
			
			
		} catch (ModuleLoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void put(String dirpath)
	{try {
		System.out.println("puting file from folder " + dirpath);
		File dir = new File(dirpath);
		File[] files = dir.listFiles();
		System.out.println("files size is " + files.length);
		for(int j = 0 ; j < files.length ; j++)
		{	//toks : tokens making part of files[j] name
			String[] toks = files[j].getName().split("[\\s\\._-]");
			File file = files[j];
			Data d = bitdew.createData(file);
			for( int i =0 ; i < toks.length; i++ )
			{	System.out.println("Parsing token " + toks[i]);
				
				
				d.setoob("http");
				Locator loc = bitdew.createRemoteLocator(d,"http");
				bitdew.associateDataLocator(d,loc);
				SongBitdew sb;
				String md5 = DataUtil.checksum(file);
				sb = new SongBitdew(file.getName(),md5);
				System.out.println("Putting song " + file.getName() + " with uid " + d.getuid() + "md5 is " + md5);
				
				
				bitdewddc.ddcPublish(toks[i],sb);
				bitdewddc.ddcPublish(md5,InetAddress.getLocalHost().getHostAddress());
				
			}
			bitdew.put(file,d);
		}
	}catch (BitDewException e) {
		e.printStackTrace();
	}catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	
	
	/**
	 * args[0] bootstrap node
	 * args[1] songs directory
	 * @param args
	 */
	public static void main(String[] args)
	{
		P2PSongs p2p = new P2PSongs(args[0]);
		p2p.put(args[1]);
		
		
	}

}
