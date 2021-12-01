package profile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;

public class CommonReporter {
	
	/** Indicates whether to report monitoring info, which depends on a property value NOT being provided by invoking process. Default: true. */
	public final static boolean outputEnabled;
	/** Whether to initialize / clean up monitoring data before/after each test run. Default: true. */
	public final static boolean cleanupPerTest;
	/** Whether to report to matrix output files. Default: false. */
	public final static boolean matrixReport;
	/** Whether to report test name. Default: true. */
	public final static boolean reportTestName;
	static {
		String propVal = System.getProperty("duaf.monitor");
		outputEnabled = !(propVal != null && (propVal.equals("no") || propVal.equals("off") || propVal.equals("false")));
		
		propVal = System.getProperty("duaf.cleanup");
		cleanupPerTest = !(propVal != null && (propVal.equals("no") || propVal.equals("off") || propVal.equals("false")));
		
		propVal = System.getProperty("duaf.matrixreport");
		matrixReport = (propVal != null && (propVal.equals("yes") || propVal.equals("on") || propVal.equals("true")));
		
		propVal = System.getProperty("duaf.reporttestname");
		reportTestName = (propVal != null && (propVal.equals("no") || propVal.equals("off") || propVal.equals("false")));
	}
	
	/** Used to allow Soot find this class by having subject link directly or indirectly to this class through a call to this method. */
	public static void __link() {}
	
	public static void reportTestName(String name) {
		if (outputEnabled)
			System.out.println("Test: " + name);
	}
	
	/** Appends new coverage (or count) row to entity coverage matrix output file */
	public static void reportToCovMatrixFile(BitSet duaCov, int numDUAs, String filenameSuffix) {
		// translate to int array
		int[] intCovArray = new int[numDUAs];
		for (int i = 0; i < numDUAs; ++i)
			intCovArray[i] = duaCov.get(i)? 1 : 0;
		
		reportToCovMatrixFile(intCovArray, filenameSuffix);
	}
	
	/** Appends new coverage row to entity coverage matrix output file */
	public static void reportToCovMatrixFile(int[] countCovArray, String filenameSuffix) {
		if (!matrixReport)
			return; // only write to this file if functionality is enabled
		try {
			// Open out file for appending
			File fCovMatrix = new File("exereport.out" + ((filenameSuffix.length() > 0)? "." + filenameSuffix : ""));
			BufferedWriter writer = new BufferedWriter(new FileWriter(fCovMatrix, true));
			
			// Write coverage or count; id is implicit in left-to-right ordering
			for (int i = 0; i < countCovArray.length; ++i) {
				writer.write(
						Integer.toString(countCovArray[i]) +
						((i < countCovArray.length - 1)? " " : "\n")); // add space after entity id, or carriage return if last entity
			}
			
			writer.flush();
			writer.close();
		}
		catch (FileNotFoundException e) { if (CommonReporter.outputEnabled) System.err.println("Couldn't write to entity coverage matrix file: " + e); }
		catch (SecurityException e) { if (CommonReporter.outputEnabled) System.err.println("Couldn't write to entity coverage matrix file: " + e); }
		catch (IOException e) { if (CommonReporter.outputEnabled) System.err.println("Couldn't write to entity coverage matrix file: " + e); }
	}
	
	/** Returns an array of the space-separated integers found in the string */
	public static int[] parseIds(String s) {
		// create array list dynamically with parsed id integer objects
		ArrayList idsList = new ArrayList();
		int start = 0;
		int end;
		while ((end = s.indexOf(' ', start)) != -1) {
			idsList.add(Integer.valueOf(s.substring(start, end)));
			start = end + 1;
		}
		if (start < s.length())
			idsList.add(Integer.valueOf(s.substring(start)));
		
		// transform to native int array
		int[] ids = new int[idsList.size()];
		int idIdx = 0;
		for (Iterator it = idsList.iterator(); it.hasNext(); ++idIdx)
			ids[idIdx] = ((Integer) it.next()).intValue();
		
		return ids;
	}
	
}
