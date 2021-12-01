package ODD;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
//import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.Set;

public class ODDToolUtil {
	/** a map from method signature to index for the underlying static VTG */
//	protected static Map< String, Integer > method2idx;
//	/** a map from index to method signature for the underlying static VTG */
//	protected static Map< Integer, String > idx2method;
	public static void main(String []args) {
		//saveBudget("main() ; 2100");
//		String s1=getFirstStr("main() ; 2100", " ; ");
//		System.out.println("s1=" + s1);
//		HashSet<Integer> set1 = getQuerySetNumbersAfterLastTime(Long.parseLong("1577290208078"), "C:/TP/Message2Query2009.txt"); //getQuerySetNumberFromString(21, "[[21]]  size=11 ; 16 ; 1 ; 2 ; 18 ; 19 ; 20 ; 21 ; 22 ; 23 ; 24 ; 26"); // getQueryNumbersLastTimeAfter(Long.parseLong("1577290208078"), "C:/TP/Message2Query2003.txt");
//		
//		//HashSet<String> set1 = getQueryIntraSetFromFile("<NioServer: void accept(java.nio.channels.SelectionKey)>", "C:/TP/Message2Query2009.txt"); // getQuerySetNumbersAfterLastTimeInDir(Long.parseLong("1577290208078"), "C:/TP/");
//		System.out.println("set1=" + set1+" set1=" + set1.size());
//		initializeFunctionList();
//		HashSet<String> set2 = getQuerySetAfterLastTime(Long.parseLong("1577290208078"), "C:/TP/Message2Query2009.txt",idx2method);
//		System.out.println("set2=" + set2+" set2=" + set2.size());
//		//getQuerySetFromFile(long firstTime, String resultFile, Map< Integer, String > idx2method, String Query)
//		HashSet<String> set3 = getQuerySetFromFile(Long.parseLong("1577290208078"), "C:/TP/Message2Query2009.txt",idx2method,"<NioServer: void run()>");
//		System.out.println("set3=" + set3+" set3=" + set3.size());
//		Long ft=getFirstEventTime("<NioServer: void run()>","C:/TP/", idx2method);
//		System.out.println("ft=" + ft);
//		HashSet<String> set3 = getQuerySetFromDir( "C:/TP", idx2method, "<NioServer: void run()>");
		//		getQuerySetFromFile(Long.parseLong("1577290208078"), "C:/TP/Message2Query2009.txt",idx2method,"<NioServer: void run()>");
//		System.out.println("set3=" + set3+" set3=" + set3.size());
	}
	public static String getFirstStr(String longStr, String splitStr)
    {
    	String firstStr=longStr;
    	if (longStr.indexOf(" ; ")>0)
        {
    		String[] longStrs=longStr.split(" ; ");
    		firstStr=longStrs[0];
        }
//    	//System.out.println("budgetStr="+budgetStr);
//    	ODDUtil.writeStringToFile(budgetStr,"budget.txt");
    	return firstStr;
    }
    public static void saveBudget(String longStr)
    {
    	String budgetStr=longStr;
    	if (longStr.indexOf(" ; ")>0)
        {
    		String[] longStrs=longStr.split(" ; ");
    		budgetStr=longStrs[1];
        }
    	//System.out.println("budgetStr="+budgetStr);
    	ODDUtil.writeStringToFile(budgetStr,"budget.txt");
    }
	
    public static Long getFirstEventTime(int methodId, String msgStr)  {
    	Long resultL=(long) 0;
    	int idx=Math.abs(methodId)*-1;
    	String idxStr=""+idx+",";
    	String[] strs = msgStr.split("\n"); 
    	String lineStr="";
    	String timeStr="";
    	for (int i=0; i<strs.length;i++)  {
    		lineStr=strs[i];
			//System.out.println("lineStr="+lineStr+" idxStr="+idxStr);
    		if (lineStr.startsWith(idxStr))  {
    			String[] lineStrs = lineStr.split(","); 
    			//System.out.println("lineStrs.length="+lineStrs.length);
    			if (lineStrs.length==2)  {
        			//System.out.println("lineStrs[1]="+lineStrs[1]);
        	    	try {
        	    		resultL=Long.parseLong(lineStrs[1]);
        	    	} catch (Exception e) {
        				continue;
        			}    
        	    	return resultL;
    			}
    				
    		}
    	}
    	return resultL;
    }
    
