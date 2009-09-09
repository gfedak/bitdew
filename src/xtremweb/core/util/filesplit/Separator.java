/**
 * Give the path of the big file and the size of each chunk
 * if the chunk size is larger than the file size, it will not be split, generate a .bak file
 *  each chunk is named as ".part0000"
 * Method: Input/Output Stream, and RandomAccessFile
 */
package xtremweb.core.util.filesplit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class Separator
{
  public String FileName=null; //filename of original file
  public long FileSize=0;		//size of original file
  public long BlockNum=0;		//number of blocks
  
  public Separator()
  {
  }
  
 /**
   * 
   * @param fileAndPath Directory+filename
   */
  private void getFileAttribute(String fileAndPath)//get the attributes of original file
  {
    File file=new File(fileAndPath);
    FileName=file.getName();
    FileSize=file.length();     //Bytes
  }
  
 /**
   * 
   * @param blockSize 
   * @return the number of blocks
   */
  public long getBlockNum(long blockSize)   //block size: Bytes
  {
    long fileSize=FileSize;
    if(fileSize<=blockSize)   //if the file can be split into 1 part,  only
      return 1;
    else
    {
      if(fileSize%blockSize>0)
      {
        return fileSize/blockSize+1;
      }
      else
        return fileSize/blockSize;
    }
  }
  
 /**
   * 
   * @param fileAndPath full path of original file
   * @param currentBlock #ID of current block
   * @return new name of each block
   */
  private String generateSeparatorFileName(String fileAndPath,int currentBlock)
  {
	//chunk name format      .part0000
	String str=null;
	str=String.format("%04d", currentBlock);
    return fileAndPath+".part"+str;
  }
  
 /**
   * 
   * @param fileAndPath full path of original file
   * @param fileSeparateName new name of each block,   the same directory with original file 
   * @param blockSize current block size
   * @param beginPos   pos to read from original file
   * @return true write successful false write fail
   */
  private boolean writeFile(String fileAndPath,String fileSeparateName,long blockSize,long beginPos)//write to disk
  {
    RandomAccessFile raf=null;
    FileOutputStream fos=null;
    byte[] bt=new byte[1024];   //1024 Bytes  a unit       1K  a unit
    long writeByte=0;
    int len=0;
    try
    {
      raf = new RandomAccessFile(fileAndPath,"r");
      raf.seek(beginPos);
      fos = new FileOutputStream(fileSeparateName);
      while((len=raf.read(bt))>0)
      {        
        if(writeByte<blockSize)//if it is not full!
        {
          writeByte=writeByte+len;
          if(writeByte<=blockSize)
            fos.write(bt,0,len);
          else
          {
            len=len-(int)(writeByte-blockSize);
            fos.write(bt,0,len);
          }
        }        
      }
      fos.close();
      raf.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      try
      {
        if(fos!=null)
          fos.close();
        if(raf!=null)
          raf.close();
      }
      catch(Exception f)
      {
        f.printStackTrace();
      }
      return false;
    }
    return true;
  }
  
 /**
   * 
   * @param fileAndPath full path of orinal file
   * @param blockSize  Bytes    1M : 1024*1024     1K:  1024
   * @return true , false
   */
  public boolean SepFile(String fileAndPath,long blockSize)//main function
  {
    getFileAttribute(fileAndPath);//get name and size
//    System.out.println("FileSize:"+FileSize);
//    System.out.println("blockSize:"+blockSize);
    BlockNum=getBlockNum(blockSize);//get the number of blocks
//    System.out.println("BlockNum:"+BlockNum);
    
    if(BlockNum==1)//
      blockSize=FileSize;
    long writeSize=0;  //each time to write
    long writeTotal=0; //how many has been written
    String FileCurrentNameAndPath=null;
    for(int i=1;i<=BlockNum;i++)
    {
      if(i<BlockNum)
        writeSize=blockSize;//get the size  should be written each time
      else
        writeSize=FileSize-writeTotal;
      if(BlockNum==1)
        FileCurrentNameAndPath=fileAndPath+".bak";
      else
        FileCurrentNameAndPath=generateSeparatorFileName(fileAndPath,i);
      //System.out.print("one time write"+writeSize);      
      if(!writeFile(fileAndPath,FileCurrentNameAndPath,writeSize,writeTotal))//write to disk
        return false;
      writeTotal=writeTotal+writeSize;
      //System.out.println("  total write:"+writeTotal);
      System.out.println("FileSplitting index:"+i);
    }
    return true;
  }
  
  public static void main(String[] args)
  {
    Separator separator = new Separator();
    //String fileAndPath="D:\\H\\Ubuntu\\JavaFileSplit\\a.bin";
	String fileAndPath="/home/btang/DS/gos-3.1-gadgets-20081205.iso";
    long blockSize=20*1024*1024;       //    1M : 1024*1024     1K:  1024
    if(separator.SepFile(fileAndPath,blockSize))
    {
      System.out.println("File Split Successful!");
    }
    else
    {
      System.out.println("File Split not Successful!");
    }
  }
}
