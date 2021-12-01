package ODD;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;   
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;   
import javax.xml.parsers.DocumentBuilderFactory;   

import org.w3c.dom.Document;   
import org.w3c.dom.NodeList;   

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import soot.SootMethod;

public class dtConfig {

	 public static void main(String []args) {
		 /*
		 //readXML("C:/Research/data");  		 
//		HashMap<String,String> sources = new HashMap<>(); 
//		HashMap<String,String> sinks = new HashMap<>(); 
//		 sources=getSourceMap("C:/Research/data/");  		 getMethodItem
//		 sinks=getSinkMap("C:/Research/data/");  
//			HashSet<String> sources = new HashSet<>(); 
			HashSet<String> sources = new HashSet<>(); 
			sources=getFirstHashSet("C:/Research/data/Sources.txt", "\t");  
			System.out.println(" sources="+sources);
////			HashSet<String> sourceMethod=getSecondHashSet("C:/Research/data/Sources.txt", "\t","java.io.DataInputStream");
////			System.out.println(" sourceMethod="+sourceMethod);
//			String sourceItem= itemInList("i0 = virtualinvoke r2.<java.nio.channels.SocketChannel: int read(java.nio.ByteBuffer)>($r7);",sources);
//			System.out.println(" sourceItem="+sourceItem);
//			HashSet<String> sourceMethods=getSecondHashSet("C:/Research/data/Sources.txt", "\t",sourceItem);
//			System.out.println(" sourceMethods="+sourceMethods);
//			String sourceMethod= itemInList("i0 = virtualinvoke r2.<java.nio.channels.SocketChannel: int read(java.nio.ByteBuffer2)>($r7);",sourceMethods);
//			System.out.println(" sourceMethod="+sourceMethod);
*/			
		    LinkedHashMap<String, String> classMethods=getClassesMethods("C:/Research/Hsqldb/data/Sources.txt", "\t");
			System.out.println("classMethods: "+ classMethods);
//	        for(Map.Entry<String, String> entry: classMethods.entrySet())
//	        {
//	         System.out.println("ClassMethod: "+ entry.getKey()+ ": "+entry.getValue());
//	        }
			//			 sources=getSourceSet("C:/Research/data/");  		 
//			 sinks=getSinkSet("C:/Research/data/"); 

//			 String class1=itemInList("i0 = virtualinvoke r2.<java.nio.channels.SocketChannel: int read(java.nio.ByteBuffer)>($r7);",sources);
//			 System.out.println("class1="+class1);
//			 String method1=getMethodItem(classMethods, class1,"i0 = virtualinvoke r2.<java.nio.channels.SocketChannel: int read(java.nio.ByteBuffer)>($r7);", " | "); //getMethods(classMethods, class1);
//			 System.out.println("method1="+method1);
			 //			 String midStr="java.io.PrintStream";  readToString(String fileName)
//			 String lineStr="$r1 = <java.lang.System: java.io.PrintStream out>";
//			 System.out.println(" midStr="+midStr+" lineStr="+lineStr+" lineStr.indexOf(midStr)="+lineStr.indexOf(midStr));
			
			 //System.out.println(itemInList("$r1 = <java.lang.System: java.io.PrintStream out>",sinks));getStmtFromLine(String lineStr)
//		 System.out.println(" getStmtFromLine="+getStmtFromLine("Sink: <NioClient: void run()> - virtualinvoke r2.<java.nio.channels.SelectionKey: java.nio.channels.SelectionKey interestOps(int)>($i2) :(java.nio.channels.SelectionKey)"));
//		 System.out.println(" getMethodFromLine="+getMethodFromLine("Sink: <NioClient: void run()> - virtualinvoke r2.<java.nio.channels.SelectionKey: java.nio.channels.SelectionKey interestOps(int)>($i2) :(java.nio.channels.SelectionKey)"));
//		 System.out.println(" getClassFromLine="+getClassFromLine("Sink: <NioClient: void run()> - virtualinvoke r2.<java.nio.channels.SelectionKey: java.nio.channels.SelectionKey interestOps(int)>($i2) :(java.nio.channels.SelectionKey)"));
//		 System.out.println(" getClassFromMethod="+getClassFromMethod(getMethodFromLine("Sink: <NioClient: void run()> - virtualinvoke r2.<java.nio.channels.SelectionKey: java.nio.channels.SelectionKey interestOps(int)>($i2) :(java.nio.channels.SelectionKey)")));
//		 System.out.println(" getClassFromMethod="+getClassFromMethod(getStmtFromLine("Sink: <NioClient: void run()> - virtualinvoke r2.<java.nio.channels.SelectionKey: java.nio.channels.SelectionKey interestOps(int)>($i2) :(java.nio.channels.SelectionKey)")));
		 //System.out.println(" readToString(String fileName)="+readToString("C:/temp/sourceSinkMethodPair.txt"));
	 }
	 
//	public static void readXML(String dataPath) {
//		   try {   
//
//			     File f = new File(dataPath+"/configuration.xml");   
//			     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
//			     DocumentBuilder builder = factory.newDocumentBuilder();   
//			     Document doc = builder.parse(f);   
//			     NodeList nl = doc.getElementsByTagName("sources");   
//			    for (int i = 0; i < nl.getLength(); i++) {   
//			      if (doc.getElementsByTagName("source1")!=null)	
//			    	  System.out.println("source1: "+ doc.getElementsByTagName("source1").item(i).getFirstChild().getNodeValue());  
//			      if (doc.getElementsByTagName("source2")!=null)	
//			    	  System.out.println("source2: "+ doc.getElementsByTagName("source2").item(i).getFirstChild().getNodeValue());   
//			      if (doc.getElementsByTagName("source3")!=null)	
//			    	  System.out.println("source3: "+ doc.getElementsByTagName("source3").item(i).getFirstChild().getNodeValue());  
//			      if (doc.getElementsByTagName("source4")!=null)	
//			    	  System.out.println("source4: "+ doc.getElementsByTagName("source4").item(i).getFirstChild().getNodeValue());   
//			     }   
//			    System.out.println("");
//			    nl = doc.getElementsByTagName("sinks");   
//			    for (int i = 0; i < nl.getLength(); i++) {   
//			      System.out.println("sink1: "+ doc.getElementsByTagName("sink1").item(i).getFirstChild().getNodeValue()); 
//			      System.out.println("sink2: "+ doc.getElementsByTagName("sink2").item(i).getFirstChild().getNodeValue()); 
//			      System.out.println("sink3: "+ doc.getElementsByTagName("sink3").item(i).getFirstChild().getNodeValue()); 
//			      System.out.println("sink4: "+ doc.getElementsByTagName("sink4").item(i).getFirstChild().getNodeValue());
//			     }   
//			    } catch (Exception e) {   
//			     e.printStackTrace();   
//			   }   
//	}	   
	
