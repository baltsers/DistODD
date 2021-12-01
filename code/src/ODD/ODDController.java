package ODD;

import java.util.ArrayList;

import QL.Qlearner;

public class ODDController {
	public static void main(String []args) {
		//updateConfigurationFromTimesFile("C:/temp/dynamicConfiguration.txt", "C:/temp/dynamicTimes.txt", (long)100, 4);
//		String ss1=getNextConfigurations("011001");
//		System.out.println("ss1="+ss1);
//    	Qlearner learner  = new Qlearner();
//		learner.gamma=0.9;
//		learner.alpha=0.9;
//		learner.epislon=0.2;
//		setNextConfigurationInFile(learner, "C:/tp/maze3.txt", "C:/tp/Configuration70989hcaidl5802.txt");
//		String s1=getNextConfigurations("100000");
//		System.out.println("s1="+s1);
//		double epsilon = (1.5-1.25)/0.75;
//		System.out.println("epsilon="+epsilon);
		//long[] known= {673646, 777589, 993012};
		String cfg1=getNextControlTheoryConfiguration(67975, "C:/Research/thrift/Maze.txt");
		System.out.println("cfg1="+cfg1);
		
	}
    public static int getMinPosDiffArrayIndex(ArrayList<Long> oldA, Long expected) {  
    	int resultI=-1;
    	long oldValue=-1;
    	long diffValue=expected+1;
    	long minDiff=expected+1;
		for (int i=0; i<oldA.size(); i++) { 
			oldValue=oldA.get(i);		
			diffValue=expected-oldValue;
			//System.out.println("oldValue="+oldValue+" diffValue="+diffValue+" minDiff="+minDiff);
			if (diffValue>=0 && diffValue<minDiff)  {
				minDiff=diffValue;
				resultI=i;
			}			
		}			    	
    	return resultI;
    }  
    
//    public static int getFirst0ArrayIndex(ArrayList<Long> oldA) {  
//    	int resultI=-1;
//		for (int i=0; i<oldA.size(); i++) { 
//			if (oldA.get(i)==0)  {
//				resultI=i;
//				return i;
//			}
//			
//		}			    	
//    	return resultI;
//    }  
    
    public static int getLast0ArrayIndex(ArrayList<Long> oldA) {  
    	int resultI=-1;
		for (int i=oldA.size()-1; i>=0; i--) { 
			if (oldA.get(i)==0)  {
				resultI=i;
				return i;
			}
			
		}			    	
    	return resultI;
    }  
    public static int getNextDynamicIndex(ArrayList<Long> oldA, Long expected) {  
    	//int resultI=-1;
    	int firstI=getLast0ArrayIndex(oldA);
    	if (firstI>=0)  {
    		//resultI=firstI;
    		return firstI;
    	}
    	return getMinPosDiffArrayIndex(oldA, expected);
    }	
    
    public static void updateConfigurationFromTimesFile(String configurationFile, String timesFile, Long expected, int configurationLength) {
		ArrayList<Long> list1=ODDUtil.readTimesFromFile(timesFile, " ");
		//System.out.println("list1=" + list1+" expected=" + expected);
		int nextIndex=getNextDynamicIndex(list1, expected);		    	
    	String binaryStr=Integer.toBinaryString(nextIndex);
    	String writeStr=addZeroForNum(binaryStr, configurationLength);
    	String readStr=ODDUtil.readToString(configurationFile);
    	//System.out.println("writeStr=" + writeStr+" readStr=" + readStr);
    	if (!writeStr.equals(readStr))
    		ODDUtil.writeStringToFile(writeStr, configurationFile);
    	
    }
    
