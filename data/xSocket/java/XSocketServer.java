import java.net.InetAddress;   
import java.util.Map;   
import java.util.Map.Entry;   
import java.lang.management.ManagementFactory;   
import org.xsocket.connection.IServer;   
import org.xsocket.connection.Server;   
import org.xsocket.connection.IConnection.FlushMode;   
  
/**  
   
 */  
public class XSocketServer {   
      
    private static final int PORT = 8014;   
       
    public static void main(String[] args) throws Exception {   
        
        InetAddress address=InetAddress.getByName("localhost");   
        //
        IServer srv = new Server(address,PORT,new ServerHandler());   
        //
        srv.setFlushmode(FlushMode.ASYNC);   
       try{   
         
            srv.start(); //   
            System.out.println("Server" + srv.getLocalAddress() +":"+PORT);    
            System.out.println("******************** Server getProcessID = " + getProcessID());  			
            Map<String, Class> maps=srv.getOptions();   
            if(maps!=null){   
                   
                for (Entry<String, Class> entry : maps.entrySet()) {   
                    System.out.println("key= "+entry.getKey()+" value ="+entry.getValue().getName());   
                }   
            }   
            System.out.println("log: " + srv.getStartUpLogMessage());   
               
       }catch(Exception e){   
            System.out.println(e);   
        }
     }
	public static String getProcessID() {
		return ManagementFactory.getRuntimeMXBean().getName()+'\0';
	}          
  
} 
