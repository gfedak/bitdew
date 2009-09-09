
package xtremweb.core.util.filesplit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.StringTokenizer;

public class ReadFileList
{
  public String srcDirectory;
  public String[] separatedFiles;           //the name of each file
  public String[][] separatedFilesAndSize;  //the name of each file and the size
  public int FileNum=0;                     //the number of files
  public long totalsize=0;

  public ReadFileList()
  {
  }

  public long getFileSize(String FileName)
  {
    FileName=srcDirectory+FileName;
    return (new File(FileName).length());
  }

  public void getFileList(String directory)
  {
    srcDirectory = directory;
    File file=new File(srcDirectory);
    separatedFiles=new String[file.list().length];   //1-D
    separatedFiles=file.list();
    
    //2-D
    separatedFilesAndSize=new String[separatedFiles.length][2];
    Arrays.sort(separatedFiles);      //sort
    FileNum=separatedFiles.length;    //the number of files
    for(int i=0;i<FileNum;i++)
    {
      separatedFilesAndSize[i][0]=separatedFiles[i];    //filename
      long sss = getFileSize(separatedFiles[i]);
      totalsize=totalsize+sss;
      separatedFilesAndSize[i][1]=String.valueOf(sss);    //size
    }
  }



    // after separated, with ".part****"
  public void getFileListFromSep(String directory, int n)
  { 
    separatedFiles=new String[n];
    separatedFilesAndSize=new String[n][2];

    srcDirectory = directory;
    File file=new File(srcDirectory);
    String[] separatedFiles_=new String[file.list().length];   
    separatedFiles_=file.list();
        
    Arrays.sort(separatedFiles_);      //sort

    FileNum=n;    //the number of files  (all the files)

    System.out.println("sepaatedFiles_ size="+separatedFiles_.length);
    int j=0;
    for(int i=0;i<separatedFiles_.length;i++)
    {
	int len = separatedFiles_[i].length();
	//System.out.println("len="+len+" filename="+separatedFiles_[i]);
	if (len>=9){
	    if (separatedFiles_[i].substring(len-9,len-4).equals(".part")){
		separatedFiles[j]=separatedFiles_[i];
		separatedFilesAndSize[j][0]=separatedFiles_[i];    //filename
		System.out.println("separatedFiles_[i]="+separatedFiles_[i]);
		long sss = getFileSize(separatedFiles_[i]);
		totalsize=totalsize+sss;
		separatedFilesAndSize[j][1]=String.valueOf(sss);    //size
		j=j+1;
	    }
	}
    }
  }

  
  public static void main(String[] args)
  {
   ReadFileList rfl= new ReadFileList ();    
   rfl.getFileList("/home/btang/DS/");
   System.out.println(rfl.FileNum);
   for (int i=0;i<rfl.FileNum;i++)
      System.out.println(rfl.separatedFilesAndSize[i][0]+"+"+rfl.separatedFilesAndSize[i][1]);
  }
} 