    public static Long getLastEventTime(int methodId, String msgStr)  {
    	Long resultL=(long) 0;
    	int idx=methodId;
    	String idxStr=""+idx+",";
    	String[] strs = msgStr.split("\n"); 
    	String lineStr="";
    	String timeStr="";
    	for (int i=0; i<strs.length;i++)  {
    		lineStr=strs[i];
			//System.out.println("lineStr="+lineStr+" idxStr="+idxStr);
    		if (lineStr.startsWith(idxStr))  {
    			String[] lineStrs = lineStr.split(","); 
    			//System.out.println("lineStrs.length="+lineStrs.length);
    			if (lineStrs.length==2)  {
        			//System.out.println("lineStrs[1]="+lineStrs[1]);
        	    	try {
        	    		resultL=Long.parseLong(lineStrs[1]);
        	    	} catch (Exception e) {
        				continue;
        			}    
        	    	return resultL;
    			}
    				
    		}
    	}
    	return resultL;
    }
    public static String getFirstEventStr(String msgStr) {
    	String resultS="";
    	String[] strs = msgStr.split("\n"); 
    	String lineStr="";
    	for (int i=0; i<strs.length;i++)  {
    		lineStr=strs[i];      //if (lineStrs.str.matches("^[ABC]+$");
    		if (lineStr.matches("[-][0-9]*,[0-9]*"))
//    		String[] lineStrs = lineStr.split(","); 
//    		if (lineStrs.length==2)  
    		{
    			resultS+=lineStr+"\n";
    		}
    	}
    	return resultS;
    }
    public static String getLastEventStr(String msgStr) {
    	String resultS="";
    	String[] strs = msgStr.split("\n"); 
    	String lineStr="";
    	for (int i=0; i<strs.length;i++)  {
    		lineStr=strs[i];      //if (lineStrs.str.matches("^[ABC]+$");
    		if (lineStr.matches("[0-9]*,[0-9]*"))
//    		String[] lineStrs = lineStr.split(","); 
//    		if (lineStrs.length==2)  
    		{
    			resultS+=lineStr+"\n";
    		}
    	}
    	return resultS;
    }
    public static String getDependenceSetStr(String msgStr) {
    	String[] strs = msgStr.split("\n"); 
    	String lineStr="";
    	for (int i=0; i<strs.length;i++)  {
    		lineStr=strs[i];      //if (lineStrs.str.matches("^[ABC]+$");
    		if (lineStr.startsWith("[") && lineStr.indexOf("]")>28 && lineStr.indexOf("size=")>30)
    			return lineStr;
    	}
    	return lineStr;
    }
    public static String getDependenceSetNumberStr(String msgStr) {
    	String[] strs = msgStr.split("\n"); 
    	String lineStr="";
    	for (int i=0; i<strs.length;i++)  {
    		lineStr=strs[i];      //if (lineStrs.str.matches("^[ABC]+$");
    		if (lineStr.startsWith("[") && lineStr.indexOf("] size=")>1)
    			return lineStr;
    	}
    	return lineStr;
    }
    public static String getQueryDependenceSetStr(String msgStr, String query) {
    	String[] strs = msgStr.split("\n"); 
    	String lineStr="";
    	for (int i=0; i<strs.length;i++)  {
    		lineStr=strs[i];      //if (lineStrs.str.matches("^[ABC]+$");
    		if (lineStr.startsWith("==== DistODD impact set of ["+query+"]") && lineStr.indexOf("size=")>28)
    			return lineStr;
    	}
    	return lineStr;
    }
    public static String getQueryDependencSetNumberStr(String msgStr, int query) {
    	String str=getDependenceSetNumberStr(msgStr);
    	String[] strs = msgStr.split(" ; "); 
    	String midStr="";
    	for (int i=0; i<strs.length;i++)  {
    		midStr=strs[i];      //if (lineStrs.str.matches("^[ABC]+$");
    		if (midStr.startsWith("["+query+"] size="))
    			return midStr;
    	}
    	return midStr;
    }
    
//    public static HashSet<Integer> getQueryDependencSet(String msgStr, int query) {
//		   HashSet<Integer> hs1=new HashSet<Integer>();	 
//		   String setStr=getQueryDependencSetNumberStr(msgStr, query);
//		  
//		   return hs1;				    	
//    }
    
