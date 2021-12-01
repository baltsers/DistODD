package ODD;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class ODDGetMsgResult {
	static String allResult="";
	protected static Map< String, Integer > method2idx;
	protected static Map< Integer, String > idx2method;
    public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		Process process=Runtime.getRuntime().exec("./RMallResult.sh");	
		process.waitFor();	
		initializeFunctionList();
        Scanner sc = new Scanner(System.in); 
        System.out.println("Please input method:"); 
        String query = sc.nextLine();
        handleQuery(query);
		String tmpStr=ODDUtil.readToString("allResult.txt");
		System.out.println(tmpStr);
		//System.out.println("Query took "+(System.currentTimeMillis() - startTime)+" ms");
    }

    public static void handleQuery(String query) {
    	HashSet<String> onePortImpactSet = new HashSet<>();
        onePortImpactSet=ODDToolUtil.getQuerySetFromDir(method2idx,idx2method, query);   //ODDUtil.getSetFromImpactsetStr(resultS, ";");		

        	allResult += ODDToolUtil.getQueryImpactSets(onePortImpactSet, query); //resultS+"\n";		    		
		//System.out.println("Query="+query+" resultS: "+resultS+" onePortImpactSet "+onePortImpactSet+" allResult="+allResult+" resultMap="+resultMap);      

	//System.out.println("Querys allResults:\n"+allResult+" resultMap="+resultMap); 
	     ODDUtil.writeStringToFile(allResult, "allResult.txt");
    }
    
   	public static int initializeFunctionList() {
   	       try {
   	   		FileReader reader = null;      
   	        BufferedReader br = null;    
   	        reader = new FileReader("functionList.out");   
   	        br = new BufferedReader(reader);
   	        String str = "";  
   	        String mname="";
   	        int index = 0;
   	        method2idx=new HashMap< String, Integer >();
   	        idx2method=new HashMap< Integer, String >();
   	        while((str = br.readLine()) != null)
   	        {  
   	        	mname=str.trim();
   				if (!method2idx.containsKey(mname)) {
   					method2idx.put(mname, index);
   					idx2method.put(index, mname);
   					index ++;
   				}
   	        }        

   	       } catch (Exception e) {  
   	           e.printStackTrace(); 
   	           return -1;
   	       } 
   		return 0;
   	}

}