	public static HashMap getSourceMap(String dataPath) {
	    String source1="";  
	    String source2="";  
	    String source3="";  
	    String source4="";
	    	HashMap<String,String> totalSources = new HashMap<>(); 
		   try {   
			     File f = new File(dataPath+"/configuration.xml");   
			     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			     DocumentBuilder builder = factory.newDocumentBuilder();   
			     Document doc = builder.parse(f);   
			     NodeList nl = doc.getElementsByTagName("sources");   
			    for (int i = 0; i < nl.getLength(); i++) {   			    	 
				    if (doc.getElementsByTagName("source1")!=null)
			    	if (doc.getElementsByTagName("source1").item(i)!=null)	
			    	if (doc.getElementsByTagName("source1").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("source1").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		source1=doc.getElementsByTagName("source1").item(i).getFirstChild().getNodeValue();    
				    } 			    	 
				    if (doc.getElementsByTagName("source2")!=null)
			    	if (doc.getElementsByTagName("source2").item(i)!=null)	
			    	if (doc.getElementsByTagName("source2").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("source2").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		source2=doc.getElementsByTagName("source2").item(i).getFirstChild().getNodeValue();  
				    }		    	 
				    if (doc.getElementsByTagName("source3")!=null)
			    	if (doc.getElementsByTagName("source3").item(i)!=null)	
			    	if (doc.getElementsByTagName("source3").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("source3").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		//source3=doc.getElementsByTagName("source3").item(i).getFirstChild().getNodeValue(); 
				    }
			    	 
				    if (doc.getElementsByTagName("source4")!=null)
			    	if (doc.getElementsByTagName("source4").item(i)!=null)	
			    	if (doc.getElementsByTagName("source4").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("source4").item(i).getFirstChild().getNodeValue()!=null)	
				    {
				    	source4=doc.getElementsByTagName("source4").item(i).getFirstChild().getNodeValue();     
				    }	
			     }   
			     System.out.println(" source1="+source1+" source2="+source2+" source3="+source3+" source4="+source4);
			     if (source1.length()>1)
			    	 totalSources.putAll(dtUtil.transferfunctionList(dataPath+"/"+source1+".list", ""));  
			     if (source2.length()>1)
			    	 totalSources.putAll(dtUtil.transferfunctionList(dataPath+"/"+source2+".list", ""));   
			     if (source3.length()>1)
			    	 totalSources.putAll(dtUtil.transferfunctionList(dataPath+"/"+source3+".list", ""));  
			     if (source4.length()>1)
			    	 totalSources.putAll(dtUtil.transferfunctionList(dataPath+"/"+source4+".list", ""));
			     return totalSources;
			    } catch (Exception e) {   
			     e.printStackTrace();   
			     return null;
		}   
	}	
	public static HashMap getSinkMap(String dataPath) {
	    String sink1="";  
	    String sink2="";  
	    String sink3="";  
	    String sink4="";	
	    	HashMap<String,String> totalSinks = new HashMap<>(); 
		   try {   
			     File f = new File(dataPath+"/configuration.xml");   
			     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			     DocumentBuilder builder = factory.newDocumentBuilder();   
			     Document doc = builder.parse(f);   
			     NodeList nl = doc.getElementsByTagName("sinks");   
			    for (int i = 0; i < nl.getLength(); i++) {   			    	 
				    if (doc.getElementsByTagName("sink1")!=null)
			    	if (doc.getElementsByTagName("sink1").item(i)!=null)	
			    	if (doc.getElementsByTagName("sink1").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("sink1").item(i).getFirstChild().getNodeValue()!=null)	
				    {
				    	sink1=doc.getElementsByTagName("sink1").item(i).getFirstChild().getNodeValue();    
				    }	  			    	 
				    if (doc.getElementsByTagName("sink2")!=null)
			    	if (doc.getElementsByTagName("sink2").item(i)!=null)	
			    	if (doc.getElementsByTagName("sink2").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("sink2").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		sink2=doc.getElementsByTagName("sink2").item(i).getFirstChild().getNodeValue();  
				    }			    	 
				    if (doc.getElementsByTagName("sink3")!=null)
			    	if (doc.getElementsByTagName("sink3").item(i)!=null)	
			    	if (doc.getElementsByTagName("sink3").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("sink3").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		sink3=doc.getElementsByTagName("sink3").item(i).getFirstChild().getNodeValue();    
				    }		    	 
				    if (doc.getElementsByTagName("sink4")!=null)
			    	if (doc.getElementsByTagName("sink4").item(i)!=null)	
			    	if (doc.getElementsByTagName("sink4").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("sink4").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		sink4=doc.getElementsByTagName("sink4").item(i).getFirstChild().getNodeValue();      
				    }
			     }   
			     //System.out.println(" sink1="+sink1+" sink2="+sink2+" sink3="+sink3+" sink4="+sink4);
			     
			     if (sink1.length()>1)
			    	 totalSinks.putAll(dtUtil.transferfunctionList(dataPath+"/"+sink1+".list", ""));  
			     if (sink2.length()>1)
			    	 totalSinks.putAll(dtUtil.transferfunctionList(dataPath+"/"+sink2+".list", ""));   
			     if (sink3.length()>1)
			    	 totalSinks.putAll(dtUtil.transferfunctionList(dataPath+"/"+sink3+".list", ""));  
			     if (sink4.length()>1)
			    	 totalSinks.putAll(dtUtil.transferfunctionList(dataPath+"/"+sink4+".list", ""));
			     return totalSinks;
			    } catch (Exception e) {   
			     e.printStackTrace();   
			     return null;
		}   
	}	
	
