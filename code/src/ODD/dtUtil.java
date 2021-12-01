package ODD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.Set;
import java.util.regex.Pattern;

import profile.InstrumManager;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.VirtualInvokeExpr;
import MciaUtil.utils;
import dua.global.ProgramFlowGraph;
import dua.method.CFG;
import dua.method.CFG.CFGNode;
import dua.util.Util;

public class dtUtil {

	public static void main(String []args) {
		//System.out.println(isIncludePackageClass("C:/Research/nioecho/src/NioClient.java","java.net.channels.SocketChannel")); 
		//System.out.println("getMethodFromLineString="+getMethodFromLineString("public void sendMessageToClient(String message)"));
		//HashSet hs=new HashSet();  //ChatServer.handler.ClientInfo
//		int ln=getLineNum("C:/TEST/DT2BrInstrumented/entitystmt.out.branch");
//		System.out.println("ln="+ln);
		//hs.add("ClientInfo");
		//System.out.println("transferClassType(String str, String className, Set methods)="+transferClassType("ClientInfo", "ChatServer.handler", hs));
//		HashMap<String,String> hm = new HashMap<>();  getStmtNum(String longStr)
//		hm=transferfunctionList("C:/Research/Zookeeper/functionList.out", "");
//		System.out.println("transferPara="+transferPara("FileTxnSnapLog logFactory,QuorumPeer self,DataTreeBuilder treeBuilder, ZKDatabase zkDb", hm));
		//System.out.println("replaceIn1(String str)="+replaceIn1("HashSet<Long> set"));
//		System.out.println("Thread.currentThread().getStackTrace()[1].getClassName()="+Thread.currentThread().getStackTrace()[1].getClassName());
//		System.out.println("Thread.currentThread().getStackTrace()[1].getMethodName()="+Thread.currentThread().getStackTrace()[1].getMethodName());
//		System.out.println("getStmtNum(String longStr)="+getStmtNum(" STR -->  SRR  -->  ","-->"));
//		ArrayList ss1=getMethodItems("<NioClient: void read(java.nio.channels.SelectionKey)>; <RspHandler: void waitForResponse()>", "C:/Research/nioecho/methodsInPair.txt");
//		System.out.println("ss1="+ss1);
//		for (int i = 0; i < ss1.size(); i++) {
//			System.out.println(""+ss1.get(i));
//		}
//		List<String> ss1=getFileIdSort("C:/Research/nioecho", "Local", ".em");
//				//("<NioClient: void read(java.nio.channels.SelectionKey)>; <RspHandler: void waitForResponse()>", "C:/Research/nioecho/methodsInPair.txt");
//        for (String str : ss1) {
//
//            System.out.println("str="+str);
//
//       }
//		HashMap<Integer, String> allEAMethodMap = new HashMap<Integer, String>();
//		HashMap<Integer, Integer> allEASeq = new HashMap<Integer, Integer>();
//		try
//		{
//			mergeLocalTraces("C:/Research/nioecho",allEAMethodMap, allEASeq);
//		}	
//		catch (IOException e) {
//			throw new RuntimeException(e); 
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}	
//		ArrayList branchStmts = new ArrayList();
//		branchStmts=dtUtil.getArrayList("C:/Research/nioecho/entitystmt.out.branch");
//		//System.out.println("branchStmts="+branchStmts);
//		String line1=getBranchLineFromId(999, branchStmts);
//		System.out.println("line1="+line1);
		//int sc=subStrCount(String parent, String child)
//		ArrayList s2=getArrayListFromTwo("C:/Research/rocketmq/methodList.out","C:/Research/rocketmq/coveredMethods.txt");
//		//HashSet s2=getSet(System.getProperty("user.dir") + File.separator + "coveredMethods.txt");
//		System.out.println("s2.size()="+s2.size()+" s2="+s2);
		//mergeTwoArrayFiles("C:/Research/Hsqldb/methodListOld.out", "C:/Research/Hsqldb/methodListNew.out", "C:/Research/Hsqldb/methodListNew2.out");
		
		HashSet<String> hs1=getSet("C:/Research/Hsqldb/data/Sources2.txt"); 
		System.out.println("hs1.size()="+hs1.size());
		Boolean is1=isIncludeSetItem("sss read2()>", hs1);
		System.out.println("is1="+is1);
		