    public static HashSet<Integer> getQueryNumbersAfterLastTime(long firstTime, String resultFile) {
    	HashSet<Integer> oneSet = new HashSet<>();
        FileReader reader = null;  
        BufferedReader br = null;  		        
        try { 

            reader = new FileReader(resultFile);
            String str = null;     
            String midStr="";
            br = new BufferedReader(reader);
            int  methodId=0;
            long resultL=0;
            while ((str = br.readLine()) != null) {   
        		if (str.matches("[0-9]*,[0-9]*"))  {
        			//System.out.println("str="+str);
        			String[] lineStrs = str.split(","); 
        			//System.out.println("lineStrs.length="+lineStrs.length);
            		if (lineStrs.length==2)  
            		{
            			methodId=0;
            			resultL=0;
            	    	try {
            	    		methodId=Integer.parseInt(lineStrs[0]);
            	    		resultL=Long.parseLong(lineStrs[1]);
            	    	} catch (Exception e) {
            			}   
            	    	//System.out.println("methodId="+methodId+" resultL="+resultL+" firstTime="+firstTime+ " resultL>firstTime");
            	    	if (resultL>firstTime) {
            	    		oneSet.add(methodId);
            	    		//System.out.println("oneSet="+oneSet);
            	    	}	
            	    		
            		}
        		}
            }  
   
        } catch (Exception e) {  
            e.printStackTrace();  
        }
    	return oneSet;			    	
 }
    
    public static String getAllSetsLine(String resultFile) {
    	String resultS = "";
        FileReader reader = null;  
        BufferedReader br = null;  		        
        try { 

            reader = new FileReader(resultFile);
            String str = null;     
            String midStr="";
            br = new BufferedReader(reader);
            int  methodId=0;
            long resultL=0;
            while ((str = br.readLine()) != null) {   
            	if (str.indexOf("[")>=0 && str.indexOf("]")>=1)
            		return str;
            }    
        } catch (Exception e) {  
            e.printStackTrace();  
        }
    	return resultS;			    	
    }
    public static String getQuerySetLine(String resultFile) {
    	String resultS = "";
        FileReader reader = null;  
        BufferedReader br = null;  		        
        try { 

            reader = new FileReader(resultFile);
            String str = null;     
            String midStr="";
            br = new BufferedReader(reader);
            int  methodId=0;
            long resultL=0;
            while ((str = br.readLine()) != null) {   
            	if (str.startsWith("==== DistODD impact set of"))
            		return str;
            }    
        } catch (Exception e) {  
            e.printStackTrace();  
        }
    	return resultS;			    	
    }
    
    public static HashSet<Integer> getQuerySetNumberFromString(int querySetNumber, String lineStr) {
    	HashSet<Integer> resultSet = new HashSet<>();
    	//System.out.println("getQuerySetNumberFromString lineStr="+lineStr+" querySetNumber="+querySetNumber);
    	if (lineStr.indexOf("["+querySetNumber+"]")<0 && lineStr.indexOf("[["+querySetNumber+"]]")<0)
    		return resultSet;
    	lineStr=lineStr.replace("[[", "xx").replace("]]", "yy").replace("[", "xx").replace("]", "yy");
    	String[] lineStrs = lineStr.split("xx");
    	String midStr="";
    	for (int i=0; i<lineStrs.length; i++) {
    		midStr=lineStrs[i];
    		if (midStr.indexOf(""+querySetNumber+"yy")>=0) {
    			String[] midStrs = midStr.split(" ; ");
    			String midStr2="";
    			int midInt2=-1;
    	    	for (int j=0; j<midStrs.length; j++) {
    	    		midStr2=midStrs[j].trim();
    	    		//System.out.println(" getQuerySetNumberFromString midStr="+midStr+" midStr2="+midStr2);
        	    	try {
        	    		midInt2=Integer.parseInt(midStr2);        	    		
        	    	} catch (Exception e) {
        	    		continue;
        			}   
        	    	if (midInt2>=0)
        	    		resultSet.add(midInt2);
    	    	}
    		}
    	}
    	return resultSet;			    	
    }
    
