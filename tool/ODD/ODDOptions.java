package ODD;
import java.util.ArrayList;
import java.util.List;

import EAS.EAOptions;

public class ODDOptions  extends EAOptions {
	/* if serializing the static VTG at the end of the static analysis phase */
	protected boolean serializeVTG = false;
	/* if considering Intraprocedural CDs */
	protected boolean intraCD = false;
	/* if considering Interprocedural CDs */
	protected boolean interCD = false;
	/* if visualize the eventual VTG, namely the MDG (method dependence graph) */
	protected boolean visualizeVTG = false;
	/* safety check against the static VTG */
	protected boolean validateVTG = false;
	/* if adding exceptional interprocedural CDs due to uncaught exceptions */
	protected boolean exceptionalInterCD = false;
	/* if ignoring RunimeException exceptions when considering the exceptional interprocedural CDs due to uncaught exceptions */
	protected boolean ignoreRTECD = false;
	
	/* for dynamic alias monitoring expressly: cache until the end of execution or dump immediately */
	protected boolean cachingOIDs = false;
	
	protected boolean onlineOne = false;
	protected boolean onlineAll = false;
	public boolean runDiver = false;
	protected boolean branch = true;
	
	public final static int OPTION_NUM = EAOptions.OPTION_NUM + 11;
	private boolean sync_nio_probes = false;
	public boolean probe_sync_nio() { return sync_nio_probes; }
	private boolean per_thread_monitor = false;
	public boolean monitor_per_thread() { return per_thread_monitor; }
	private boolean use_socket = false;
	public boolean use_socket() { return use_socket; }
	private boolean use_nio = false;
	public boolean use_nio() { return use_nio; }
	
	private boolean use_objstream = false;
	public boolean use_objstream() { return use_objstream; }
	public boolean branch() { return branch; }
	
	public String[] process(String[] args) {
		args = super.process(args);
		
		List<String> argsFiltered = new ArrayList<String>();
		
		for (int i = 0; i < args.length; ++i) {
			String arg = args[i];

			if (arg.equals("-syncnio")) {
				// by default, we handle asynchronous non-blocking I/O for intercepting java NIO based network traffic, unless specified as 'sync' mode here  
				sync_nio_probes = true;
			}
			else if (arg.equals("-socket")) {
				use_socket = true;
			}
			else if (arg.equals("-objstream")) {
				use_objstream = true;
			}
			else if (arg.equals("-nio")) {
				use_nio = true;
			}
			else if (arg.equals("-perthread")) {
				// by default, we monitor inter-process message passing; when this is set, do for inter-thread message passing instead  
				per_thread_monitor = true;
			}
			else if (arg.equals("-serializeVTG")) {
				serializeVTG = true;
			}
			else if (arg.equals("-intraCD")) {
				intraCD = true;
			}
			else if (arg.equals("-interCD")) {
				interCD = true;
			}
			else if (arg.equals("-visualizeVTG")) {
				visualizeVTG = true;
			}
			else if (arg.equals("-validateVTG")) {
				validateVTG = true;
			}
			else if (arg.equals("-exInterCD")) {
				exceptionalInterCD = true;
			}
			else if (arg.equals("-ignoreRTECD")) {
				ignoreRTECD = true;
			}
			else if (arg.equals("-cachingOIDs")) {
				cachingOIDs = true;
			}
			else if (arg.equals("-onlineOne")) {
				onlineOne = true;
				onlineAll = false;
			}
			else if (arg.equals("-onlineAll")) {
				onlineOne = false;
				onlineAll = true;
			}
			else if (arg.equals("-runDiver")) {
				runDiver = true;
			}
			else if (arg.equals("-branch")) {
				branch = true;
			}
			else {
				argsFiltered.add(arg);
			}
		}
		
		String[] arrArgsFilt = new String[argsFiltered.size()];
		return (String[]) argsFiltered.toArray(arrArgsFilt);
	}
}