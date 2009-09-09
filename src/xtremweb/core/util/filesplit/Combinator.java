/**
 * Combinate file
 * all the chunks in the same directory
 * Method: Input/Output Stream, and RandomAccessFile
 */
package xtremweb.core.util.filesplit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Combinator
{
  public String srcDirectory=null;//  directory      must end with "\\" in Windows   or "/" in Linux
  public String[] separatedFiles;//name of each block
  public String[][] separatedFilesAndSize;//name and size
  public int FileNum=0;//the number of blocks
  public String fileRealName="";//get the original file name
  
 /**
   * Constructor
   * @param 
   * @return 
   */
  public Combinator()
  {
  }
  
 /**
   * 
   * @param str   block directory   add "\\"      "/"
   * @return 
   */
  public void setDirectory(String str)
  {
	srcDirectory=str;
	getFileAttribute(srcDirectory);
  }
  
 /**
   * 
   * @param sFileName  name of one block
   * @return  the name of original file
   */
  public String getRealName(String sFileName)
  {
	int len=sFileName.length();
	return sFileName.substring(0,len-9);
  }
  
 /**
   * get the size of one block
   * @param FileName name of one block
   * @return
   */
  public long getFileSize(String FileName)
  {
    FileName=srcDirectory+FileName;
    return (new File(FileName).length());
  }
  
 /**
   * get attributes
   * @param drictory 
   */
  public void getFileAttribute(String drictory)
  {
    File file=new File(drictory);
    separatedFiles=new String[file.list().length];//1-D filename of each bolck
    separatedFiles=file.list();
    
    //2-D name and size
    separatedFilesAndSize=new String[separatedFiles.length][2];
    Arrays.sort(separatedFiles);//sort
    FileNum=separatedFiles.length;//how many blocks in the directory
    System.out.println("getFileAttribute FileNum="+FileNum);
    for(int i=0;i<FileNum;i++)
    {
      separatedFilesAndSize[i][0]=separatedFiles[i];//name
      separatedFilesAndSize[i][1]=String.valueOf(getFileSize(separatedFiles[i]));//size
    }
    fileRealName=getRealName(separatedFiles[FileNum-1]);//original name
  }
  
 /**
   * RandomAccessFile
   * @return true successful
   */
  public boolean CombFile()
  {
    RandomAccessFile raf=null;
    long alreadyWrite=0;
    FileInputStream fis=null;
    int len=0;
    byte[] bt=new byte[1024];
    try
    {
      raf = new RandomAccessFile(srcDirectory+fileRealName,"rw");
      for(int i=0;i<FileNum;i++)
      {
        raf.seek(alreadyWrite);
        fis=new FileInputStream(srcDirectory+separatedFilesAndSize[i][0]);
        while((len=fis.read(bt))>0)
        {
          raf.write(bt,0,len);
        }
        fis.close();
        alreadyWrite=alreadyWrite+Long.parseLong(separatedFilesAndSize[i][1]);
      }
      raf.close();      
    }
    catch (Exception e)
    {
      e.printStackTrace();
      try
      {
        if(raf!=null)
          raf.close();
        if(fis!=null)
          fis.close();
      }
      catch (IOException f)
      {
        f.printStackTrace();
      }
      return false;
    }
    return true;
  }
  
  public static void main(String[] args)
  {
   Combinator combinator = new Combinator();    
   combinator.setDirectory("/home/btang/DS/");
    if (combinator.CombFile())
    {
      System.out.println("Combination successful");
    }
    else
    {
      System.out.println("Combination not successful");
    }
  }
} 