    public static String updatedConfigurationFromTimesFile(String configurationFile, String timesFile, Long expected, int configurationLength) {
		ArrayList<Long> list1=ODDUtil.readTimesFromFile(timesFile, " ");
		//System.out.println("list1=" + list1+" expected=" + expected);
		int nextIndex=getNextDynamicIndex(list1, expected);		    	
    	String binaryStr=Integer.toBinaryString(nextIndex);
    	String writeStr=addZeroForNum(binaryStr, configurationLength);
    	//String readStr=ODDUtil.readToString(configurationFile);
    	//System.out.println("writeStr=" + writeStr+" readStr=" + readStr);
    	return writeStr;

    	
    }
    
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 
                str = sb.toString();
                strLen = str.length();
            }
        }
        if (str.length()>strLength)  {
        	return str.substring(0, strLength);
        }	
        else
        	return str;
    }
    
    public static String getNextStaticIndexStr(String staticConfigurations) {  
    	//int resultI=-1;
    	//int oldI=Integer.parseInt(staticConfigurations, 2);
    	int newI=Integer.parseInt(staticConfigurations, 2)-1;
    	if (newI<0)
    		newI=0;
    	return addZeroForNum(""+newI, 2);
    }	
    
    public static String getNextConfigurations(String configurations) {  
//    	//int resultI=-1;
//    	//int oldI=Integer.parseInt(staticConfigurations, 2);
//    	int newI=Integer.parseInt(configurations, 2)-1;
//    	if (newI<0)
//    		newI=0;
//    	return addZeroForNum(""+Integer.toBinaryString(newI), 6);
    	String resultS=configurations;
    	String [] allCfgs= {"111111","111110","111101","111100","111010","111000","101111","101110","101101","101100","101010","101000","011111","011110","011101","011100","011010","011000","001111","001110","001101","001100","001010","001000","000101","000100"};
    	String aItem="";
    	for (int i=0; i<26; i++)
    	{
    		aItem=allCfgs[i];
    		if (aItem.equals(configurations))  {
    			if (i<25)
    				return allCfgs[i+1];
    		}
    	}
    	return resultS;
    }	
    
//    public static String getNextConfigurations(String configurations) {  
//    	//int resultI=-1;
//    	//int oldI=Integer.parseInt(staticConfigurations, 2);
//    	if (configurations.length()<6)
//    		return configurations;
//		String staticConfigurations=configurations.substring(0, 2);
//		String dynamicConfigurations=configurations.substring(2, 6);
//		int oldStateY=Integer.parseInt(dynamicConfigurations, 2);
//		int oldStateX=Integer.parseInt(staticConfigurations, 2);
//		//System.out.println("oldStateY="+oldStateY+" oldStateX="+oldStateX);	
//    	Qlearner learner  = new Qlearner();
//		learner.gamma=0.9;
//		learner.alpha=0.9;
//		learner.epislon=0.2;
////		learner.iteration=1000;
////		learner.time_interval=1;
////
////		learner.reset=false;	
//		int[] oldState={ oldStateY, oldStateX };
//		int[] newState=learner.getNextState(oldState);
//		//System.out.println("newState[1]="+newState[1]+" newState[0]="+newState[0]);
//		String newConfigurations=addZeroForNum(Integer.toBinaryString(newState[1]),2)+addZeroForNum(Integer.toBinaryString(newState[0]),4);
//    	return newConfigurations;    	
//    }    
    
    public static void setNextConfigurationInFile(String configurationFile) {
    	String configurations=ODDUtil.readLastLine(configurationFile);
    	String newConfigurations=getNextConfigurations(configurations);
    	if (!newConfigurations.equals(configurations))
    		ODDUtil.writeStringToFile(newConfigurations,configurationFile);   	
    }
    
    public static String getNextConfigurations(Qlearner learner, String mazeFile, String configurations) {  
    	//int resultI=-1;
    	//int oldI=Integer.parseInt(staticConfigurations, 2);
    	if (configurations.length()<6)
    		return configurations;
		String staticConfigurations=configurations.substring(0, 2);
		String dynamicConfigurations=configurations.substring(2, 6);
		int oldStateY=Integer.parseInt(dynamicConfigurations, 2);
		int oldStateX=Integer.parseInt(staticConfigurations, 2);
		//System.out.println("oldStateY="+oldStateY+" oldStateX="+oldStateX);	
		learner.updateMAP(mazeFile);
		int[] oldState={ oldStateY, oldStateX };
		int[] newState=learner.getNextState(oldState);
		//System.out.println("newState[1]="+newState[1]+" newState[0]="+newState[0]);
		String newConfigurations=addZeroForNum(Integer.toBinaryString(newState[1]),2)+addZeroForNum(Integer.toBinaryString(newState[0]),4);
    	return newConfigurations;    	
    }	
    public static String getNextConfigurations(Qlearner learner, String mazeFile, String configurations, String budgetFile) {  
    	//int resultI=-1;
    	//int oldI=Integer.parseInt(staticConfigurations, 2);
    	if (configurations.length()<6)
    		return configurations;
		String staticConfigurations=configurations.substring(0, 2);
		String dynamicConfigurations=configurations.substring(2, 6);
		int oldStateY=Integer.parseInt(dynamicConfigurations, 2);
		int oldStateX=Integer.parseInt(staticConfigurations, 2);
		//System.out.println("oldStateY="+oldStateY+" oldStateX="+oldStateX);	
		learner.updateMAP(mazeFile,budgetFile);
		int[] oldState={ oldStateY, oldStateX };
		int[] newState=learner.getNextState(oldState);
		//System.out.println("newState[1]="+newState[1]+" newState[0]="+newState[0]);
		String newConfigurations=addZeroForNum(Integer.toBinaryString(newState[1]),2)+addZeroForNum(Integer.toBinaryString(newState[0]),4);
    	return newConfigurations;    	
    }
   
