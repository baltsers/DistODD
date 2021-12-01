package ODD;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ODDAllQuerySet {
	public static void main(String[] args) {
		int argslength=args.length;
		switch (argslength) {
			case 0:
				ODDUtil.readAndWriteQueriesSetFromToFiles("", "queryDiffAll.txt", 2,"queryDiffMethodSet.txt"); 
				break;
			case 1:
				ODDUtil.readAndWriteQueriesSetFromToFiles("", args[0], 2,"queryDiffMethodSet.txt"); 
				break;
			case 2:
				int componentCount=2;
				try {
					componentCount=Integer.valueOf(args[1]);
				
				} catch (Exception e) {  	            //e.printStackTrace(); 

				}    
				ODDUtil.readAndWriteQueriesSetFromToFiles("", args[0], componentCount,"queryDiffMethodSet.txt"); 
				break;
			case 3:
				int componentCount2=2;
				try {
					componentCount2=Integer.valueOf(args[1]);
				
				} catch (Exception e) {  	            //e.printStackTrace(); 

				}    
				ODDUtil.readAndWriteQueriesSetFromToFiles("", args[0], componentCount2,args[2]); 
				break;
		}
			
	}
	
    
}