	public static HashSet getSourceSet(String dataPath) {
	    String source1="";  
	    String source2="";  
	    String source3="";  
	    String source4="";
	    	HashSet<String> totalSources = new HashSet<>(); 
		   try {   
			     File f = new File(dataPath+"/configuration.xml");   
			     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			     DocumentBuilder builder = factory.newDocumentBuilder();   
			     Document doc = builder.parse(f);   
			     NodeList nl = doc.getElementsByTagName("sources");   
			    for (int i = 0; i < nl.getLength(); i++) {   			    	 
				    if (doc.getElementsByTagName("source1")!=null)
			    	if (doc.getElementsByTagName("source1").item(i)!=null)	
			    	if (doc.getElementsByTagName("source1").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("source1").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		source1=doc.getElementsByTagName("source1").item(i).getFirstChild().getNodeValue();    
				    } 			    	 
				    if (doc.getElementsByTagName("source2")!=null)
			    	if (doc.getElementsByTagName("source2").item(i)!=null)	
			    	if (doc.getElementsByTagName("source2").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("source2").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		source2=doc.getElementsByTagName("source2").item(i).getFirstChild().getNodeValue();  
				    }		    	 
				    if (doc.getElementsByTagName("source3")!=null)
			    	if (doc.getElementsByTagName("source3").item(i)!=null)	
			    	if (doc.getElementsByTagName("source3").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("source3").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		source3=doc.getElementsByTagName("source3").item(i).getFirstChild().getNodeValue(); 
				    }
			    	 
				    if (doc.getElementsByTagName("source4")!=null)
			    	if (doc.getElementsByTagName("source4").item(i)!=null)	
			    	if (doc.getElementsByTagName("source4").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("source4").item(i).getFirstChild().getNodeValue()!=null)	
				    {
				    	source4=doc.getElementsByTagName("source4").item(i).getFirstChild().getNodeValue();     
				    }	
			     }   
			     //System.out.println(" source1="+source1+" source2="+source2+" source3="+source3+" source4="+source4);
			     if (source1.length()>1)
			    	 totalSources.addAll(dtUtil.getListSet(dataPath+"/"+source1+".list"));
			     if (source2.length()>1)
			    	 totalSources.addAll(dtUtil.getListSet(dataPath+"/"+source2+".list"));   
			     if (source3.length()>1)
			    	 totalSources.addAll(dtUtil.getListSet(dataPath+"/"+source3+".list"));  
			     if (source4.length()>1)
			    	 totalSources.addAll(dtUtil.getListSet(dataPath+"/"+source4+".list"));
			     return totalSources;
			    } catch (Exception e) {   
			     e.printStackTrace();   
			     return null;
		}   
	}	
	public static HashSet getSinkSet(String dataPath) {
	    String sink1="";  
	    String sink2="";  
	    String sink3="";  
	    String sink4="";	
	    	HashSet<String> totalSinks = new HashSet<>(); 
		   try {   
			     File f = new File(dataPath+"/configuration.xml");   
			     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			     DocumentBuilder builder = factory.newDocumentBuilder();   
			     Document doc = builder.parse(f);   
			     NodeList nl = doc.getElementsByTagName("sinks");   
			    for (int i = 0; i < nl.getLength(); i++) {   			    	 
				    if (doc.getElementsByTagName("sink1")!=null)
			    	if (doc.getElementsByTagName("sink1").item(i)!=null)	
			    	if (doc.getElementsByTagName("sink1").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("sink1").item(i).getFirstChild().getNodeValue()!=null)	
				    {
				    	sink1=doc.getElementsByTagName("sink1").item(i).getFirstChild().getNodeValue();    
				    }	  			    	 
				    if (doc.getElementsByTagName("sink2")!=null)
			    	if (doc.getElementsByTagName("sink2").item(i)!=null)	
			    	if (doc.getElementsByTagName("sink2").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("sink2").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		sink2=doc.getElementsByTagName("sink2").item(i).getFirstChild().getNodeValue();  
				    }			    	 
				    if (doc.getElementsByTagName("sink3")!=null)
			    	if (doc.getElementsByTagName("sink3").item(i)!=null)	
			    	if (doc.getElementsByTagName("sink3").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("sink3").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		sink3=doc.getElementsByTagName("sink3").item(i).getFirstChild().getNodeValue();    
				    }		    	 
				    if (doc.getElementsByTagName("sink4")!=null)
			    	if (doc.getElementsByTagName("sink4").item(i)!=null)	
			    	if (doc.getElementsByTagName("sink4").item(i).getFirstChild()!=null)		
			    	if (doc.getElementsByTagName("sink4").item(i).getFirstChild().getNodeValue()!=null)	
				    {
			    		sink4=doc.getElementsByTagName("sink4").item(i).getFirstChild().getNodeValue();      
				    }
			     }   
			     //System.out.println(" sink1="+sink1+" sink2="+sink2+" sink3="+sink3+" sink4="+sink4);
			     
			     if (sink1.length()>1)
			    	 totalSinks.addAll(dtUtil.getListSet(dataPath+"/"+sink1+".list"));  
			     if (sink2.length()>1)
			    	 totalSinks.addAll(dtUtil.getListSet(dataPath+"/"+sink2+".list"));   
			     if (sink3.length()>1)
			    	 totalSinks.addAll(dtUtil.getListSet(dataPath+"/"+sink3+".list"));  
			     if (sink4.length()>1)
			    	 totalSinks.addAll(dtUtil.getListSet(dataPath+"/"+sink4+".list"));
			     return totalSinks;
			    } catch (Exception e) {   
			     e.printStackTrace();   
			     return null;
		}   
	}	
	
