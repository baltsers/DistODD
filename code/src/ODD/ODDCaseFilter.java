package ODD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ODDCaseFilter {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		HashSet<String> diffMethodSet = ODDUtil.getListSet("/Research/z3411/DP/queryDiffMethodSet.txt");
//		System.out.println("diffMethodSet="+diffMethodSet);
//		boolean isCase=isTheCase(diffMethodSet, "/Research/z3411/test1/1/");
//		System.out.println("isCase="+isCase);
//		ArrayList allCases=new ArrayList();
//		allCases=getArrayList("/Research/z3411/test1/",diffMethodSet);
//		System.out.println("allCases="+allCases);
		
		
		ArrayList allCases=new ArrayList();
		int argslength=args.length;
		switch (argslength) {
			case 0:
				//allCases=getArrayList("/Research/z3411/test1/",diffMethodSet);
				//allCases=getFilteredCases("queryDiffMethodSet.txt", "test1/clientlog","diff0Available.txt");
				allCases=getFilteredCases("/Research/z3411/queryDiffMethodSet.txt", "/Research/z3411/test1_load/clientlog","/Research/z3411/diff0Available_load.txt");
				//allCases=getFilteredCases("/Research/z3411/queryDiffMethodSet.txt", "/Research/z3411/test1/clientlog");
				//allCases=getFilteredCases("/Research/Thrift/queryDiffMethodSet.txt", "/Research/Thrift/test1/clientlog","/Research/Thrift/diff0Available.txt");
				//allCases=getFilteredCases("/Research/xSocket/queryDiffMethodSet.txt", "/Research/xSocket/test1/clientlog","/Research/xSocket/diff0Available.txt");
				break;
			case 1:
				allCases=getFilteredCases(args[0], "");
				break;
			case 2:
				allCases=getFilteredCases(args[0], args[1]);
				break;
		}
		System.out.println("allCases.size()="+allCases.size());
//		Collections.sort(allCases);
//		for (int i=0; i<allCases.size(); i++) {
//			System.out.println(allCases.get(i));
//		}
	}
	
	
	public static boolean isTheCase(HashSet<String> diffSet, String casePath) 
	{
		boolean rb=false;
        FileReader reader = null;  
        BufferedReader br = null;     
        try {  
            File file = new File(casePath+"FL.txt");  
            if (!file.exists()) {  
               return false;                
            }  
            reader = new FileReader(casePath+"FL.txt");  
            br = new BufferedReader(reader);              
            String str = null;  
            while ((str = br.readLine()) != null) {  
            	//System.out.println("str="+str);
            	if (str.length()>1)
            	{
            		String str2=str.replace("\n", "").replace("\n", "").replace(":f", "").replace(":l", "").trim();
            		if  (diffSet.contains(str2)) {
            			System.out.println("str2="+str2);
            			return true;
            		}
            	}
            }     
            br.close();  
            reader.close(); 
        } catch (Exception e) {  
            e.printStackTrace();   
               
        }  		
		return rb;
	}
	
	
	public static ArrayList getArrayList(String listPath, HashSet<String> diffSet) {
		ArrayList  resultA = new ArrayList(); 
        File realPath = new File(listPath);
        if (realPath.isDirectory()) {
            File[] subfiles = realPath.listFiles();
            for (File sub : subfiles) {
            	System.out.println("sub="+sub+" sub.isDirectory()="+sub.isDirectory());
            	if (sub.isDirectory() && isTheCase(diffSet, sub.toString()+"/")) 
            		resultA.add(sub.toString());
            }
        }    		
       
        return resultA; 
	}
	public static ArrayList getArrayList(String listPath, HashSet<String> diffSet, HashSet<String> caseSet) {
		if (caseSet.size()<1)
			return getArrayList(listPath, diffSet);
		ArrayList  resultA = new ArrayList(); 
        File realPath = new File(listPath);
        if (realPath.isDirectory()) {
            File[] subfiles = realPath.listFiles();
            for (File sub : subfiles) {
            	
            	String subLast=ODDUtil.theLastOfPath(sub.toString());
            	
            	if (sub.isDirectory() && (caseSet.contains(subLast) ||  caseSet.contains(subLast.replace("_", "-"))))  {
            		 System.out.println("sub="+sub+"  "+sub.isDirectory()+" "+(caseSet.contains(subLast) ||  caseSet.contains(subLast.replace("_", "-")))+" subLast="+subLast);
            		if (isTheCase(diffSet, sub.toString()+"/")) {
            			resultA.add(sub.toString());
            			System.out.println("resultA.size()="+resultA.size());	
            		}
//            		else
//            			System.out.println("sub="+sub+"  "+isTheCase(diffSet, sub.toString()+"/")+" subLast="+subLast);
            	}	
            }
        }    		
       
        return resultA; 
	}
	public static ArrayList getFilteredCases(String diffSetFile, String listPath) {
		//ArrayList  resultA = new ArrayList();
		
		HashSet<String> diffMethodSet = ODDUtil.getListSet(diffSetFile);
		//System.out.println("diffMethodSet.size()="+diffMethodSet.size());
		//boolean isCase=isTheCase(diffMethodSet, "/Research/z3411/test1/1/");
		//System.out.println("isCase="+isCase);
		ArrayList allCases=getArrayList(listPath,diffMethodSet);
		//System.out.println("allCases="+allCases);	
       
        return allCases; 
	}
	public static ArrayList getFilteredCases(String diffSetFile, String listPath, String diffCaseFile) {
		//ArrayList  resultA = new ArrayList();
		   File file = new File(diffCaseFile);  
           if (!file.exists()) {  
              return getFilteredCases(diffSetFile, listPath);                
           }  
		
		HashSet<String> diffMethodSet = ODDUtil.getListSet(diffSetFile);
		//System.out.println("diffMethodSet.size()="+diffMethodSet.size());
		//boolean isCase=isTheCase(diffMethodSet, "/Research/z3411/test1/1/");
		//System.out.println("diffCaseFile="+diffCaseFile);
		HashSet<String> diffCaseSet= ODDUtil.getListSet(diffCaseFile);
		//System.out.println("diffCaseSet.size()="+diffCaseSet.size()+"  diffCaseSet="+diffCaseSet);
		ArrayList allCases=getArrayList(listPath,diffMethodSet,diffCaseSet);
		//System.out.println("allCases="+allCases);	
       
        return allCases; 
	}
}
