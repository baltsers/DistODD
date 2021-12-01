import java.io.IOException;   
import java.lang.management.ManagementFactory;    
import org.xsocket.connection.BlockingConnection;   
import org.xsocket.connection.IBlockingConnection;   
import org.xsocket.connection.INonBlockingConnection;   
import org.xsocket.connection.NonBlockingConnection;   
/**  
 
 */  
public class XSocketClient {   
    private static final int PORT = 8014;   
    public static void main(String[] args) throws IOException {   
            //   
            INonBlockingConnection nbc = new NonBlockingConnection("localhost", PORT, new ClientHandler());   
            //IBlockingConnection bc = new BlockingConnection("localhost", PORT);     
           
           IBlockingConnection bc = new BlockingConnection(nbc);   
           //  
            //bc.setEncoding("UTF-8");   
            System.out.println("******************** Client getProcessID = " + getProcessID());  
            bc.setAutoflush(true);   
            bc.write("Client:1\r\n");
			System.out.println("Write1");
			//bc.write("Client:2\r\n");            
			//System.out.println("Write2");
			String res = bc.readStringByDelimiter("\r\n"); 
           //byte[] byteBuffers= bc.readBytesByDelimiter("|", "UTF-8");   
           System.out.println(res);   
           //System.out.println(new String(byteBuffers));   
           //   
           bc.flush();               
			System.out.println("flush()");
           bc.close();                
			System.out.println("close()");
    }   
  
	public static String getProcessID() {
		return ManagementFactory.getRuntimeMXBean().getName()+'\0';
	}
}  