	public static String itemInList(String lineStr,HashSet items)
	{
		try {  
			String midStr="";
			//System.out.println(" items="+items);
	        Iterator iterator = items.iterator();  
	        while (iterator.hasNext()) {  
	        	midStr=iterator.next().toString();   
	        	//System.out.println(" midStr="+midStr+" lineStr="+lineStr+" lineStr.indexOf(midStr)="+lineStr.indexOf(midStr));
	        	if (lineStr.indexOf(midStr)>=0)
	        	{
	        		return midStr;
	        	}
	        }   
			return "";
        } catch (Exception e) {  
            e.printStackTrace();  
            return "";
        }  
	}
	

	public static HashSet getFirstHashSet(String listFile, String separation) {
		HashSet  resultS = new HashSet(); 
        FileReader reader = null;  
        BufferedReader br = null;     
        try {  
   
            reader = new FileReader(listFile);  
            br = new BufferedReader(reader);              
            String str = null;  
            //int count=0;
            while ((str = br.readLine()) != null) {  
            	//System.out.println("str="+str);
            	//if (!str.equalsIgnoreCase("org.jboss.netty.channel.ChannelPipelineFactory	getPipeline()"))  {continue;}
            	String strs[]=str.split(separation);
            	if (strs.length<1)
            	{
            		resultS.add(str);            		
            	}
            	else
            	{
            		resultS.add(strs[0].trim());
            	}
            }     
            br.close();  
            reader.close();     
            return resultS;   
        } catch (Exception e) {  
            e.printStackTrace();   
            return resultS;    
        }  
	}
	
