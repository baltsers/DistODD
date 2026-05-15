package ODD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.regex.Pattern;

public class ODDQLUtil {
    private static int mazeWidth = 4;
    private static int mazeHeight = 16;
    public static String[][] maze;  //=new String[mazeHeight][mazeWidth];
    public static double[][] MAP=new double[mazeHeight][mazeWidth];
	public static void main(String []args) {
		maze=getMazeFromFile("C:/tp/maze3.txt", mazeWidth, mazeHeight);
		for (int i=0; i<mazeHeight; i++)
		{
			for (int j=0; j<mazeWidth;j++)
            {
				System.out.print(maze[i][j]+" ");
            }
			System.out.print("\n");
		}
		
		MAP=getMAPFromFile("C:/tp/maze3.txt", mazeWidth, mazeHeight);
		for (int i=0; i<mazeHeight; i++)
		{
			for (int j=0; j<mazeWidth;j++)
            {
				System.out.print(MAP[i][j]+" ");
            }
			System.out.print("\n");
		}		
	}
    
    public static String[][] getMazeFromFile(String fileName, int mazeWidth, int mazeHeight) {
    	String[][] maze=new String[mazeHeight][mazeWidth];
   		FileReader reader = null;      
        BufferedReader br = null;    
        String str = "";  
        String strtrim="";
        int i = 0;
        try {
			reader = new FileReader(fileName);
			br = new BufferedReader(reader);
			str = br.readLine();
			while(str != null)
	        {	        	
	        	
	        	strtrim=str.trim().replace("\n", "").replace("\t", "");
	        	String[] strs = strtrim.split(" "); 
	        	//System.out.print(" strtrim="+strtrim+" strs.length="+strs.length+"\n");
	            for (int j=0; j<mazeWidth;j++)
	            {
	            	if (j<strs.length)
	            	{
	            		maze[i][j] = strs[j];
	            	}
	            	else
	            		maze[i][j] = " ";
	            	//System.out.print("maze["+i+"]["+j+"] = "+maze[i][j]+" ");
	            	
	            }
	            i++;
	            if (i>=mazeHeight)
	            	break;
	        	// read lines
	            str = br.readLine();
	        }     
	        br.close();
	        reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}           
    	return maze;
    }
    
    public static double[][] getMAPFromFile(String fileName, int mazeWidth, int mazeHeight) {
    	double[][] MAP=new double[mazeHeight][mazeWidth];
   		FileReader reader = null;      
        BufferedReader br = null;    
        String str = "";  
        String strtrim="";
        String tmpStr="";
        int i = 0;
        try {
			reader = new FileReader(fileName);
			br = new BufferedReader(reader);
			str = br.readLine();
			while(str != null)
	        {	        	
	        	
	        	strtrim=str.trim().replace("\n", "").replace("\t", "");
	        	String[] strs = strtrim.split(" "); 
	        	//System.out.print(" strtrim="+strtrim+" strs.length="+strs.length+"\n");
	            for (int j=0; j<mazeWidth;j++)
	            {
	            	if (j>=strs.length)  {
	            		tmpStr="0";
	            	}
	            	else
	            		tmpStr=strs[j];
	            	//System.out.print(" tmpStr="+tmpStr);
	            	if (j<strs.length)
	            	{
	            		if (isDouble(tmpStr)) {
	            			MAP[i][j] = Double.parseDouble(tmpStr);
	            		}
	            		else 
	            			MAP[i][j] = 0;
	            	}
	            	else
	            		MAP[i][j] = 0;
	            	//System.out.print("MAP["+i+"]["+j+"] = "+MAP[i][j]+" ");
	            	
	            }
	            i++;
	            if (i>=mazeHeight)
	            	break;
	        	// read lines
	            str = br.readLine();
	        }     
	        br.close();
	        reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}           
    	return MAP;
    }
    
    public static boolean isDouble(String str){
    	try {
    		double db1= Double.parseDouble(str);
	    	
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}  
    return true;
    }
}
