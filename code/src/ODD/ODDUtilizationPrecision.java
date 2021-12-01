package ODD;

public class ODDUtilizationPrecision {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ODDUtil.logUtilizationPrecision("/Research/Netty/DP/",2);
		if (args.length <=0 ) {
			ODDUtil.logUtilizationPrecision("",2);
		}
		else {
			String arg0= args[0];
			int componentCount=2;
			try {
				componentCount=Integer.valueOf(arg0);
			
			} catch (Exception e) {  
            //e.printStackTrace(); 

          }    
			ODDUtil.logUtilizationPrecision("",componentCount);
		}
	}

}