	public static HashSet getSecondHashSet(String listFile, String separation, String method) {
		HashSet  resultS = new HashSet(); 
        FileReader reader = null;  
        BufferedReader br = null;     
        try {  
   
            reader = new FileReader(listFile);  
            br = new BufferedReader(reader);              
            String str = null;  
            //int count=0;
            while ((str = br.readLine()) != null) {  
            	//System.out.println("str="+str);
            	String strs[]=str.split(separation);
            	if (strs.length<1)
            	{
            		continue;            		
            	}
            	
            	if (strs[0].trim().equalsIgnoreCase(method))
            	{
            		resultS.add(strs[1].trim());
            	}
            	if (resultS.size()>0)
            		break;
            }     
            br.close();  
            reader.close();     
            return resultS;   
        } catch (Exception e) {  
            e.printStackTrace();   
            return resultS;    
        }  
	}
	
	public static LinkedHashMap<String, String> getClassesMethods(String listFile, String separation) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		   //map.put("keyValue", list);
        FileReader reader = null;  
        BufferedReader br = null;  
        String oldClass="";
        String newClass="";
        String methodName="";
        try {  
        	String methods = "";
            reader = new FileReader(listFile);  
            br = new BufferedReader(reader);              
            String str = null;  
            //int count=0;
            while ((str = br.readLine()) != null) {  
            	//System.out.println("str="+str);
            	String strs[]=str.split(separation);
            	if (strs.length<=1)
            		continue;
            	newClass=strs[0].trim();
            	
            	if (!newClass.equals(oldClass))  {
            		if (oldClass.length()>0 && methods.length()>0)  {
            			map.put(oldClass, methods);
            			//System.out.println("oldClass="+oldClass+" newClass="+newClass+" methodName="+methodName+" methods="+methods+" map="+map);
            		}	
            		methods="";
            		oldClass=newClass;
            	}	
            	methodName=strs[1].trim();
            	methods+=methodName+" | ";
            	
            }     
            map.put(newClass, methods);
            br.close();  
            reader.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return map;
	}
	