    public static HashSet<Integer> getQuerySetNumbersAfterLastTime(long firstTime, String resultFile) {
    	HashSet<Integer> resultSet = new HashSet<>();
    	HashSet<Integer> methodIdSet = getQueryNumbersAfterLastTime(firstTime,resultFile);
    	String allSetsLines=getAllSetsLine(resultFile);
    	//System.out.println("getQuerySetNumbersAfterLastTime allSetsLines="+allSetsLines);
    	for (int methodId   : methodIdSet) {
    	      //System.out.println("getQuerySetNumbersAfterLastTime methodId="+methodId);
    	      HashSet<Integer> methodDependenceSet = getQuerySetNumberFromString(methodId, allSetsLines);
    	      //System.out.println("getQuerySetNumbersAfterLastTime methodDependenceSet="+methodDependenceSet);
    	      resultSet.addAll(methodDependenceSet);
    	      resultSet.add(methodId);
    	}
    	return resultSet;			    	
     }
    
    public static HashSet<Integer> getQuerySetNumbersAfterLastTimeInDir(long firstTime, String filePath) {
    	HashSet<Integer> resultSet = new HashSet<>();
    	File dir = new File(filePath); 
        if (dir.isDirectory()) {     	            
            String[] names = dir.list();
            if (!filePath.endsWith(File.separator))
            	filePath+=File.separator;
            for (int i = 0; i < names.length; i++) {
            	if (names[i].startsWith("Message2Query"))  {
            		String oneQuerySetLine=getQuerySetLine(filePath+names[i]);
            		//System.out.println("oneQuerySetLine="+oneQuerySetLine);
            		if (oneQuerySetLine.indexOf("size=0")>1)  {
	            		HashSet<Integer> midSet=getQuerySetNumbersAfterLastTime(firstTime, filePath+names[i]);
	            		//System.out.println("midSet="+midSet);
	            		resultSet.addAll(midSet);
            		}
            	}
            }
        }
    	return resultSet;			    	
     }
    public static HashSet<String> getQueryIntraSetFromFile(String Query, String resultFile) {
    	HashSet<String> resultSet = new HashSet<>();
    	String lineStr=getQuerySetLine(resultFile);    	
	      //System.out.println("methodId="+methodId);
    	if (lineStr==null || lineStr.length()<10 || lineStr.indexOf("size=0")>1 || !lineStr.startsWith("==== DistODD impact set of ["+Query.trim()+"]"))
    		return resultSet;
		String[] midStrs = lineStr.split(" ; ");
		String midStr2="";
    	for (int j=1; j<midStrs.length; j++) {
    		midStr2=midStrs[j].trim();
    		//System.out.println("midStr="+midStr+" midStr2="+midStr2);	    	
	    	resultSet.add(midStr2);
    	}
    	return resultSet;
    }
    
    public static HashSet<String> getInterSetAfterLastTime(long firstTime, String resultFile, Map< Integer, String > idx2method) {
    	HashSet<String> resultSet = new HashSet<String>();
    	HashSet<Integer> numberSet = getQuerySetNumbersAfterLastTime(firstTime, resultFile);
    	String methodName="";
    	for (int methodId: numberSet) {
    		//System.out.println("methodId="+methodId);
    		methodName=idx2method.get(methodId);
    		//System.out.println("methodName="+methodName);
    		if (methodName!=null && methodName.length()>0)
    			resultSet.add(methodName);
    	}
    	return resultSet;			    	
     }
    
