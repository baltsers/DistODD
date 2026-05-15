package ODD;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ODDCreateStaticGraph {
	static class CallCreateStaticGraph implements Callable <Boolean> {
		@Override
        public Boolean call() throws Exception {
			try {
                Thread.sleep(1000);          
	           	//long startTime = System.currentTimeMillis();
	           	Runtime.getRuntime().exec("./ODDStaticGraph.sh");
				System.out.println("Creating static graph successes");           	
           	//return "" + (System.currentTimeMillis() - startTime);
			  } catch (InterruptedException e) {
				  System.out.println("MyInitialStaticGraph is interrupted when calculating, will stop...");
				  return false; // 
			  }
			return true;
		}
	}	
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        CallCreateStaticGraph task1 = new CallCreateStaticGraph();
        Future<Boolean> f1 = executor.submit(task1);
        try {
        	if (f1.get(100, TimeUnit.MILLISECONDS)) { // 
                System.out.println("CallCreateStaticGraph completes successfully");
            }
        } catch (InterruptedException e) {
            System.out.println("CallCreateStaticGraph was interrupted during the sleeping");
            executor.shutdownNow();
        } catch (ExecutionException e) {
            System.out.println("CallCreateStaticGraph has mistakes during getting the result");
            executor.shutdownNow();
        } catch (TimeoutException e) {
            System.out.println("CallCreateStaticGraph is timeoouts");
            f1.cancel(true);
            // executor.shutdownNow();
            // executor.shutdown();
        } finally {
            executor.shutdownNow();
        }
    }
}
