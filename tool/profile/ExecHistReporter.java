package profile;

import java.io.PrintStream;

public class ExecHistReporter {
	public static void __link() {}
	
//	private static BufferedWriter writer = null;
//	static {
//		File fOut = new File("exechist.out");
//		try { writer = new BufferedWriter(new FileWriter(fOut)); } catch (IOException e) { e.printStackTrace(); }
//	}
//	
//	private static ExecHistReporter inst = new ExecHistReporter();
//	protected void finalize() {
//		try {
//			writer.flush();
//			writer.close();
//		} catch (IOException e) { e.printStackTrace(); }
//	}
	
	/** Used to avoid infinite recursion due to call to oVal.toString() */
	private static boolean active = false;
	/*
	public static void reportDef(int sId, boolean pos, Object oVal) {
		if (active)
			return;
		active = true;
		
		System.out.println("|||" + sId + "[" + (pos? 1 : 0) + "]=" + (oVal == null? null : oVal.toString().replace('\n', '|')));
		
		active = false;
		
//		try { writer.write(sId + "[" + (pre? 0 : 1) + "]=" + oVal + "\n"); }
//		catch (IOException e) { e.printStackTrace(); }
	}
	*/
	/** hcai: let applications to set the output stream of EH records because for some of them can get interfered by
	 * the dumping of EH records to the standard output (Such as Ant EchoTest cases)
	 */
	public static PrintStream outStream = System.out;
	
	public static void reportDef(int sId, boolean pos, Object oVal) {
		if (active)
			return;
		active = true;
		
		try {
			reportDef_IMPL(sId, pos, oVal);
		}
		catch (Exception e) {
			//e.printStackTrace();
			outStream.println("|||" + sId + "[" + (pos? 1 : 0) + "]=" + oVal.getClass().toString() + " Expected Exception");
		}
		finally {
			active = false;
		}
	}
	
	private static void reportDef_IMPL(int sId, boolean pos, Object oVal) {
		//outStream.println("|||" + sId + "[" + (pos? 1 : 0) + "]=" + (oVal == null? null : oVal.toString().replace('\n', '|')));
		
		// hcai: remove most of the addresses in default toString() output
		String oStr="";
		if (oVal != null) {
			String hc =  "@" + Integer.toHexString(oVal.hashCode());
			oStr = oVal.toString().replaceAll(hc, "");
		}
		outStream.println("|||" + sId + "[" + (pos? 1 : 0) + "]=" + (oVal == null? null : oStr.replace('\n', '|')));
		/*
		String info = (oVal == null? null : oVal.toString());
		if (info.contains("\t")) {
			
		}
		System.out.println("|||" + sId + "[" + (pos? 1 : 0) + "]=" + (oVal == null? null : oVal.toString()));
		*/
	}
}
