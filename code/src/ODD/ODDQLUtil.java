package ODD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.regex.Pattern;

public class ODDQLUtil {
//    private static int mazeWidth = 4;
//    private static int mazeHeight = 16;
//    public static String[][] maze;  //=new String[mazeHeight][mazeWidth];
//    public static double[][] MAP=new double[mazeHeight][mazeWidth];
	public static void main(String []args) {
		int mazeWidth = 4;
		int mazeHeight = 16;
		String[][] maze=getMazeFromFile("C:/Research/thrift/Maze.txt", mazeWidth, mazeHeight);
		for (int i=0; i<mazeHeight; i++)
		{
			for (int j=0; j<mazeWidth;j++)
            {
				System.out.print(maze[i][j]+" ");
            }
			System.out.print("\n");
		}
		long l1 = getTimeCostFromMaze(maze, mazeWidth, mazeHeight, "011111");
		System.out.print(" l1="+l1);
		//updateMazeFromReward(String[][] maze, int mW, int mH, String cfg, Long timeCost, Long budget, Long ratio) 
//		String[][] maze2=updateMazeFromReward(maze, mazeWidth, mazeHeight, "000100", (double)5000, (double)6000);  //updateMaze(maze, mazeWidth, mazeHeight, 16, 4, "999;1" );
//		for (int i=0; i<mazeHeight; i++)
//		{
//			for (int j=0; j<mazeWidth;j++)
//            {
//				System.out.print(maze2[i][j]+" ");
//            }
//			System.out.print("\n");
//		}
//		writeMazeToFile(maze2, mazeWidth, mazeHeight, "C:/Research/thrift/Maze2.txt");
		//MAP=getMAPFromFile("C:/tp/maze.txt", mazeWidth, mazeHeight);
//		MAP=getMAPFromRewardFile("C:/tp/maze.txt", "C:/tp/budget.txt", mazeWidth, mazeHeight);
//		for (int i=0; i<mazeHeight; i++)
//		{
//			for (int j=0; j<mazeWidth;j++)
//            {
//				System.out.print(MAP[i][j]+" ");
//            }
//			System.out.print("\n");
//		}		
//		String str1=ODDUtil.readToString("C:/Research/thrift/maze.txt");
//		System.out.print(" str1="+str1);
//		String str2=getPrecsions("C:/Research/thrift/maze.txt");
//		System.out.println(" str2="+str2);
//		String str3=ODDUtil.readToString("C:/Research/thrift/Times71446hcaidl5802.txt");
//		System.out.print(" str3.split.length="+str3.split(" ").length+" str3="+str3);
	}
    
