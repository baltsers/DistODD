package ODD;

import java.io.*;

import ODD.ODDBranchMonitor.logicClock;
//import distEA.distThreadMonitor.logicClock;

/* customized Socket InputStream where extraneous operations, for piggybacking logic clocks, are added
 */
/** for replacing socket input stream objects */
public class dtSocketInputStream extends InputStream {
	public static void __link() { }
	
	public static boolean debugOut = true, usingToken = true, intercept = true;
	
	private InputStream in;
	private logicClock lgclock;

	public dtSocketInputStream(InputStream in, logicClock clock) {
		this.in = in;
		this.lgclock = clock;
	}
	public dtSocketInputStream(InputStream in) {
		this.in = in;
		//this.lgclock = distEA.distThreadMonitor.getCreateClock();
		this.lgclock = ODDBranchMonitor.g_lgclock;
		//
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read() throws IOException {
		if (!intercept) {
			return in.read();
		}
		
		if (usingToken) {
	    	if (debugOut) {
				System.out.println("[To Read]<= " + "socket bytes to read: " + lgclock.bytesAvailableSocket);
			}
	    	if (lgclock.bytesAvailableSocket > 0) {
	    		int bytesRead = in.read();
	    		if (bytesRead == -1 || bytesRead == 0) {
	    			return bytesRead;
		  	    }
	    		lgclock.bytesAvailableSocket -= bytesRead;
	    		if (lgclock.bytesAvailableSocket < 0) lgclock.bytesAvailableSocket = 0;
	    		if (debugOut) {
					System.out.println("[Read]<= " + bytesRead + " bytes read for original message without socket token+clock piggybacked");
				}
	    		return bytesRead;
	    	}
       }
		
	   lgclock.retrieveClock(in);
	   int nb = in.read();
	   if (usingToken) {
		   lgclock.bytesAvailableSocket -= nb;
		   if (debugOut) {
			   System.out.println("[Read]<= " + nb + " bytes read for original message with socket token+clock piggybacked");
		   }
	   }
	   return nb;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (!intercept) {
			return in.read(b, off, len);
		}
		
		if (usingToken) {
	    	if (debugOut) {
				System.out.println("[To Read]<= " + "socket bytes to read: " + lgclock.bytesAvailableSocket);
			}
	    	if (lgclock.bytesAvailableSocket > 0) {
	    		int bytesRead = in.read(b, off, len);
	    		if (bytesRead == -1 || bytesRead == 0) {
	  	           return bytesRead;
	  	        }
	    		lgclock.bytesAvailableSocket -= bytesRead;
	    		//if (bytesAvailable < 0) bytesAvailable = 0;
	    		if (debugOut) {
					System.out.println("[Read]<= " + bytesRead + " bytes read for original message without socket token+clock piggybacked");
				}
	    		return bytesRead;
	    	}
       }
		
	   lgclock.retrieveClock(in);
	   int nb = in.read(b, off, len);
	   
	   if (usingToken) {
		   lgclock.bytesAvailableSocket -= nb;
		   if (debugOut) { 
			   System.out.println("[Read]<= " + nb + " bytes read for original message with socket token+clock piggybacked");
		   }
	   }
	   return nb;
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}
}
	