//    public static String getNextConfigurations(Qlearner learner, String configurations) {  
//    	//int resultI=-1;
//    	//int oldI=Integer.parseInt(staticConfigurations, 2);
//    	if (configurations.length()<6)
//    		return configurations;
//		String staticConfigurations=configurations.substring(0, 2);
//		String dynamicConfigurations=configurations.substring(2, 6);
//		int oldStateY=Integer.parseInt(dynamicConfigurations, 2);
//		int oldStateX=Integer.parseInt(staticConfigurations, 2);
//		//System.out.println("oldStateY="+oldStateY+" oldStateX="+oldStateX);	
//		int[] oldState={ oldStateY, oldStateX };
//		int[] newState=learner.getNextState(oldState);
//		//System.out.println("newState[1]="+newState[1]+" newState[0]="+newState[0]);
//		String newConfigurations=addZeroForNum(Integer.toBinaryString(newState[1]),2)+addZeroForNum(Integer.toBinaryString(newState[0]),4);
//    	return newConfigurations;    	
//    }	
    
//    public static void setNextConfigurationInFile(Qlearner learner, String mazeFile, String configurationFile) {
//    	String configurations=ODDUtil.readLastLine(configurationFile);
//    	String newConfigurations=getNextConfigurations(learner, mazeFile, configurations);
//    	//String newConfigurations=getNextConfigurations(configurations);
//    	System.out.println("oldconfigurations: " + configurations + " newConfigurations: " + newConfigurations);
//    	if (!newConfigurations.equals(configurations))
//    		ODDUtil.writeStringToFile(newConfigurations,configurationFile);   	
//    }
    public static void setNextConfigurationInFile(Qlearner learner, String mazeFile, String configurationFile, String budgetFIle, long cost) {
    	String configurations=ODDUtil.readLastLine(configurationFile);
    	String newConfigurations=getNextConfigurations(learner, mazeFile, configurations,budgetFIle);
    	//String newConfigurations=getNextConfigurations(configurations);
    	//String newConfigurations=getNextControlTheoryConfiguration(cost, mazeFile); 
    	System.out.println("oldconfigurations: " + configurations + " newConfigurations: " + newConfigurations);
    	if (!newConfigurations.equals(configurations))
    		ODDUtil.writeStringToFile(newConfigurations,configurationFile);   	
    }
    
    public static void setNextConfigurationInFileControl(String mazeFile, String configurationFile, String budgetFIle, long cost) {
    	String configurations=ODDUtil.readLastLine(configurationFile);
    	//String newConfigurations=getNextConfigurations(learner, mazeFile, configurations,budgetFIle);
    	//String newConfigurations=getNextConfigurations(configurations);
    	String newConfigurations=getNextControlTheoryConfiguration(cost, mazeFile); 
    	System.out.println("oldconfigurations: " + configurations + " newConfigurations: " + newConfigurations);
    	if (!newConfigurations.equals(configurations))
    		ODDUtil.writeStringToFile(newConfigurations,configurationFile);   	
    }