    public static String[][] getMazeFromFile(String fileName, int mW, int mH) {
    	String[][] maze=new String[mH][mW];
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
	            for (int j=0; j<mW;j++)
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
	            if (i>=mH)
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
   
    public static double[][] getMAPFromRewardFile(String rewardFile, String budgetFile, int mazeWidth, int mazeHeight) {
    	double[][] MAP=new double[mazeHeight][mazeWidth];
    	Long budget=(long) 0;
    	if (budgetFile.length()>1)  {
	    	String budgetStr=ODDUtil.readToString(budgetFile).trim().replaceAll("[^\\d]", "");
			if (budgetStr.length()>0) {
				try
				{			
						budget=Long.parseLong(budgetStr);	
				} catch (Exception e) { 
		        } 
			}
    	}
   		FileReader reader = null;      
        BufferedReader br = null;    
        String str = "";  
        String strtrim="";
        String tmpStr="";
        int i = 0;
        try {
			reader = new FileReader(rewardFile);
			br = new BufferedReader(reader);
			str = br.readLine();
			while(str != null)
	        {	        	
	        	
	        	strtrim=str.trim().replace("\n", "").replace("\t", "");
	        	String[] strs = strtrim.split(" "); 
	        	//System.out.print(" strtrim="+strtrim+" strs.length="+strs.length+"\n");
	        	double tmpD1=0;	        	
	        	double tmpD2=0;	      	
	            for (int j=0; j<mazeWidth;j++)
	            {
	            	if (j>=strs.length)  {
	            		tmpStr="0";
	            	}
	            	else
	            		tmpStr=strs[j].trim();
	            	//System.out.print(" tmpStr="+tmpStr);
	            	if (j<strs.length)
	            	{
	            		if (tmpStr.indexOf(";")>1) {
	            			//tmpD1=0;
	            			tmpD2=0;	 
	            			String[] tmpStrs= tmpStr.split(";"); 
	            			try {
	            				tmpD1=Double.parseDouble(tmpStrs[0].trim());
	            			}catch (Exception e) { 
	            				tmpD1=0;
	            	        } 
	            			if (tmpStrs.length>1)  {
	            				try {
		            				tmpD2=Double.parseDouble(tmpStrs[1].trim());
		            			}catch (Exception e) { 
		            				tmpD2=0;
		            	        } 
	            			}
	            			//System.out.print(" tmpD1="+tmpD1+" tmpD2="+tmpD2+"\n");
	            			MAP[i][j] =(double)1/(budget-tmpD1)*1000 + (double)Math.abs(1/(budget-tmpD1)*1000*tmpD2);
	            		}
	            		else if (tmpStr.equals("-99")) {
	            			MAP[i][j] = (double)-99;
	            		}
	            		else if (isDouble(tmpStr)) {	
	            			tmpD1=0;
	            			//MAP[i][j] = Double.parseDouble(tmpStr);
	            			try {
	            				tmpD1=Double.parseDouble(tmpStr);
	            			}catch (Exception e) { 
	            				tmpD1=0;
	            	        } 
	            			//System.out.print(" budget==tmpD1="+(budget==tmpD1));
	            			if (budget==tmpD1)  {
	            				MAP[i][j] = 0;
	            			}	
	            			else
	            				MAP[i][j] =(double)1/(budget-tmpD1)*1000;
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
    
    public static String[][] updateMaze(String[][] maze, int mW, int mH, int rowPos, int colPos, String updatedValue) {
    	String[][] resultM=new String[mH][mW];
    	System.out.println(" mW="+mW+" mH="+mH+" updatedValue="+updatedValue);
        for (int i=0; i<mH; i++)
        	for (int j=0; j<mW; j++)  {
        		if (i==rowPos && j==colPos)  {
        			//System.out.println(" Updated i="+i+" j="+j);
        			resultM[i][j]=updatedValue;
        		}
        		else  
        		{
        			//System.out.println(" i="+i+" j="+j);
        			resultM[i][j]=maze[i][j];
        		}
        	}
      
    	return resultM;
    }

    public static String[][] updateMazeFromReward(String[][] maze, int mW, int mH, String cfg, double timeCost, double budget) {
    	String[][] resultM=maze;
    	if (cfg.length()!=6)
    		return resultM;
    	String staticCfg=cfg.substring(0,2);
    	int colPos=Integer.parseInt(staticCfg, 2);
    	String dynamicCfg=cfg.substring(2,6);
    	int rowPos=Integer.parseInt(dynamicCfg, 2);
    	//System.out.println(" staticCfg="+staticCfg+" colPos="+colPos+" dynamicCfg="+dynamicCfg+" rowPos="+rowPos);
    	double ratio=(double)0;
    	try {
    		String midStr=maze[rowPos][colPos];
    		String ratioStr=getRatio(midStr); 	
    		ratio=Double.parseDouble(ratioStr);
    	} catch (Exception e) {}
    	double updatedD=0;
    	if (timeCost!=budget)
    		updatedD=(double)1/(budget-timeCost)*1000+Math.abs((double)1/(budget-timeCost)*1000*ratio);
    	System.out.println(" budget="+budget+" timeCost="+timeCost+" (double)1/(budget-timeCost)*1000="+(double)1/(budget-timeCost)*1000+" ratio="+ratio+" updatedD="+updatedD);
    	resultM=updateMaze(maze, mW, mH, rowPos, colPos, ""+updatedD+";"+ratio );
    	return resultM;
    }
    
    public static void writeMazeToFile(String[][] maze, int mW, int mH, String dest)    {
    	String allS="";
        for (int i=0; i<mH; i++)  {
        	for (int j=0; j<mW; j++)  {
        		allS+=maze[i][j]+" ";
        	}
        	allS+="\n";
        }	
        ODDUtil.writeStringToFile(allS, dest);
    }

    
    public static void saveMazeFileRewardPenalty (String cfg, String mazeFile, String timeStr, double expected) {
    	String[][] maze=getMazeFromFile(mazeFile, 4, 16);
    	String oneTime=getTime(timeStr, cfg);
    	double oneTimeD=(double)0.00;
    	try {
    		oneTimeD=Double.parseDouble(oneTime);
    	} catch (Exception e) {}
    	//   public static String[][] updateMazeFromReward(String[][] maze, int mW, int mH, String cfg, double timeCost, double budget) {
    	String[][] maze2=updateMazeFromReward(maze, 4, 16, cfg, oneTimeD, expected);
    	writeMazeToFile(maze2, 4, 16, mazeFile);	
    }

    public static void saveRewardPenaltyfromFiles (String cfg, String mazeFile, String timeFile, long staticExpected, long dynamicExpected, boolean isStaticLoad, boolean isStaticCreate) {
    	String times=ODDUtil.readLastLine(timeFile);
    	long expected=dynamicExpected;
    	if (isStaticCreate || isStaticLoad) {
    		expected+=staticExpected;
    	}
    	saveMazeFileRewardPenalty (cfg, mazeFile, times, expected);
    }	

    public static String getRatio(String str) {
    	String resultS="1";
    		String[] strs=str.split(";");
    		if (strs.length>1)  {
    			resultS=strs[1].trim();
    		}
    	return resultS;
    }
    
    public static String getFirst(String str) {
    		String[] strs=str.split(";");
    		return strs[0].trim();
    }
    
    public static String getTime(String str, String Configurations) {
    	String resultS="";
		String[] strs=str.split(" ");
		int position=-1;
		try {
			position=Integer.parseInt(Configurations,2);
		} catch (Exception e)	{ }
		if (position>=0 && position<strs.length)
			return strs[position].trim();
    	return resultS;
    }
    
//    public static String getRatio(String mazeFile, String cfg) {
////    	String midS="";
////		String[] strs=str.split(" ");
////		int position=-1;
////		try {
////			position=Integer.parseInt(Configurations,2);
////		} catch (Exception e)	{ }
////		if (position>=0 && position<strs.length)
////			midS=strs[position].trim();
////		if (midS.length()>0) {
////			return getRatio(midS);
////		}
////		else
////			return "";
//    	String[][] maze=getMazeFromFile(mazeFile, 4, 16);
//    	if (cfg.length()!=6)
//    		return "";
//    	String staticCfg=cfg.substring(0,2);
//    	int colPos=Integer.parseInt(staticCfg, 2);
//    	String dynamicCfg=cfg.substring(2,6);
//    	int rowPos=Integer.parseInt(dynamicCfg, 2);
//    	String midStr=maze
//    }
//    public static String getPrecisions(String mazeStr) {
//    	String resultS="";
//        String longStr=mazeStr;
//        String[] strs=longStr.split(" ");
//        String midStr="";
//        //System.out.println("longStr="+longStr+" strs.length="+strs.length);
//        for (int i=0; i<strs.length; i++) {
//        	midStr=strs[i].trim();
//        	System.out.println("midStr="+midStr);
//        	if (midStr.length()>0)  {
//        		System.out.println("getRato(midStr)="+getRatio(midStr));
//        		resultS+=getRatio(midStr)+" ";
//        	}
//        }
//        System.out.println(" resultS.length="+resultS.split(" ").length);
//    	return resultS;
//    }
    public static long getTimeCostFromMaze(String[][] maze, int mW, int mH, String cfg) {
    	String staticCfg=cfg.substring(0,2);
    	int colPos=Integer.parseInt(staticCfg, 2);
    	String dynamicCfg=cfg.substring(2,6);
    	int rowPos=Integer.parseInt(dynamicCfg, 2);
    	//System.out.println(" staticCfg="+staticCfg+" colPos="+colPos+" dynamicCfg="+dynamicCfg+" rowPos="+rowPos);
    	String midStr=maze[rowPos][colPos];
    	String costStr=getFirst(midStr);
    	//System.out.println(" costStr="+costStr);
    	double costD=(double)0;
    	try {
    		costD=Double.parseDouble(costStr);
    	} catch (Exception e) { e.printStackTrace();}
    	//System.out.println(" costD="+costD);
    	long cost=(long)Math.round(costD);
    	//System.out.println(" cost="+cost);
    	return cost;
    }
}
