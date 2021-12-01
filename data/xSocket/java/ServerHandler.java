import java.io.IOException;   
import java.nio.BufferUnderflowException;   
import java.nio.channels.ClosedChannelException;   
  
import org.xsocket.MaxReadSizeExceededException;   
import org.xsocket.connection.IConnectHandler;   
import org.xsocket.connection.IConnectionTimeoutHandler;   
import org.xsocket.connection.IDataHandler;   
import org.xsocket.connection.IDisconnectHandler;   
import org.xsocket.connection.IIdleTimeoutHandler;   
import org.xsocket.connection.INonBlockingConnection;   
/**  
   
 *  
 */  
public class ServerHandler implements IDataHandler ,IConnectHandler ,IIdleTimeoutHandler ,IConnectionTimeoutHandler,IDisconnectHandler {   
  
    /**  
     
     */  
    @Override  
    public boolean onConnect(INonBlockingConnection nbc) throws IOException,   
            BufferUnderflowException, MaxReadSizeExceededException {   
        String  remoteName=nbc.getRemoteAddress().getHostName();   
        System.out.println("remoteName "+remoteName +" has connected !");   
            return true;   
    }   
    /**  
     
     */  
    @Override  
    public boolean onDisconnect(INonBlockingConnection nbc) throws IOException {   
       return false;   
    }   
    /**  
     
     */  
    @Override  
    public boolean onData(INonBlockingConnection nbc) throws IOException,   
            BufferUnderflowException, ClosedChannelException,   
            MaxReadSizeExceededException {   
         String data=nbc.readStringByDelimiter("|");   
         nbc.write("--|server:receiving data from client is successful !-----");   
         nbc.flush();   
         System.out.println(data);   
         return true;   
    }   
    /**  
    
     */  
    @Override  
    public boolean onIdleTimeout(INonBlockingConnection connection) throws IOException {   
        // TODO Auto-generated method stub   
        return false;   
    }   
    /**  
     
     */  
    @Override  
    public boolean onConnectionTimeout(INonBlockingConnection connection) throws IOException {   
        // TODO Auto-generated method stub   
        return false;   
    }   
  
}  