		HashSet<String> hs2=getSet("C:/Research/Hsqldb/data/Sinks2.txt"); 
		System.out.println("hs2.size()="+hs2.size());
		Boolean is2=isIncludeSetItem("sss write(int)>", hs2);
		System.out.println("is2="+is2);
	}
    public static String replaceIn1(String str) // 
    {
        int head = str.indexOf('<'); // 
        if (head == -1)
            ; //
        else {
            int next = head + 1; // 
            int count = 1; // 
            do {
                if (str.charAt(next) == '<')
                    count++;
                else if (str.charAt(next) == '>')
                    count--;
                next++; //
                if (count == 0) // 
                {
                    String temp = str.substring(head, next); //
                    str = str.replace(temp, ""); //
                    head = str.indexOf('<'); // 
                    next = head + 1; // 
                    count = 1; // 
                }
            } while (head != -1); // 
        }
        return str; //
    }
	   public static String transferPara(String str, HashMap<String,String> classePackages)
	   {
		   String resultStr="";
		   try {
			       	String[] strs=replaceIn1(str).replace(",", " ").replace("  ", " ").split(" "); 
			       	int strslength=strs.length;
			       	
			   		//System.out.print("strs.length="+strs.length+"\n");
			       	if (strslength<1)
			       	{
			       		return str;
			       	}
			       	else
			       	{
			       		//String fullClass="";
			       		String basicType="";
			       		String addStr="";
			       		for (int i=0; i<strslength; i++)
			       		{
			       			if (i % 2==0)
			       			{
			       				//System.out.print("strs[i]="+strs[i]+"\n");
			       				//System.out.print("transferClassType(strs[i],classePackages)="+transferClassType(strs[i],classePackages)+"\n");
			       				addStr="";
			       				basicType=transferBasicType(strs[i]);
			       				//System.out.print("basicType="+basicType+"\n");
			       				if (i+1<strslength)
			       				{
			       					String midStr=strs[i+1].replace(",", "").trim();
			       					//System.out.print("i="+i+" midStr="+midStr+"\n");
			       					int midStrlength=midStr.length();
			       					//System.out.print("midStrlength="+midStrlength+" midStr.substring(midStrlength-2, midStrlength="+midStr.substring(midStrlength-2, midStrlength)+"\n");
			       					if (midStrlength>2 && (midStr.substring(midStrlength-2, midStrlength).equals("[]")))
	       							{
			       						addStr="[]";
	       							}
				       				//System.out.print("addStr="+addStr+"\n");
			       				}
			       				if (basicType.indexOf(".")<0)
			       				{
			       					resultStr=resultStr+transferClassType(strs[i],classePackages)+addStr+",";
			       				}
			       				else
			       					resultStr=resultStr+basicType+addStr+",";
			       				//System.out.print("resultStr="+resultStr+"\n");
			       			}
			       		}
			       	}
			       	int resultStrlength=resultStr.length();
			       	//System.out.print("resultStrlength="+resultStrlength+"\n");
			       	if (resultStrlength<2)
			       		return resultStr; 
			       	//System.out.print("resultStr.substring(resultStrlength-1, resultStrlength)="+resultStr.substring(resultStrlength-1, resultStrlength)+"\n");
			       	if (resultStr.substring(resultStrlength-1, resultStrlength).equals(","))
			       	{	
			       		return resultStr.substring(0,resultStrlength-1);
			       	}
			       	else
			       		return resultStr; 
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return str;
	       }  
	   }
	   public static String transferPara(String str, String functionListFile)
	   {
		   String resultStr="";
		   try {
			       	String[] strs=replaceIn1(str).replace(",", " ").replace("  ", " ").split(" "); 
			       	int strslength=strs.length;
			       	
			   		//System.out.print("strs.length="+strs.length+"\n");
			       	if (strslength<1)
			       	{
			       		return str;
			       	}
			       	else
			       	{
			       		//String fullClass="";
			       		String basicType="";
			       		String addStr="";
			       		String itemStr="";
			       		HashMap<String,String> classePackages = new HashMap<>(); 
			       		classePackages=transferfunctionList("C:/Research/Zookeeper/functionList.out", "");
			       		for (int i=0; i<strslength; i++)
			       		{
			       			if (i % 2==0)
			       			{
			       				//System.out.print("strs[i]="+strs[i]+"\n");
			       				//System.out.print("transferClassType(strs[i],classePackages)="+transferClassType(strs[i],classePackages)+"\n");
			       				addStr="";
			       				basicType=transferBasicType(strs[i]);
			       				//System.out.print("basicType="+basicType+"\n");
			       				if (i+1<strslength)
			       				{
			       					String midStr=strs[i+1].replace(",", "").trim();
			       					//System.out.print("i="+i+" midStr="+midStr+"\n");
			       					int midStrlength=midStr.length();
			       					//System.out.print("midStrlength="+midStrlength+" midStr.substring(midStrlength-2, midStrlength="+midStr.substring(midStrlength-2, midStrlength)+"\n");
			       					if (midStrlength>2 && (midStr.substring(midStrlength-2, midStrlength).equals("[]")))
	       							{
			       						addStr="[]";
	       							}
				       				//System.out.print("addStr="+addStr+"\n");
			       				}
			       				if (basicType.indexOf(".")<0)
			       				{
			       					itemStr=transferClassType(strs[i],classePackages);
			       				}
			       				else
			       					itemStr=basicType;
			       				resultStr=resultStr+itemStr+addStr+",";
			       				//System.out.print("resultStr="+resultStr+"\n");
			       			}
			       		}
			       	}
			       	int resultStrlength=resultStr.length();
			       	//System.out.print("resultStrlength="+resultStrlength+"\n");
			       	if (resultStrlength<2)
			       		return resultStr; 
			       	//System.out.print("resultStr.substring(resultStrlength-1, resultStrlength)="+resultStr.substring(resultStrlength-1, resultStrlength)+"\n");
			       	if (resultStr.substring(resultStrlength-1, resultStrlength).equals(","))
			       	{	
			       		return resultStr.substring(0,resultStrlength-1);
			       	}
			       	else
			       		return resultStr; 
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return str;
	       }  
	   }	   
	   public static String transferClassType(String str, String packageName, HashSet classes)
	   {
		   try {
			   if (classes.contains(str) && str.indexOf(".")<0)
			   {
				   return packageName+"."+str;
			   }
			   else
				   return str; 
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   }
	   public static String transferClassType(String str, HashMap classePackages)
	   {
		   try {
			   if (classePackages.containsKey(str) && str.indexOf(".")<0)
			   {
				   return  classePackages.get(str)+"."+str;
			   }
			   else
				   return str; 
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   }
	   public static String transferBasicType(String str)
	   {
		   try {
			   if (str.indexOf(".")>=0)
			   {
				   return str;
			   }
			   else if (str.equals("String"))
			   {
				   return "java.lang.String";
			   }
			   else if (str.equals("List"))
			   {
				   return "java.util.List";
			   }
			   else if (str.equals("Set"))
			   {
				   return "java.util.Set";
			   }
			   else if (str.equals("InetSocketAddress"))
			   {
				   return "java.net.InetSocketAddress";
			   }
			   else if (str.equals("Integer"))
			   {
				   return "java.lang.Integer";
			   }
			   else if (str.equals("Long"))
			   {
				   return "java.lang.Long";
			   }
			   else if (str.equals("Set"))
			   {
				   return "java.util.Set";
			   }
			   else if (str.equals("InputStream"))
			   {
				   return "java.io.InputStream";
			   }
			   else if (str.equals("OutputStream"))
			   {
				   return "java.io.OutputStream";
			   }
			   else if (str.equals("DataInput"))
			   {
				   return "java.io.DataInput";
			   }
			   else if (str.equals("DataOutput"))
			   {
				   return "java.io.DataOutput";
			   }
			   else if (str.equals("HashSet"))
			   {
				   return "java.util.HashSet";
			   }
			   else if (str.equals("Collection"))
			   {
				   return "java.util.Collection";
			   }
			   else if (str.equals("List"))
			   {
				   return "java.util.List";
			   }
			   else if (str.equals("HashSet"))
			   {
				   return "java.util.HashSet";
			   }
			   else if (str.equals("HashMap"))
			   {
				   return "java.util.HashMap";
			   }
			   else if (str.equals("ArrayList"))
			   {
				   return "java.util.ArrayList";
			   }
			   else if (str.equals("Map"))
			   {
				   return "java.util.Map";
			   }
			   return str;
		   }catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   }
	   public static String getClassFromString(String str) {  
		   	
	       try {  
	           // check the string str
	       	if (str.length()<3)
	       	{
	       		return "";
	       	}    	  
	   		//System.out.print("str="+str+"\n");
	       	String[] strs=str.split("\\."); 
	   		//System.out.print("strs.length="+strs.length+"\n");
	       	if (strs.length<1)
	       	{
	       		return "";
	       	}
	       	else
	       	{
	       		return strs[strs.length-1];
	       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   }  
	   
	   public static String getPackageFromString(String str) {  
		   	
	       try {  
	           // check the string str
	       	if (str.length()<3)
	       	{
	       		return "";
	       	}    	  
	   		//System.out.print("str="+str+"\n");
	       	String[] strs=str.split("\\."); 
	   		//System.out.print("strs.length="+strs.length+"\n");
	       	if (strs.length<1)
	       	{
	       		return "";
	       	}
	       	else
	       	{   String resultStr="";
	       		for (int i=0; i<strs.length-1; i++)
	       			resultStr+=strs[i]+".";
	       		return resultStr+"*";
	       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   
	   public static String getPackageFromLineString(String str) {  
		   	
	       try {  
	           // check the string str
	       	if (str.indexOf("package ")!=0)
	       	{
	       		return "";
	       	}    	  
	   		//System.out.print("str="+str+"\n");
	       	String[] strs=str.split(" "); 
	   		//System.out.print("strs.length="+strs.length+"\n");
	       	if (strs.length<2)
	       	{
	       		return "";
	       	}
	       	else
	       	{   
	       		return strs[1].replace(";","");
	       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   public static String getClassFromLineString(String str) {  
		   	
	       try {  
	           // check the string str
	       	if (str.indexOf("public class ")!=0)
	       	{
	       		return "";
	       	}    	  
	   		//System.out.print("str="+str+"\n");
	       	String[] strs=str.split(" "); 
	   		//System.out.print("strs.length="+strs.length+"\n");
	       	if (strs.length<3)
	       	{
	       		return "";
	       	}
	       	else
	       	{   
	       		return strs[2].replace("{", "");
	       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   //public static void main(String[] args) throws InterruptedException
	   public static String getMethodFromLineString(String str) {  
		   	
	       try {  
	            String strIn="";
		       	if (str.indexOf("public ")!=0 && str.indexOf("protected ")!=0&& str.indexOf("private ")!=0)
		       	{
		       		return "";
		       	}    	  
		       	String midStr=str.replace("public","").replace("protected","").replace("private","").replace("static","").replace("synchronized","").replace("abstract","").replace("{", "").trim();
		   		//System.out.print("str="+str+"\n");
		       	String[] strs=midStr.split("\\)"); 
		   		System.out.print("strs.length="+strs.length+"\n");
		   		
		   		String midStr2=strs[0];
	       		String[] strs2=midStr2.split("\\("); 
		   		System.out.print("strs2.length="+strs2.length+"\n");
	       		if (strs2.length>1)
		       	{
		       		strIn=strs2[1];
		       		System.out.print("strIn="+strIn+"\n");
		       	}
		       	if (strs.length<2)
		       	{
		       		return midStr;
		       	}
		       	else 
		       	{   
		       		
		       		return strs[0]+")";
		       	}
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   public static boolean isIncludePackageClass(String javaFile, String listStr) {  	   	
	       try {
	   		FileReader reader = null;      
	        BufferedReader br = null;    
	        reader = new FileReader(javaFile);   
	        br = new BufferedReader(reader);
	        String str = "";  
	        String strtrim="";
	        boolean hasPackage=false;
	        boolean hasClass=false;
//	        String packageStr1="import "+listStr+";";
//	        String packageStr2="import "+getPackageFromString(listStr)+";";
	        String packageStr1=listStr+";";
	        String packageStr2=getPackageFromString(listStr)+";";
	        String classStr=getClassFromString(listStr)+" ";
	        System.out.println(" packageStr1="+packageStr1+" packageStr2="+packageStr2+" classStr="+classStr);
	        while((str = br.readLine()) != null)
	        {	        	
	        	
	        	strtrim=str.trim();
	        	System.out.print(" strtrim="+strtrim+"\n");
	        	if (strtrim.indexOf("import ")==0)
	        	{

	        		System.out.println(" strtrim.indexOf(packageStr1)="+strtrim.indexOf(packageStr1)+" strtrim.indexOf(packageStr2)="+strtrim.indexOf(packageStr2));
	        		if (strtrim.indexOf(packageStr1)>6 || strtrim.indexOf(packageStr2)>6)
	        		{
	        			hasPackage=true;
	        		}
	        	}
	        	if (hasPackage)
	        	{
	        		if (strtrim.indexOf(classStr)==0)
	        		{
	        			hasClass=true;
	        		}
	        	}
	        	System.out.println(" hasPackage="+hasPackage+" hasClass="+hasClass);
	        	if (hasPackage && hasClass)
	        		return true;
	        	// read lines
	            //str = br.readLine();
	        }        

	       			return (hasPackage && hasClass);       
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return false;
	       }  
	   } 
	   
	   public static String methodIncludePackageClass(String javaFile, String listStr) {  	   	
	       try {  
	    	String methodName="";   
	    	String packageName="";   
	   		FileReader reader = null;      
	        BufferedReader br = null;    
	        reader = new FileReader(javaFile);   
	        br = new BufferedReader(reader);
	        String str = "";  
	        String strtrim="";
	        boolean hasPackage=false;
	        boolean hasClass=false;
//	        String packageStr1="import "+listStr+";";
//	        String packageStr2="import "+getPackageFromString(listStr)+";";
	        String packageStr1=listStr+";";
	        String packageStr2=getPackageFromString(listStr)+";";
	        String classStr=getClassFromString(listStr)+" ";
	        System.out.println(" packageStr1="+packageStr1+" packageStr2="+packageStr2+" classStr="+classStr);
	        while((str = br.readLine()) != null)
	        {	        	
	        	
	        	strtrim=str.trim();
	        	System.out.print(" strtrim="+strtrim+"\n");
	        	if (strtrim.indexOf("package ")==0)
	        	{
	        		
	        	}
	        	if (strtrim.indexOf("import ")==0)
	        	{

	        		System.out.println(" strtrim.indexOf(packageStr1)="+strtrim.indexOf(packageStr1)+" strtrim.indexOf(packageStr2)="+strtrim.indexOf(packageStr2));
	        		if (strtrim.indexOf(packageStr1)>6 || strtrim.indexOf(packageStr2)>6)
	        		{
	        			hasPackage=true;
	        		}
	        	}
	        	if (hasPackage)
	        	{
	        		if (strtrim.indexOf(classStr)==0)
	        		{
	        			hasClass=true;
	        		}
	        	}
	        	System.out.println(" hasPackage="+hasPackage+" hasClass="+hasClass);
	        	if (hasPackage && hasClass)
	        		return methodName;
	        	// read lines
	            //str = br.readLine();
	        }        

	       			return methodName;       
	       } catch (Exception e) {  
	           e.printStackTrace();  
	           return "";
	       }  
	   } 
	   
	    public static HashMap transferfunctionList(String oldFile, String newFile) {  
	        FileWriter writer = null;  
	        FileReader reader = null;  
	        BufferedReader br = null;  
	        BufferedWriter bw = null;  	   
	        //HashSet<String> classes = new HashSet<String>();	
	        HashMap<String, String> classPackages = new HashMap<>();  
	        try {  
	            
	               
	            //    
	            reader = new FileReader(oldFile);  	   
	            String str = null;  	   
	            br = new BufferedReader(reader);
	            String allName = "";      	   
	            String className = "";  
	            String packageName = ""; 
	            int strs2length=0;
	            while ((str = br.readLine()) != null) {  	  
	            	//System.out.print("str="+str+"\n");
	                StringBuffer sb = new StringBuffer("");   
	    	       	String[] strs=str.split(":"); 	    	  	  
	            	//System.out.print("strs[0]="+strs[0]+"\n");
	    	       	allName=strs[0].replace("<", "");   	  	  
	            	//System.out.print("allName="+allName+"\n");
	            	String[] strs2=allName.split("\\."); 
	            	strs2length=strs2.length;
	    	       	className=strs2[strs2length-1]; 	  	  
	            	//System.out.print("className="+className+"\n");
	    	       	packageName = "";
	    	       	for (int i=0; i<strs2length-2; i++)
	    	       		packageName = packageName+strs2[i]+".";
	    	       	if (strs2length>=2)
	    	       		packageName = packageName+strs2[strs2length-2]; 	  	  
	            	//System.out.print("packageName="+packageName+"\n");
	    	       	if (className.length()>1 &&  packageName.length()>1)
	    	       		classPackages.put(className, packageName);
	            }  
	   
	            br.close();  
	            reader.close();  
	            if (newFile.length()<1)
	            	return classPackages;
	            File file = new File(newFile);  
	            if (!file.exists()) {  
	                file.createNewFile();  
	            }  
	            writer = new FileWriter(newFile, true);  	
	            bw = new BufferedWriter(writer);
	            Iterator<Entry<String, String>> iterator = classPackages.entrySet().iterator();  
	            while (iterator.hasNext()) {  
	                Entry<String, String> entry = iterator.next();  
	                bw.write(entry.getValue()+"."+entry.getKey()+"\n"); 
	            }  
	            
	            bw.close();  
	            writer.close();  
	            return classPackages;
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            return null;
	        }  
	    }
	    
	    public static HashMap transferfunctionList() {  
	    	return transferfunctionList("functionList.out", "classList.out");
	    }
	    
	    public static HashSet<String> getListSet(String listFile) {  
	        FileReader reader = null;  
	        BufferedReader br = null;    
	        //HashSet<String> classes = new HashSet<String>();	
	        HashSet<String> lists = new HashSet<>();  
	        try {  
	            
	               
	            //    
	            reader = new FileReader(listFile);  	   
	            String str = null;  	   
	            br = new BufferedReader(reader);

	            while ((str = br.readLine()) != null) {  	  
	    	       	if (str.length()>0)
	    	       		lists.add(str);
	            }  
	   
	            br.close();  
	            reader.close();  
	           
	            return lists;
	        } catch (IOException e) {  
	            //e.printStackTrace();  
	            return null;
	        }  
	    }

	    
	    
	    public static void writeListSet(HashMap<String, Integer> hm, String dest) {  
       
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	   
	        try {  
	            File file = new File(dest);  
	            file.createNewFile(); 	           
	            // 
	            writer = new FileWriter(dest, true); 
	            bw = new BufferedWriter(writer);  
		    	Iterator iter = hm.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					bw.write(entry.getKey()+"\n");
				}
	            bw.close();  
	            writer.close();  
	   
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }
	    
	    public static HashSet<String> getSet(String listFile) { 
	    	return getListSet(listFile);
	    }
		public static HashSet<String> getSetFromTwo(String listFile1, String listFile2) {
			HashSet<String>  resultA = new HashSet<String>(); 
			resultA=getSet(listFile1);
			if (resultA==null || resultA.size()<1)
				resultA=getSet(listFile2);
	        return resultA; 
		}
	    public static void writeSet(HashSet hs, String dest) {  
	        
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	   
	        try {  
	            File file = new File(dest);  
	            file.createNewFile(); 	           
	            // 
	            writer = new FileWriter(dest, true); 
	            bw = new BufferedWriter(writer);  
	            for (Object s: hs)
	    		{
	            	bw.write(s+"\n");
	    		}
	            bw.close();  
	            writer.close();  
	   
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }
	    
	    public static void writeArrayList(ArrayList al, String dest) {  
	        
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	   
	        try {  
	            File file = new File(dest);  
	            file.createNewFile(); 	           
	            // 
	            writer = new FileWriter(dest, true); 
	            bw = new BufferedWriter(writer);  
	            for (int i=0; i<al.size(); i++)
	            {
	            	bw.write(al.get(i).toString().trim()+"\n");
	            }            
		    	
	            bw.close();  
	            writer.close();  
	   
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }	   
	    public static void writeListMap(List<Map.Entry<String, Integer>> lm, String dest, String LastItem, boolean isCheck) {  
	        
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	        String oldFileMsg="";
	        String lineStr="";
	        try {  
	            File file = new File(dest);  
	            file.createNewFile(); 	           
	            // 
	            writer = new FileWriter(dest, true); 
	            bw = new BufferedWriter(writer);  
	            oldFileMsg=readToString(dest);
	            for(Map.Entry<String, Integer> entry: lm){
	            	lineStr=entry.getKey().trim();
	            	if (isCheck) {
	            		if (!oldFileMsg.contains(lineStr))
	            			bw.write(lineStr+"\n");
	            	}
	            	else
	            		bw.write(lineStr+"\n");
					if (lineStr.equals(LastItem))
					{						
						break;
					}
		        }   		    	
	            bw.close();  
	            writer.close();  
	   
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }	    
	    
	    public static void writeMethodPairListMap(String methodPairMsg, List<Map.Entry<String, Integer>> lm, String dest, String LastItem, boolean isCheck) {  
	        
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	        String oldFileMsg="";
	        String lineStr="";
	        try {  
	            File file = new File(dest);  
	            file.createNewFile(); 	           
	            // 
	            writer = new FileWriter(dest, true); 
	            bw = new BufferedWriter(writer);  
	            bw.write(methodPairMsg+"\t");
	            oldFileMsg=readToString(dest);
	            for(Map.Entry<String, Integer> entry: lm){
	            	lineStr=entry.getKey().trim();
	            	if (isCheck) {
	            		if (!oldFileMsg.contains(lineStr))
	            			bw.write(lineStr+" | ");
	            	}
	            	else
	            		bw.write(lineStr+" | ");
					if (lineStr.equals(LastItem))
					{						
						break;
					}
		        }   	
	            bw.write(lineStr+"\n");
	            bw.close();  
	            writer.close();  
	   
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }	    
		public static ArrayList getArrayList(String listFile) {
			//System.out.println("listFile="+listFile);
			ArrayList  resultA = new ArrayList(); 
	        FileReader reader = null;  
	        BufferedReader br = null;     
	        try {  
	   
	            reader = new FileReader(listFile);  
	            br = new BufferedReader(reader);              
	            String str = null;  
	            //int count=0;
	            while ((str = br.readLine()) != null) {  
	            	//System.out.println("str="+str);
	            	if (str.length()>1)
	            	{
	            		if (!resultA.contains(str))
	            			resultA.add(str);	            		
	            	}
	            }     
	            br.close();  
	            reader.close(); 
	        } catch (Exception e) {  
	            //e.printStackTrace();   
	               
	        }  
	        return resultA; 
		}

		public static ArrayList getArrayListFromTwo(String listFile1, String listFile2) {
			ArrayList  resultA = new ArrayList(); 
			resultA=getArrayList(listFile1);
			//System.out.println("resultA.size()="+resultA.size());
			if (resultA==null || resultA.size()<1) {
				ArrayList  resultB = new ArrayList(); 
				resultB=getArrayList(listFile2);
				//System.out.println("resultB.size()="+resultB.size());
				return resultB; 
			}
			else
				return resultA; 
		}
		public static HashMap getSourceSinksFromFile(String stmtFile) {
			ArrayList  sources = new ArrayList(); 
			ArrayList  sinks = new ArrayList(); 
			HashMap map = new HashMap();
	        FileReader reader = null;  
	        BufferedReader br = null;     
	        try {  
	   
	            reader = new FileReader(stmtFile);  
	            br = new BufferedReader(reader);              
	            String str = null;  
	            //int count=0;
	            while ((str = br.readLine()) != null) {  
	            	//System.out.println("str="+str);
	            	if (str.length()>1)
	            	{
	            		//if (sinks.contains(str))
	        				//continue;
	            		String strs[]=str.split("; ");
	                	//System.out.println("strs.length="+strs.length+" strs[1]="+strs[1]);
	            		if (strs.length>1)
	            		{	
	            			sources.add(strs[0].trim());
	            			sinks.add(strs[1].trim());
	            		}

	            	}
	            }     
	            br.close();  
	            reader.close();    
	            map.put(1,sources);
	            map.put(2,sinks);
	            return map;   
	        } catch (Exception e) {  
	            e.printStackTrace();   
	            return map;    
	        }  
		}
		
		public static ArrayList getMethodItems(String pairName, String listFile) {
			ArrayList  resultA = new ArrayList(); 
			
			
	        FileReader reader = null;  
	        BufferedReader br = null;     
	        try {  
	        	String lineStr="";
	            reader = new FileReader(listFile);  
	            br = new BufferedReader(reader);              
	            String str = "";  
	            //int count=0;
	            while ((str = br.readLine()) != null) {  
	            	//System.out.println("str="+str);
	            	if (str.startsWith(pairName))
	            	{
	            		lineStr=str.substring(pairName.length(),str.length());
	            		break;
	            	}
	            }     
	            //System.out.println("lineStr="+lineStr);
	            br.close();  
	            reader.close();     
	            String tmpStr="";
	            String strs[]=lineStr.split(" \\| ");
	        	for (int i=0; i<strs.length;i++)  {     
	        		tmpStr=strs[i].trim();
	        		//System.out.println("tmpStr="+tmpStr);
	        		if (tmpStr.length()>0 && !resultA.contains(tmpStr))  {
	        			resultA.add(tmpStr);
	        		}	
	        	}	   
	            
	            return resultA;   
	        } catch (Exception e) {  
	            e.printStackTrace();   
	            return resultA;    
	        }  
		}

	    public static boolean itemInArrayList(ArrayList al, String str) {  
	        

            for (int i=0; i<al.size(); i++)
            {
            	if (str.indexOf(al.get(i).toString().trim())>=0)
            		return true;
            }            
		    return false;	
	          
	    }	 
	    
	    public static int getLineNum(String listFile) {  
	    	int lineNum=0;
	        FileReader reader = null;  
	        BufferedReader br = null;    
	       
	        try {  
	            reader = new FileReader(listFile);  	   
	            String str = null;  	   
	            br = new BufferedReader(reader);

	            while ((str = br.readLine()) != null) {  	  
	            	lineNum++;
	            }  
	   
	            br.close();  
	            reader.close();  
	          
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            return 0;
	        }  
	        return lineNum;
	    }
	    
	    public static int getStmtNum(String longStr, String separation) {  
	    	if (longStr.length()<1)
	    		return 0;
	    	String midStr=longStr.trim();
	    	String[] strs = midStr.split(separation);
	    	return strs.length;
//	    	System.out.println("strs.length="+strs.length+" midStr.lastIndexOf="+midStr.lastIndexOf("-->")+" midStr.length()="+midStr.length());
//	    	if (midStr.lastIndexOf("-->")==(midStr.length()-3))  {
//	    		return strs.length;
//	    	}
//	    	else
//	    		return strs.length+1;
	       
	    }
		public static String readToString(String fileName) {  
	        String encoding = "UTF-8";  
	        File file = new File(fileName);  
	        Long filelength = file.length();  
	        byte[] filecontent = new byte[filelength.intValue()];  
	        try {  
	            FileInputStream in = new FileInputStream(file);  
	            in.read(filecontent);  
	            in.close();
	            return new String(filecontent, encoding);
	        } catch (Exception e) {  
	            e.printStackTrace();  
	            return "";
	        } 
	        
	    }  
	    public static ArrayList<File> getFiles(String realpath, ArrayList<File> files, String startsWith, String endsWith) {
	        File realFile = new File(realpath);
	        if (realFile.isDirectory()) {
	            File[] subfiles = realFile.listFiles();
	            for (File file : subfiles) {
//	                if (file.isDirectory()) {
//	                    getFiles(file.getAbsolutePath(), files, startsWith, endsWith);
//	                } else 
	                {
	                	if (file.isFile() &&  file.getName().startsWith(startsWith) && file.getName().endsWith(endsWith))
	                		files.add(file);
	                }
	            }
	        }
	        return files;
	    }
	    
	    public static ArrayList<File> getFileSort(String path, String startsWith, String endsWith) {
	    	ArrayList<File> list = getFiles(path, new ArrayList<File>(), startsWith, endsWith);
	    	if (list != null && list.size() > 0) {
	    		Collections.sort(list, new Comparator<File>() {
	    			public int compare(File file, File newFile) {
	    				if (file.lastModified() < newFile.lastModified()) {
	    					return -1;
	    				} else if (file.lastModified() == newFile.lastModified()) {
	    					return 0;
	    				} else {
	    					return 1;
	    				}
	    			}

	    		});
	    	}
	    	return list;
	    }	

	    public static List<String> getFileIds(String realpath, List<String> files, String startsWith, String endsWith) {
	        File realFile = new File(realpath);
	        if (realFile.isDirectory()) {
	            File[] subfiles = realFile.listFiles();
	            for (File file : subfiles) {
//	                if (file.isDirectory()) {
//	                    getFileIds(file.getAbsolutePath(), files, startsWith, endsWith);
//	                } else 
	            	{
	                	if (file.isFile() &&  file.getName().startsWith(startsWith) && file.getName().endsWith(endsWith))
	                		files.add(file.getName().replace(startsWith, "").replace(endsWith, ""));
	                }
	            }
	        }
	        return files;
	    }

	    public static List<String> getFileIdSort(String path, String startsWith, String endsWith) {
	    	List<String> list=getFileIds(path, new ArrayList<String>(), startsWith, endsWith);
	    	if (list != null && list.size() > 0) {
	    	       Collections.sort(list,new Comparator<String>() {
	    	            @Override
	    	            public int compare(String o1, String o2) {
	    	                if(o1 == null || o2 == null){
	    	                    return -1;
	    	                }
	    	                if(o1.length() > o2.length()){
	    	                    return 1;
	    	                }
	    	                if(o1.length() < o2.length()){
	    	                    return -1;
	    	                }
	    	                if(o1.compareTo(o2) > 0){
	    	                    return 1;
	    	                }
	    	                if(o1.compareTo(o2) < 0){
	    	                    return -1;
	    	                }
	    	                if(o1.compareTo(o2) == 0){
	    	                    return 0;
	    	                }
	    	                return 0;
	    	            }
	    	        });
//	    	        for(String s:list){
//	    	            System.out.println(s);
//	    	        }

	    	}
	    	return list;
	    }	
	    
	    public static String getBranchLineFromId(int stmtId, ArrayList<String> branchStmts)
	    {
	    	//String resultLine="";
	    	for (String oneLine: branchStmts)
			{
	    		if (oneLine.indexOf(stmtId+" ")>=0 || oneLine.indexOf(" "+stmtId)>0)
	    			return oneLine;
			}
	    	return ""; 
	    }
	    
	    public static int subStrCount(String parent, String child) {
	    	try {
		    	String[] arr = parent.split(child);
				return (arr.length - 1); 
	    	} catch (Exception e) {
	    		return 0;
	    	}
	    }
	    
		public static void mergeTwoArrayFiles(String listFile1, String listFile2, String listFile3) {
			HashSet<String>  resultA = new HashSet<String>(); 
			resultA=getSet(listFile1);
			System.out.println("resultA.size()="+resultA.size());
			HashSet<String>  resultB = new HashSet<String>(); 
			resultB=getSet(listFile2);
			System.out.println("resultB.size()="+resultB.size());
			resultA.addAll(resultB);
			System.out.println("Merged resultA.size()="+resultA.size());
			writeSet(resultA, listFile3);
		}
		
		public static boolean isIncludeSetItem(String LongStr, HashSet<String>strSet) {			
			for (String str: strSet) {				
				if (LongStr.indexOf(str)==0 || LongStr.indexOf(" "+str)>=0) {
					//System.out.println("LongStr="+LongStr+" str="+str+" LongStr.indexOf(str)="+LongStr.indexOf(str));
					return true;
				}
					
					
			}
			return false; 
			
		}
		
		public static String includeSetItem(String LongStr, HashSet<String>strSet) {			
			for (String str: strSet) {				
				if (LongStr.indexOf(str)==0 || LongStr.indexOf(" "+str)>0) {
					//System.out.println("LongStr="+LongStr+" str="+str+" LongStr.indexOf(str)="+LongStr.indexOf(str));
					return str;
				}
					
					
			}
			return "";  
			
		}
//	    public static boolean isInteger(String str) { 
//	        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$"); 
//	        return pattern.matcher(str).matches(); 
//	}
//		@SuppressWarnings("unchecked")
//		public static void mergeLocalTraces(String traceDir,HashMap<Integer, String> allEAMethodMap, HashMap<Integer, Integer> allEASeq)  throws IOException, ClassNotFoundException {
//			
//		
//			
//			// load trace of all processes into memory
//			long fixtime = System.currentTimeMillis();
////			HashMap<Integer, Integer> EASeq = new HashMap<Integer, Integer>();
////			HashMap<Integer, String> EAMethodMap = new HashMap<Integer, String>();
//			//String fname="";		
//			String methodName="";
//			try {
//				List<File> traceFileList=dtUtil.getFileSort(traceDir, "Local", ".em");
////				for (File loc : traceFileList) {
////					System.out.println("loc.getName()="+loc.getName());
////				}	
//				for (File loc : traceFileList) {
//					System.out.println("loc.getName()="+loc.getName());
//					FileInputStream fis = new FileInputStream(loc);
//					int methodId=0;
//					//fis = new FileInputStream(fnSource);
//					ObjectInputStream ois = new ObjectInputStream(fis);
//					HashMap<Integer, String> EAMethodMap = new HashMap<Integer, String>();
//					HashMap<Integer, Integer> EASeq = new HashMap<Integer, Integer>();
//					
//				
//					Map<String,Integer> EAmethod2idx = new LinkedHashMap<String,Integer>();
//					@SuppressWarnings("unchecked")
//					LinkedHashMap<String,Integer> readObject1 = (LinkedHashMap<String,Integer>) ois.readObject();
//					System.out.println("readObject1.size()="+readObject1.size()+" readObject1="+readObject1);
//					EAmethod2idx = readObject1;
//					System.out.println("EAmethod2idx.size()="+EAmethod2idx.size()+" EAmethod2idx="+EAmethod2idx);
//					for (Map.Entry<String, Integer> en : EAmethod2idx.entrySet()) {
//						// create an inverse map for facilitating quick retrieval later on
//						EAMethodMap.put(en.getValue(), en.getKey());
//						//curL.put(en.getKey(),en.getValue());
//					}
//					System.out.println("EAMethodMap.size()="+EAMethodMap.size()+" EAMethodMap="+EAMethodMap);
//					Iterator<Entry<Integer, String>> iterator0= EAMethodMap.entrySet().iterator();  
//					while(iterator0.hasNext())  {
//						Entry<Integer, String> entry = iterator0.next();  
//						methodId=entry.getKey();
//						methodName=entry.getValue();		
//						System.out.println("methodId="+methodId+" methodName="+methodName);
//					}
//					allEAMethodMap.putAll(EAMethodMap);
//					
//					@SuppressWarnings("unchecked")
//					LinkedHashMap<Integer, Integer> readObject2 = (LinkedHashMap<Integer, Integer>) ois.readObject();
//					System.out.println("readObject2.size()="+readObject2.size()+" readObject2="+readObject2);
//					EASeq = readObject2;
//					System.out.println("EAMethodMap.size()="+EAMethodMap.size()+" EASeq.size()="+EASeq.size()+" EASeq="+EASeq);
//					Iterator<Entry<Integer, Integer>> iterator= EASeq.entrySet().iterator();  
//					int EASeqKey=-1;
//					while(iterator.hasNext())  {
//						Entry<Integer, Integer> entry = iterator.next();  
//						EASeqKey=entry.getKey();
//						methodName=EAMethodMap.get(EASeqKey);
//						System.out.println("EASeqKey="+EASeqKey+" methodName="+methodName+" EASeqValue="+entry.getValue());
//					}
//					allEASeq.putAll(EASeq);
//	
//				}
//			}	
//			catch (IOException e) {
//				throw new RuntimeException(e); 
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}	
//			
//		}
	    public static void writeStringToFile(String str, String dest) {  
	        FileWriter writer = null;  
	        BufferedWriter bw = null;  
	   
	        try {  
	            File file = new File(dest);  
	            if (!file.exists()) {  
	                file.createNewFile();  
	            }  
	            writer = new FileWriter(dest, false); 
	            bw = new BufferedWriter(writer);  
	            bw.write(str);    
	            bw.close();  
	            writer.close();  	
	            //System.out.println("str="+str+" dest="+dest);
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
}