//    public static void setNextConfigurationInFile(Qlearner learner, String configurationFile) {
//    	String configurations=ODDUtil.readLastLine(configurationFile);
//    	String newConfigurations=getNextConfigurations(learner, configurations);
//    	//String newConfigurations=getNextConfigurations(configurations);
//    	System.out.println("oldconfigurations: " + configurations + " newConfigurations: " + newConfigurations);
//    	if (!newConfigurations.equals(configurations))
//    		ODDUtil.writeStringToFile(newConfigurations,configurationFile);   	
//    }
    
    public static String getConfigurationsFromArray(boolean[] settings) {  
    	String resultS = "";
//    	for (int i=0; i<6; i++)
//    	{
//    		System.out.println("settings["+i+"]:"+settings[i]);
//    	}
    	try {
	    	for (int i=0; i<3; i++)
	    	{
	    		if (settings[i])  {
	    			resultS = resultS+"1";
	    		}
	    		else
	    			resultS = resultS+"0";
	    	}
	        if (!settings[2])
	        {
	        	resultS="000";
	        }
	    	for (int i=3; i<6; i++)
	    	{   	
	    		if (i==4) {
	    			if (!settings[2]) {
	    				resultS = resultS+"0";
	    			}
	    			else {
	    	    		if (settings[i])  {
	    	    			resultS = resultS+"1";
	    	    		}
	    	    		else
	    	    			resultS = resultS+"0";
	    			}
	    		}
	    		else  {
    	    		if (settings[i])  {
    	    			resultS = resultS+"1";
    	    		}
    	    		else
    	    			resultS = resultS+"0";
    			}
	    			
	    	}
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			return "";
		}     
    	return resultS;
    }	
    
    public static String getNextConfigurationAdaption(long cost, long[] known) {  
    	//0	1	1	1	0	0                           011100
    	//0	1	1	1	1	1							011111
    	//1	1	1	1	1	1                           111111
    	if (known.length<3)
    		return "";
    	String [] allCfgs= {"000100","000101","001000","011000","101000",  "111000",  "001010","011010","101010","111010",
	            "001100","011100", "101100","111100", "001110","011110", "101110","111110", "001101", "001111", 
	            "011101", "011111","101101","101111","111101","111111"};
    	String config1="";
    	double epsilon=(double) 0;
    	for (int i=0; i<20; i++)  {
    		if (cost<known[0])  {
    			config1=allCfgs[1];
    		}
    		else  {
        		if (cost<known[1])  {
        			epsilon = (cost - known[0])/ (known[1] - known[0]);
        			if (epsilon>=0 && epsilon<0.33)  {
        				config1=allCfgs[7];
        			}
        			else if (epsilon>=0.33 && epsilon<0.66)  {
        				config1=allCfgs[8];
        			}
        			else 
        				config1=allCfgs[9];
        		}
        		else   {
        			if (cost<known[2])  {
            			epsilon = (cost - known[1])/ (known[2] - known[1]);
            			if (epsilon>=0 && epsilon<0.25)  {
            				config1=allCfgs[21];
            			}
            			else if (epsilon>=0.25 && epsilon<0.5)  {
            				config1=allCfgs[12];
            			}else if (epsilon>=0.5 && epsilon<0.75)  {
            				config1=allCfgs[13];
            			}
            			else 
            				config1=allCfgs[23];
        			}
        			else
        				config1=allCfgs[25];
        		}	
    		}
    			
    	}
 
    	return config1;
    }	
    
    public static String getNextControlTheoryConfiguration(long cost, String mazeFile) {
    	String[][] maze=ODDQLUtil.getMazeFromFile(mazeFile, 4, 16);
    	long cost1=ODDQLUtil.getTimeCostFromMaze(maze, 4, 16, "011100");
    	long cost2=ODDQLUtil.getTimeCostFromMaze(maze, 4, 16, "011111");
    	long cost3=ODDQLUtil.getTimeCostFromMaze(maze, 4, 16, "111111");
    	long[] oldCosts= {cost1, cost2, cost3};
    	//System.out.println(" cost1="+cost1+" cost2="+cost2+" cost3="+cost3);
    	return getNextConfigurationAdaption(cost, oldCosts);
    }
}    