    public static HashSet<String> getQuerySetFromFile(long firstTime, String resultFile, Map< Integer, String > idx2method, String Query) {
    	HashSet<String> resultSet = new HashSet<String>();
    	HashSet<String> intraSet = getQueryIntraSetFromFile(Query, resultFile);
    	System.out.println(resultFile+" intraSet.size()="+intraSet.size());	 
    	if (intraSet.size()>0)  {
    		resultSet.addAll(intraSet);
    	}
    	else  {
    		HashSet<String> interSet = getInterSetAfterLastTime(firstTime, resultFile, idx2method);
        	System.out.println(resultFile+" interSet.size="+interSet.size());	 
    		resultSet.addAll(interSet);
    	}
    	return resultSet;			    	
     }
//    public static HashSet<String> getQuerySetAfterLastTimeInDir(long firstTime, String filePath) {    	
//    	HashSet<Integer> resultSet = new HashSet<>();
//    	File dir = new File(filePath); 
//        if (dir.isDirectory()) {     	            
//            String[] names = dir.list();
//            if (!filePath.endsWith(File.separator))
//            	filePath+=File.separator;
//            for (int i = 0; i < names.length; i++) {
//            	if (names[i].startsWith("Message2Query"))  {
//            		String oneQuerySetLine=getQuerySetLine(filePath+names[i]);
//            		System.out.println("oneQuerySetLine="+oneQuerySetLine);
//            		if (oneQuerySetLine.indexOf("size=0")>1)  {
//	            		HashSet<Integer> midSet=getQuerySetNumbersAfterLastTime(firstTime, filePath+names[i]);
//	            		System.out.println("midSet="+midSet);
//	            		resultSet.addAll(midSet);
//            		}
//            	}
//            }
//        }
//    	return resultSet;			    	
//     }
//	public static int initializeFunctionList() {
//	       try {
//	   		FileReader reader = null;      
//	        BufferedReader br = null;    
//	        reader = new FileReader("C:/TP/functionList.out");   
//	        br = new BufferedReader(reader);
//	        String str = "";  
//	        String mname="";
//	        int index = 0;
//	        method2idx=new HashMap< String, Integer >();
//	        idx2method=new HashMap< Integer, String >();
//	        while((str = br.readLine()) != null)
//	        {  
//	        	mname=str.trim();
//				if (!method2idx.containsKey(mname)) {
//					method2idx.put(mname, index);
//					idx2method.put(index, mname);
//					index ++;
//				}
//	        }        
//
//	       } catch (Exception e) {  
//	           e.printStackTrace(); 
//	           return -1;
//	       } 
//		return 0;
//	}
	
	public static Long getFirstEventTime(String methodName,String filePath, Map<String, Integer> method2idx) { 
		//System.out.println("getFirstEventTime  methodName="+methodName); //+" method2idx="+method2idx);	 
//		for (int i = 0; i < method2idx.size(); i++) {
//			System.out.println("getFirstEventTime  methodName="+methodName+" method2idx="+method2idx.g);	
//		}
		Integer smidx = method2idx.get(methodName);
//		for (String x: method2idx.keySet()) {
//			if (x.startsWith(methodName))
//				smidx=method2idx.get(x);
//			System.out.println(x+" method2idx.get(x): "+method2idx.get(x));
//		}
//		
		System.out.println("methodId="+smidx);	 
		if (smidx==null) {
			return (long) 0;
		}
	    Long resultL=Long.MAX_VALUE/3;
	    Long midL=(long) 0;
    	File dir = new File(filePath); 
    	String msgStr="";
        if (dir.isDirectory()) {     	            
            String[] names = dir.list();
            if (!filePath.endsWith(File.separator))
            	filePath+=File.separator;
            for (int i = 0; i < names.length; i++) {
            	if (names[i].startsWith("Message2Query"))  {
            		msgStr=ODDUtil.readToString(filePath+ names[i]);
            		//System.out.println("msgStr="+msgStr);	 
            		midL=getFirstEventTime(smidx, msgStr);
            		//System.out.println("midL="+midL);	 
            		if (midL>0) {
            			if (midL<resultL)
            				resultL=midL;
            		}
            	}
            }
        }
    	return resultL;			
	}
	
