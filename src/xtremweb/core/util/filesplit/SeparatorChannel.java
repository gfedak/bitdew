/**
 * Give the path of the big file and the size of each chunk
 * if the chunk size is larger than the file size, it will not be split, generate a .part0001 file
 *  each chunk is named as ".part0001"
 * Method: FileChannel
 */
package xtremweb.core.util.filesplit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import java.io.*;
import java.nio.channels.*;

public class SeparatorChannel {

    public String FileName = null;    //filename of original file
    public long FileSize = 0;		//size of original file
    public long BlockNum = 0;		//number of blocks
    
    public SeparatorChannel() {
    }
    
    /**
     * 
     * @param fileAndPath Directory+filename
     */
    private void getFileAttribute(String fileAndPath){ //get the attributes of original file
	File file = new File(fileAndPath);
	FileName = file.getName();
	FileSize = file.length();     //Bytes
    }
    
    /**
     * 
     * @param blockSize 
     * @return the number of blocks
     */
    public long getBlockNum(long blockSize){   //block size: Bytes
	long fileSize = FileSize;
	if(fileSize<=blockSize)   //if the file can be split into 1 part,  only
	    return 1;
	else{
	    if(fileSize%blockSize>0){
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
    private String generateSeparatorFileName(String fileAndPath,int currentBlock){
	//chunk name format      .part0000
	String str = null;
	str = String.format("%04d", currentBlock);
	return fileAndPath+".part"+str;
    }
    
    /**
     * 
     * @param fileAndPath full path of orinal file
     * @param blockSize  Bytes    1M : 1024*1024     1K:  1024
     * @return true , false
     */
    public boolean SepFile(String fileAndPath,long blockSize) throws IOException { 
	getFileAttribute(fileAndPath); //get name and size
	BlockNum=getBlockNum(blockSize); //get the number of blocks
	
	if(BlockNum==1)
	    blockSize=FileSize;
	long count = 0;   //each time to write
	long writeTotal = 0;  //how many has been written
	String FileCurrentNameAndPath = null;
	File in = new File(fileAndPath);
	for(int i=1; i<=BlockNum; i++) {
	    if(i<BlockNum)
		count = blockSize; //get the size  should be written each time
	    else
		count = FileSize-writeTotal;
	    if(BlockNum==1)
		FileCurrentNameAndPath = fileAndPath+".part0001";
	    else
		FileCurrentNameAndPath = generateSeparatorFileName(fileAndPath,i);      
	    if( !copyFile(in, FileCurrentNameAndPath, writeTotal, count) )  //write to disk
		return false;
	    writeTotal=writeTotal + count;
	    System.out.println("FileSplitting index:"+i);
	}
	return true;
    }

    /**
     * 
     * @param File in full path of original file
     * @param fileSeparateName new name of each block,   the same directory with original file 
     * @param count current block size
     * @param beginPos   pos to read from original file
     * @return true write successful false write fail
     */
    public boolean copyFile(File in, String out, long position, long count) throws IOException {
	FileChannel inChannel = new FileInputStream(in).getChannel();
	FileChannel outChannel = new FileOutputStream(new File(out)).getChannel();
	try {
	    inChannel.transferTo(position, count, outChannel);
	}
	catch (IOException e) {
	    return false;
	}
	finally {
	    if (inChannel != null) inChannel.close();
	    if (outChannel != null) outChannel.close();
	}
	return true;
    }
    
    public static void main(String[] args) throws IOException{
	SeparatorChannel separator = new SeparatorChannel();
	String fileAndPath="/home/btang/test/big.iso";
	long blockSize=20*1024*1024;         //    1M : 1024*1024     1K:  1024
	if(separator.SepFile(fileAndPath,blockSize)) {
	    System.out.println("File Split Successful!");
	}
	else  {
	    System.out.println("File Split not Successful!");
	}
    }
    
}