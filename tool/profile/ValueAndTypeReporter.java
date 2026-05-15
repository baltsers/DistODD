package profile;

public class ValueAndTypeReporter {
	public static void __link() { CommonReporter.__link(); }
	
	private static boolean active = false;
		
	public static void reportValueAndType(int sId, boolean pos, java.lang.Object oVal, java.lang.String typename){
		if(active)
			return;
		active = true;
		
		try{
			reportVal_IMPL(sId, pos, oVal, typename, "");
		}
		catch (Exception e){
			reportVal_IMPL(sId, pos, oVal, typename, " Expected Exception");
		}
		finally{
			active = false;
		}
	}
	
	private static void reportVal_IMPL(int sId, boolean pos, Object oVal, String typename, String extraNote){
		if(oVal == null){
			System.out.println("oVal is null");
		}
		
		if(typename == null){
			typename = "type name is null.";
		}
		
		String ovalString;
		if(oVal == null){
			ovalString = "oVal is null.";
		}
		else{
			try{
				ovalString = oVal.toString();
			}
			catch (Exception e){
				ovalString = "oVal.toString has exception.";
			}
		}

		if(ovalString==null){
			ovalString = "ovalString is null.";
		}
		System.out.println("\nValueReporter:" + 
			sId + "[" + (pos? 1: 0) + "]=" + (ovalString.replace('\n', '|')) + extraNote);
		System.out.println("TypeReporter:" +  typename);
		
	}
}