    public static HashSet<String> getQuerySetFromDir(String filePath, Map<String, Integer> method2idx,  Map< Integer, String > idx2method, String Query) {
    	HashSet<String> resultSet = new HashSet<String>();
    	Long methodFirstTime=getFirstEventTime(Query, filePath, method2idx);
    	File dir = new File(filePath); 
        if (dir.isDirectory()) {     	            
            String[] names = dir.list();
            if (!filePath.endsWith(File.separator))
            	filePath+=File.separator;
            for (int i = 0; i < names.length; i++) {
            	if (names[i].startsWith("Message2Query"))  {            		
            		HashSet<String> midSet=getQuerySetFromFile(methodFirstTime,filePath+ names[i], idx2method, Query);
            		//System.out.println("midL="+midSet);	
            		resultSet.addAll(midSet);
            	}
            }
        }
    	return resultSet;		    
    	
     }
    public static HashSet<String> getQuerySetFromDir(Map<String, Integer> method2idx, Map< Integer, String > idx2method, String Query) {
    	File directory=new File("");
    	String filePath=directory.getAbsolutePath();
    	HashSet<String> resultSet = new HashSet<String>();
    	Long methodFirstTime=getFirstEventTime(Query, filePath, method2idx);
    	System.out.println("methodFirstTime=" + methodFirstTime);
    	File dir = new File(filePath); 
        if (dir.isDirectory()) {     	            
            String[] names = dir.list();
            if (!filePath.endsWith(File.separator))
            	filePath+=File.separator;
            for (int i = 0; i < names.length; i++) {
            	if (names[i].startsWith("Message2Query"))  {            		
            		HashSet<String> midSet=getQuerySetFromFile(methodFirstTime,filePath+ names[i], idx2method, Query);
            		System.out.println("names[i]="+names[i]+" Set size="+midSet.size());	
            		resultSet.addAll(midSet);
            	}
            }
        }
    	return resultSet;			    	
     }
    