	public static String getMethods(LinkedHashMap<String, String> classMethods, String theClasses) {
 
        try {  
        	 for(Map.Entry<String, String> entry : classMethods.entrySet())
        		 if (entry.getKey().equalsIgnoreCase(theClasses))
 	        		return entry.getValue().toString().trim();   
            return "";   
        } catch (Exception e) {  
            e.printStackTrace();   
            return "";    
        }  
	}
	
	public static String getMethodItem(LinkedHashMap<String, String> classMethods, String theClasses, String lineStr, String separation) {
		if  (theClasses.length()<1 || lineStr.length()<1 || separation.length()<1)
			return "";
		String resultS="";
        try {  
        	String methods=getMethods(classMethods, theClasses);
        	//System.out.println(" theClasses="+theClasses+" lineStr="+lineStr+" separation="+separation+" methods="+methods);
        	String strs[]=methods.split(separation);
        	for (int i=0; i<strs.length;i++)  {        		
        		//System.out.println("strs["+i+"]="+strs[i]+" indexOf="+lineStr.indexOf(strs[i].trim()));
        		if (lineStr.indexOf(strs[i].trim())>=0)  {
        			resultS=strs[i].trim();
        			//System.out.println("resultS="+resultS);
        			break;
        		}	
        	}	   
        } catch (Exception e) {  
            e.printStackTrace();   
        }  
        return resultS;
	}	

	public static String getStmtFromLine(String lineStr) {
		 
        try {  
        	String strs[]=lineStr.split(":\\(");
        	if (strs.length<1)  {
        		return lineStr;
        	}
        	else
        		return strs[0].replace("Sink: ", "").replace("Source: ", "").trim();
        	
        } catch (Exception e) {  
            e.printStackTrace();   
            return "";    
        }  
	}
	
	public static String getMethodFromLine(String lineStr) {
		//String stmtStr= getStmtFromLine(lineStr);
        try {  
        	String strs[]=lineStr.split("> - ");
        	if (strs.length<1)  {
        		return lineStr;
        	}
        	else
        		return strs[0].replace("Sink: ", "").replace("Source: ", "").trim()+">";
        	
        } catch (Exception e) {  
            e.printStackTrace();   
            return "";    
        }  
	}
	
	public static String getClassFromMethod(String methodStr) {
		
        try {  
        	String strs[]=methodStr.split(": ");
        	if (strs.length<1)  {
        		return methodStr;
        	}
        	else
        		return strs[0].trim()+">";
        	
        } catch (Exception e) {  
            e.printStackTrace();   
            return "";    
        }  
	}
	
	public static String getClassFromLine(String lineStr) {
		String methodStr= getMethodFromLine(lineStr);
        try {  
        	String strs[]=methodStr.split(": ");
        	if (strs.length<1)  {
        		return methodStr;
        	}
        	else
        		return strs[0].trim()+">";
        	
        } catch (Exception e) {  
            e.printStackTrace();   
            return "";    
        }  
	}
	
	public static void writeMessage(String fileName, String message) {
		try {
	        FileWriter fw = new FileWriter(fileName, true);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.append(message);
	        bw.close();
	        fw.close();
		}
        catch (Exception e) {
        	System.out.println("Cannot write message to" + fileName );
			e.printStackTrace();
		}
	}
	
