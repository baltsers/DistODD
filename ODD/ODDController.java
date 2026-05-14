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
		String s1=getNextConfigurations("100000");
		System.out.println("s1="+s1);
		
		
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
    	//int resultI=-1;
    	//int oldI=Integer.parseInt(staticConfigurations, 2);
    	int newI=Integer.parseInt(configurations, 2)-1;
    	if (newI<0)
    		newI=0;
    	return addZeroForNum(""+Integer.toBinaryString(newI), 6);
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
    
    public static void setNextConfigurationInFile(Qlearner learner, String mazeFile, String configurationFile) {
    	String configurations=ODDUtil.readLastLine(configurationFile);
    	String newConfigurations=getNextConfigurations(learner, mazeFile, configurations);
    	//String newConfigurations=getNextConfigurations(configurations);
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
    

}