	public static String getQueryImpactSets(Set<String> finalResult, String query) {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		String resultS="";

		resultS+="==== DistODD impact set of [" + query +"]  size=" + finalResult.size() + " ====\n";
		for (String m : finalResult) {
			//System.out.println(m);
			resultS+=m+"\n";
		}
		resultS+="==== DistODD impact set of [" + query +"]  size=" + finalResult.size() + " ====\n";
		return resultS;
	}	
	public static String getQueryImpactSetSizes(Set<String> finalResult, String query) {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		String resultS="";

		resultS+="==== DistODD impact set of [" + query +"]  size=" + finalResult.size() + " ====\n";
		return resultS;
	}	
    public static HashSet<Integer> getIntraQuerySetNumberFromFile(int querySetNumber, String resultFile) {
    	HashSet<Integer> resultSet = new HashSet<Integer>();
        FileReader reader = null;  
        BufferedReader br = null;  		        
        try { 

            reader = new FileReader(resultFile);
            String str = null;     
            String midStr="";
            br = new BufferedReader(reader);
            int  methodId=0;
            long resultL=0;
            while ((str = br.readLine()) != null) {   
            	if (str.indexOf("["+querySetNumber+"]")>=0 && str.indexOf("[["+querySetNumber+"]]")>=0) {
            		HashSet<Integer> midSet = getQuerySetNumberFromString(querySetNumber, str);
            		return midSet;
            	}	
            }    
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return  resultSet;
    }    
    	
    public static HashSet<Integer> getInterSetNumberAfterLastTime(long firstTime, String resultFile) {
    	return getQuerySetNumbersAfterLastTime(firstTime, resultFile);    	
     }
    
    public static HashSet<Integer> getQuerySetNumberFromFile(long firstTime, String resultFile, int queryId) {
    	HashSet<Integer> resultSet = new HashSet<Integer>();
    	HashSet<Integer> intraSet =getIntraQuerySetNumberFromFile(queryId, resultFile);
    	System.out.println(resultFile+" intraSet.size()="+intraSet.size());	 
    	if (intraSet.size()>0)  {
    		resultSet.addAll(intraSet);
    	}
    	else  {
    		HashSet<Integer> interSet = getInterSetNumberAfterLastTime(firstTime, resultFile);
        	System.out.println(resultFile+" interSet.size="+interSet.size());	 
    		resultSet.addAll(interSet);
    	}
    	return resultSet;			    	
     }

	
    public static HashSet<Integer> getQuerySetNumberFromDir(Map<String, Integer> method2idx, String query) {
    	File directory=new File("");
    	String filePath=directory.getAbsolutePath();
    	HashSet<Integer> resultSet = new HashSet<Integer>();
    	// Long methodFirstTime=getFirstEventTime(queryId, filePath);
    	Long methodFirstTime=getFirstEventTime(query,filePath, method2idx);
    	System.out.println("methodFirstTime=" + methodFirstTime);
    	Integer queryId = method2idx.get(query);
    	System.out.println("queryId=" + queryId);
    	if (queryId==null) {
    		return resultSet;
    	}
    	File dir = new File(filePath); 
        if (dir.isDirectory()) {     	            
            String[] names = dir.list();
            if (!filePath.endsWith(File.separator))
            	filePath+=File.separator;
            for (int i = 0; i < names.length; i++) {
            	if (names[i].startsWith("Message2Query") && names[i].indexOf(".txt")>12)  {        		
            		HashSet<Integer> midSet=getQuerySetNumberFromFile(methodFirstTime,filePath+ names[i], queryId);
            		System.out.println("names[i]="+names[i]+" Set size="+midSet.size());	
            		resultSet.addAll(midSet);
            	}
            }
        }
    	return resultSet;			    	
     }
    
	public static String getQueryDependenceSets(Map< Integer, String > idx2method, Set<Integer> finalResult, String query) {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		String resultS="";

		resultS+="==== DistODD dependence set of [" + query +"]  size=" + finalResult.size() + " ====\n";
		String methodName = "";
		for (Integer methodId : finalResult) {
			//System.out.println(m);
			methodName=idx2method.get(methodId);
			resultS+=methodName+"\n";
		}
		resultS+="==== DistODD dependence set of [" + query +"]  size=" + finalResult.size() + " ====\n";
		return resultS;
	}	
	
	public static String getOnlyQueryEventStr(Map<String, Integer> method2idx, String query, String eventStr) {
		//System.out.println("getOnlyQueryEventStr query = " + query +" eventStr = " + eventStr);
		//String resultS="";
    	Integer queryId = method2idx.get(query);
    	//System.out.println("queryId=" + queryId);
    	if (queryId==null) {
    		return "";
    	}
    	String[] strs = eventStr.split("\n"); 
    	String lineStr="";
    	String queryIdStr=""+queryId+",";
    	String minusQueryIdStr="-"+queryId+",";
    	for (int i=0; i<strs.length;i++)  {
    		lineStr=strs[i];
			//System.out.println("lineStr="+lineStr+" idxStr="+idxStr);
    		if (lineStr.startsWith(queryIdStr) || lineStr.startsWith(minusQueryIdStr))  {
    				return lineStr+"\n";
    		}
    	}
		return "";
	}	
	public static String getSizeFromStr(String sizeStr) {
		String resultS="";
		String[] strs = sizeStr.split("size=");
		if (strs.length>1)  {
			String str2=strs[1];
			System.out.println("str2= " + str2);
			String[] str2s = str2.split(" ");
			System.out.println("str2s[0]= " + str2s[0]);
			System.out.println("str2s[1]= " + str2s[1]);
			if (str2s.length>0)  {
				return str2s[0];
			}
		}
		return resultS;
	}
}

