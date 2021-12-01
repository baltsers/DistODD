//import com.google.common.base.Objects;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**

 */
public class CalculatorClient {

    private static int port = 9090;
    private static String ip = "localhost";
    private static CalculatorService.Client client;

    private static TTransport transport;

    /**
     
     */
    public static TTransport createTTransport(){

        TTransport transport = new TSocket(ip, port);
        return transport;

    }

    /**

     * @param transport
     * @throws TTransportException
     */
    public static void openTTransport(TTransport transport) throws TTransportException {

        transport.open();

    }

    /**

     * @param transport
     */
    public static void closeTTransport(TTransport transport){

        transport.close();

    }

    /**
     * @return
     */
    public static CalculatorService.Client createClient(TTransport transport){


        TProtocol protocol = new TBinaryProtocol(transport);

        CalculatorService.Client client = new CalculatorService.Client(protocol);
        return client;
    }

    public static void main(String[] args) {

        try {

            // 
            transport = createTTransport();

            // 
            openTTransport(transport);

            // 
            client = createClient(transport);

            /*
            System.out.println(client.add(100, 200));
            System.out.println(client.minus(200, 100));
            System.out.println(client.multi(100, 11));
            System.out.println(client.divi(100, 20));
			*/
			int number1 = 1;
			int number2 = 1;
			int number3 = 1;
			 while (true)  {
				number1 = (int)(Math.random()*100)+1;
				number2 = (int)(Math.random()*100)+1;
				number3 = (int)(Math.random()*10)+1;
				System.out.println("client.add("+number1+","+ number2+")="+client.add(number1, number2));
				System.out.println("client.minus("+number1+","+ number2+")="+client.minus(number1, number2));
				System.out.println("client.multi("+number1+","+ number2+")="+client.multi(number1, number2));
				System.out.println("client.divi("+number1+","+ number2+")="+client.divi(number1, number2));
				System.out.println("The client will sleep "+number3+" seconds");
                try  {
                    Thread.sleep(number3*1000);
                }    
                catch (Exception e) 
                {
                    e.printStackTrace();
                }    
			}
        } catch (TException e) {
            e.printStackTrace();
        }
        finally {
            // 
            closeTTransport(transport);
        }
    }
}
