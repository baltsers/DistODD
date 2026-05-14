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
	public static void main(String []args) {
		//saveBudget("main() ; 2100");
		String s1=getFirstStr("main() ; 2100", " ; ");
		System.out.println("s1=" + s1);
		
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
	
}
