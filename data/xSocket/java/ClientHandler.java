import java.io.IOException;   
import java.nio.BufferUnderflowException;   
import java.nio.channels.ClosedChannelException;   
  
import org.xsocket.MaxReadSizeExceededException;   
import org.xsocket.connection.IConnectHandler;   
import org.xsocket.connection.IDataHandler;   
import org.xsocket.connection.IDisconnectHandler;   
import org.xsocket.connection.INonBlockingConnection;   
/**  

 *  
 */  
public class ClientHandler implements IDataHandler ,IConnectHandler ,IDisconnectHandler {   
  
    /**  
   
     */  
    @Override  
    public boolean onConnect(INonBlockingConnection nbc) throws IOException,   
            BufferUnderflowException, MaxReadSizeExceededException {   
        String  remoteName=nbc.getRemoteAddress().getHostName();   
        System.out.println("remoteName "+remoteName +" has connected ！");   
       return true;   
    }   
    /**  
    
     */  
    @Override  
    public boolean onDisconnect(INonBlockingConnection nbc) throws IOException {   
        // TODO Auto-generated method stub   
       return false;   
    }   
    /**  
     *   
    
     */  
    @Override  
    public boolean onData(INonBlockingConnection nbc) throws IOException,   
            BufferUnderflowException, ClosedChannelException,   
            MaxReadSizeExceededException {   
         String data=nbc.readStringByDelimiter("|");   
         nbc.write("--|Client:receive data from server sucessful| -----");   
         nbc.flush();   
         System.out.println(data);   
         return true;   
    }   
  
}  