	public static void writeSet(String fileName, Set<String> myset) {
		try {
	        FileWriter fw = new FileWriter(fileName, true);
	        BufferedWriter bw = new BufferedWriter(fw);
	        for(String m:myset)
			{
	        	bw.write(m+"\n");
			}
	        bw.close();
	        fw.close();
		}
        catch (Exception e) {
        	System.out.println("Cannot write message to" + fileName );
			e.printStackTrace();
		}
	}
	
	public static void caetesianTwoArraysToFile(ArrayList al1, ArrayList al2, String fileName,String type) {
		String allMessages="";
		
		try {
			String midStr1="";
			String midStr2="";		
			String class1="";
			String class2="";	
			String oldFileMsg="";		
			if (al1.size()<1 || al2.size()<1)
				return;
			for(int i=0; i<al1.size();i++)  {
				midStr1=al1.get(i).toString().trim();
				//System.out.println(" midStr1=" + midStr1 );
				for(int j=0; j<al2.size();j++)  {
					midStr2=al2.get(j).toString().trim();	
					//System.out.println(" midStr2=" + midStr2 );
					if (!midStr1.equalsIgnoreCase(midStr2)) // &&  (midStr1+midStr2).length()>1)
					{
						if (type.indexOf("Diff")>=0)
						{
							if (type.indexOf("method")>=0)  {
								class1=dtConfig.getClassFromMethod(midStr1);
								class2=dtConfig.getClassFromMethod(midStr2);
							}
							else {
								class1=dtConfig.getClassFromLine(midStr1);
								class2=dtConfig.getClassFromLine(midStr2);
							}
							//System.out.println(" class1=" + class1+" class2=" + class2 + " class1.equalsIgnoreCase(class2)="+class1.equalsIgnoreCase(class2));
							if (!class1.equalsIgnoreCase(class2) && allMessages.indexOf(midStr1+"; "+midStr2+"\n")<0)
								allMessages+=midStr1+"; "+midStr2+"\n";
						}
						else if (allMessages.indexOf(midStr1+"; "+midStr2+"\n")<0)
							allMessages+=midStr1+"; "+midStr2+"\n";
					}
				}
			}	
		}
        catch (Exception e) {
        	
			e.printStackTrace();
		}
		writeMessage(fileName, allMessages);
	}
	
	public static void partCaetesianTwoArraysToFile(ArrayList al1, ArrayList al2, String fileName,String type) {
		String allMessages="";
		
		try {
			String midStr1="";
			String midStr2="";		
			String class1="";
			String class2="";	
			String oldFileMsg="";		
			if (al1.size()<1 || al2.size()<1)
				return;
			for(int i=0; i<al1.size();i++)   
			{
				
				midStr1=al1.get(i).toString().trim();
				//System.out.println(" midStr1=" + midStr1 );
				//for(int j=0; j<al2.size();j++)  
				{
					Random random = new Random ();
					int j=random.nextInt(al2.size()-1);
					midStr2=al2.get(j).toString().trim();	
					//System.out.println(" midStr2=" + midStr2 );
					if (!midStr1.equalsIgnoreCase(midStr2)) // &&  (midStr1+midStr2).length()>1)
					{
						if (type.indexOf("Diff")>=0)
						{
							if (type.indexOf("method")>=0)  {
								class1=dtConfig.getClassFromMethod(midStr1);
								class2=dtConfig.getClassFromMethod(midStr2);
							}
							else {
								class1=dtConfig.getClassFromLine(midStr1);
								class2=dtConfig.getClassFromLine(midStr2);
							}
							//System.out.println(" class1=" + class1+" class2=" + class2 + " class1.equalsIgnoreCase(class2)="+class1.equalsIgnoreCase(class2));
							if (!class1.equalsIgnoreCase(class2) && allMessages.indexOf(midStr1+"; "+midStr2+"\n")<0)
								allMessages+=midStr1+"; "+midStr2+"\n";
						}
						else if (allMessages.indexOf(midStr1+"; "+midStr2+"\n")<0)
							allMessages+=midStr1+"; "+midStr2+"\n";
					}
				}
			}	
		}
        catch (Exception e) {
        	
			e.printStackTrace();
		}
		writeMessage(fileName, allMessages);
	}

}
